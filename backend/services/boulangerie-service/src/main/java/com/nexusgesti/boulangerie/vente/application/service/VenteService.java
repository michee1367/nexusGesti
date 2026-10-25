package com.nexusgesti.boulangerie.vente.application.service;

import com.nexusgesti.boulangerie.vente.domain.VenteException;
import com.nexusgesti.boulangerie.vente.domain.model.Acompte;
import com.nexusgesti.boulangerie.vente.domain.model.CanalVente;
import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneCommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneVente;
import com.nexusgesti.boulangerie.vente.domain.model.ModePaiementCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.vente.domain.model.PaiementVente;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.StatutVente;
import com.nexusgesti.boulangerie.vente.domain.model.TypeMouvementStock;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;
import com.nexusgesti.boulangerie.vente.domain.port.in.VenteUseCase;
import com.nexusgesti.boulangerie.vente.domain.port.out.VenteEventPublisher;
import com.nexusgesti.boulangerie.vente.domain.port.out.VenteRepository;
import com.nexusgesti.common.idempotency.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VenteService implements VenteUseCase {

    private final VenteRepository repository;
    private final VenteEventPublisher eventPublisher;
    private final IdempotencyService idempotency;

    public VenteService(VenteRepository repository,
                        VenteEventPublisher eventPublisher,
                        IdempotencyService idempotency) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.idempotency = idempotency;
    }

    // ---------- SESSION ----------
    @Override
    public SessionCaisse ouvrirSession(LocalDate date, BigDecimal fondInitial, UUID ouvertePar) {
        repository.findSessionOuverte().ifPresent(s -> {
            throw VenteException.etatInvalide("Une session de caisse est deja ouverte");
        });
        if (date == null) {
            date = LocalDate.now();
        }
        SessionCaisse session = new SessionCaisse(date, fondInitial, ouvertePar);
        return repository.saveSession(session);
    }

    @Override
    @Transactional(readOnly = true)
    public SessionCaisse getSession(UUID id) {
        return loadSession(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionCaisse> listSessions() {
        return repository.listSessions();
    }

    @Override
    public SessionCaisse cloturerSession(UUID id, BigDecimal montantCompte) {
        SessionCaisse session = loadSession(id);
        session.cloturer(montantCompte);
        SessionCaisse saved = repository.saveSession(session);
        eventPublisher.publierSessionCloturee(saved);
        return saved;
    }

    // ---------- VENTE ----------
    @Override
    public Vente creerVente(Vente vente, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw VenteException.invalide("Idempotency-Key requise");
        }
        return idempotency.getCachedResponse(idempotencyKey, String.class)
                .map(UUID::fromString)
                .map(this::getVente)
                .orElseGet(() -> creerVenteNouveau(vente, idempotencyKey));
    }

    private Vente creerVenteNouveau(Vente vente, String idempotencyKey) {
        if (!idempotency.tryAcquireLock(idempotencyKey)) {
            throw VenteException.idempotenceEnCours("Requete idempotente deja en cours");
        }
        try {
            SessionCaisse session = loadSession(vente.getSessionId());
            if (!session.estOuverte()) {
                throw VenteException.etatInvalide("Impossible de creer une vente sur une session cloturee");
            }
            if (vente.getIdVente() == null) {
                vente.setIdVente(UUID.randomUUID());
            }
            vente.setCleIdempotence(idempotencyKey);
            vente.setDate(LocalDateTime.now());
            vente.setCanal(vente.getCanal() == null ? CanalVente.COMPTOIR : vente.getCanal());
            vente.setModePaiement(vente.getModePaiement() == null ? ModePaiementCaisse.ESPECES : vente.getModePaiement());
            vente.setStatut(StatutVente.EN_COURS);
            vente.setPointsFideliteGagnes(0);
            if (vente.getNumeroTicket() == null || vente.getNumeroTicket().isBlank()) {
                int compteur = repository.countVentesBySession(session.getIdSession()) + 1;
                vente.setNumeroTicket("TKT-" + String.format("%06d", compteur));
            }
            for (LigneVente ligne : vente.getLignes()) {
                ligne.verifier();
                ligne.calculerMontants();
                if (ligne.getId() == null) {
                    ligne.setId(UUID.randomUUID());
                }
            }
            vente.calculerMontants();
            vente.verifierCreation();
            Vente saved = repository.saveVente(vente);
            idempotency.storeResponse(idempotencyKey, saved.getIdVente().toString());
            return saved;
        } finally {
            idempotency.releaseLock(idempotencyKey);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Vente getVente(UUID id) {
        return loadVente(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vente> listVentes(UUID sessionId) {
        return repository.listVentes(sessionId);
    }

    @Override
    public Vente encaisserVente(UUID id, EncaissementRequest request) {
        Vente vente = loadVente(id);
        loadSession(vente.getSessionId());
        PaiementVente paiement = new PaiementVente(
                request.mode() == null ? vente.getModePaiement() : request.mode(),
                request.montant() == null ? vente.getMontantTTC() : request.montant(),
                request.montantRecu(),
                request.referenceExterne());
        paiement.finaliser(vente.getMontantTTC());
        vente.ajouterPaiement(paiement);
        vente.valider();
        vente.calculerMontants();
        SessionCaisse session = loadSession(vente.getSessionId());
        session.encaisser(vente.getMontantTTC());
        repository.saveSession(session);
        Vente saved = repository.saveVente(vente);
        for (LigneVente ligne : saved.getLignes()) {
            eventPublisher.publierMouvementStock(new MouvementStockIntention(
                    TypeMouvementStock.SORTIE_VENTE,
                    ligne.getProduitCode(),
                    ligne.getLotNumero(),
                    ligne.getQuantite(),
                    null,
                    saved.getIdVente().toString()));
        }
        eventPublisher.publierVenteEncaissee(saved);
        return saved;
    }

    @Override
    public Vente annulerVente(UUID id, String motif) {
        Vente vente = loadVente(id);
        vente.annuler(motif);
        Vente saved = repository.saveVente(vente);
        eventPublisher.publierVenteAnnulee(saved, motif);
        return saved;
    }

    @Override
    public Vente rembourserVente(UUID id) {
        Vente vente = loadVente(id);
        vente.rembourser();
        return repository.saveVente(vente);
    }

    // ---------- COMMANDE CLIENT ----------
    @Override
    public CommandeClient creerCommande(CommandeClient commande) {
        if (commande.getIdCommande() == null) {
            commande.setIdCommande(UUID.randomUUID());
        }
        if (commande.getNumero() == null || commande.getNumero().isBlank()) {
            commande.setNumero("CMD-C-" + String.format("%05d", repository.countCommandes() + 1));
        }
        if (commande.getDateCommande() == null) {
            commande.setDateCommande(LocalDate.now());
        }
        commande.getLignes().forEach(ligne -> {
            ligne.verifier();
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
        });
        commande.calculerMontants();
        CommandeClient saved = repository.saveCommande(commande);
        eventPublisher.publierCommandeCreee(saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeClient getCommande(UUID id) {
        return loadCommande(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeClient> listCommandes() {
        return repository.listCommandes();
    }

    @Override
    public CommandeClient ajouterLigneCommande(UUID id, LigneCommandeClient ligne) {
        CommandeClient commande = loadCommande(id);
        commande.ajouterLigne(ligne);
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeClient confirmerCommande(UUID id) {
        CommandeClient commande = loadCommande(id);
        commande.confirmer();
        CommandeClient saved = repository.saveCommande(commande);
        eventPublisher.publierCommandeConfirmee(saved);
        return saved;
    }

    @Override
    public CommandeClient encaisserAcompte(UUID id, AcompteRequest request) {
        CommandeClient commande = loadCommande(id);
        ModePaiementCaisse mode = request.mode() == null ? ModePaiementCaisse.ESPECES : request.mode();
        commande.encaisserAcompte(request.montant(), mode, request.reference());
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeClient demarrerProduction(UUID id) {
        CommandeClient commande = loadCommande(id);
        commande.demarrerProduction();
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeClient marquerPrete(UUID id) {
        CommandeClient commande = loadCommande(id);
        commande.marquerPrete();
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeClient retirerCommande(UUID id) {
        CommandeClient commande = loadCommande(id);
        commande.retirer();
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeClient annulerCommande(UUID id, String motif) {
        CommandeClient commande = loadCommande(id);
        commande.annuler(motif);
        CommandeClient saved = repository.saveCommande(commande);
        eventPublisher.publierCommandeAnnulee(saved, motif);
        return saved;
    }

    private SessionCaisse loadSession(UUID id) {
        return repository.findSession(id)
                .orElseThrow(() -> VenteException.notFound("SessionCaisse", id));
    }

    private Vente loadVente(UUID id) {
        return repository.findVente(id)
                .orElseThrow(() -> VenteException.notFound("Vente", id));
    }

    private CommandeClient loadCommande(UUID id) {
        return repository.findCommande(id)
                .orElseThrow(() -> VenteException.notFound("CommandeClient", id));
    }
}

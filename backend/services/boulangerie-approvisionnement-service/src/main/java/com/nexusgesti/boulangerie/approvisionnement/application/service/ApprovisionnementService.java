package com.nexusgesti.boulangerie.approvisionnement.application.service;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Rapprochement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.TypeMouvementStock;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.in.ApprovisionnementUseCase;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementEventPublisher;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApprovisionnementService implements ApprovisionnementUseCase {

    private final ApprovisionnementRepository repository;
    private final ApprovisionnementEventPublisher eventPublisher;

    public ApprovisionnementService(ApprovisionnementRepository repository,
                                    ApprovisionnementEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    // ---------- FOURNISSEUR ----------

    @Override
    public FournisseurBoulangerie creerFournisseur(FournisseurBoulangerie fournisseur) {
        fournisseur.setIdFournisseur(UUID.randomUUID());
        fournisseur.verifierModifiable();
        return repository.saveFournisseur(fournisseur);
    }

    @Override
    @Transactional(readOnly = true)
    public FournisseurBoulangerie getFournisseur(UUID id) {
        return loadFournisseur(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FournisseurBoulangerie> listFournisseurs() {
        return repository.listFournisseurs();
    }

    @Override
    public FournisseurBoulangerie modifierFournisseur(UUID id, FournisseurBoulangerie fournisseur) {
        FournisseurBoulangerie existant = loadFournisseur(id);
        fournisseur.setIdFournisseur(existant.getIdFournisseur());
        fournisseur.verifierModifiable();
        return repository.saveFournisseur(fournisseur);
    }

    @Override
    public FournisseurBoulangerie changerEtatFournisseur(UUID id, boolean actif) {
        FournisseurBoulangerie fournisseur = loadFournisseur(id);
        if (actif) {
            fournisseur.reactiver();
        } else {
            fournisseur.desactiver();
        }
        return repository.saveFournisseur(fournisseur);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationFournisseur evaluerFournisseur(UUID id) {
        FournisseurBoulangerie fournisseur = loadFournisseur(id);
        int nbCommandes = 0;
        int nbHonorees = 0;
        int nbRefusees = 0;
        for (CommandeAchat commande : repository.listCommandes()) {
            if (!fournisseur.getCode().equals(commande.getFournisseurTiersCode())) {
                continue;
            }
            nbCommandes++;
            for (Reception reception : repository.findReceptionsByCommande(commande.getIdCommande())) {
                if (reception.getStatut() == StatutReception.REFUSEE) {
                    nbRefusees++;
                } else if (reception.estHonoree()) {
                    nbHonorees++;
                }
            }
        }
        return new EvaluationFournisseur(fournisseur.getIdFournisseur(), fournisseur.getCode(),
                fournisseur.isActif(), fournisseur.estFiable(), fournisseur.getDelai(),
                nbCommandes, nbHonorees, nbRefusees);
    }

    // ---------- BESOIN ----------

    @Override
    public List<BesoinApprovisionnement> calculerBesoins(List<SaisieBesoin> saisies) {
        if (saisies == null || saisies.isEmpty()) {
            throw ApprovisionnementException.invalide("Aucun article a evaluer");
        }
        List<BesoinApprovisionnement> besoins = new ArrayList<>();
        for (SaisieBesoin saisie : saisies) {
            if (saisie.articleCode() == null || saisie.articleCode().isBlank()) {
                throw ApprovisionnementException.invalide("L article est obligatoire dans le calcul du besoin");
            }
            besoins.add(BesoinApprovisionnement.calculer(saisie.articleCode(), saisie.libelle(),
                    saisie.quantiteDisponible(), saisie.seuil(), saisie.consommationPrevue(),
                    saisie.delaiReapproJours()));
        }
        List<BesoinApprovisionnement> sousSeuil = besoins.stream()
                .filter(BesoinApprovisionnement::estSousSeuil)
                .toList();
        if (!sousSeuil.isEmpty()) {
            eventPublisher.publierBesoinsSousSeuil(sousSeuil);
        }
        return besoins;
    }

    @Override
    public List<BesoinApprovisionnement> listerSousSeuil(List<SaisieBesoin> saisies) {
        return calculerBesoins(saisies).stream()
                .filter(BesoinApprovisionnement::estSousSeuil)
                .toList();
    }

    // ---------- COMMANDE ----------

    @Override
    public CommandeAchat creerCommande(CommandeAchat commande) {
        if (commande.getIdCommande() == null) {
            commande.setIdCommande(UUID.randomUUID());
        }
        if (commande.getNumero() == null || commande.getNumero().isBlank()) {
            commande.setNumero("CMD-" + String.format("%05d", repository.countCommandes() + 1));
        }
        commande.setStatut(StatutCommandeAchat.BROUILLON);
        commande.getLignes().forEach(ligne -> {
            ligne.setCommandeId(commande.getIdCommande());
            ligne.setQuantiteRecue(BigDecimal.ZERO);
        });
        return repository.saveCommande(commande);
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeAchat getCommande(UUID id) {
        return loadCommande(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeAchat> listCommandes() {
        return repository.listCommandes();
    }

    @Override
    public CommandeAchat validerCommande(UUID id) {
        CommandeAchat commande = loadCommande(id);
        commande.valider();
        CommandeAchat saved = repository.saveCommande(commande);
        eventPublisher.publierCommandeValidee(saved);
        return saved;
    }

    @Override
    public CommandeAchat envoyerCommande(UUID id) {
        CommandeAchat commande = loadCommande(id);
        commande.envoyer();
        return repository.saveCommande(commande);
    }

    @Override
    public CommandeAchat annulerCommande(UUID id, String motif) {
        CommandeAchat commande = loadCommande(id);
        commande.annuler(motif);
        CommandeAchat saved = repository.saveCommande(commande);
        eventPublisher.publierCommandeAnnulee(saved, motif);
        return saved;
    }

    // ---------- RECEPTION ----------

    @Override
    public Reception creerReception(Reception reception) {
        CommandeAchat commande = loadCommande(reception.getCommandeId());
        if (!commande.estRecevable()) {
            throw ApprovisionnementException.etatInvalide(
                    "La commande " + commande.getNumero() + " doit etre envoyee pour etre receptionnee");
        }
        if (reception.getIdReception() == null) {
            reception.setIdReception(UUID.randomUUID());
        }
        if (reception.getNumero() == null || reception.getNumero().isBlank()) {
            reception.setNumero("REC-" + String.format("%05d", repository.countReceptions() + 1));
        }
        reception.setCommandeNumero(commande.getNumero());
        reception.setStatut(StatutReception.ATTENDUE);
        if (reception.getLignes().isEmpty()) {
            throw ApprovisionnementException.invalide("Une reception doit porter au moins une ligne");
        }
        for (LigneReception ligne : reception.getLignes()) {
            LigneCommandeAchat ligneCommande = commande.trouverLigne(ligne.getArticleCode());
            ligne.setQuantiteCommandee(ligneCommande.getQuantiteCommandee());
            ligne.verifier();
            BigDecimal restant = ligneCommande.getResteARecevoir();
            if (ligne.getQuantiteRecue().compareTo(restant) > 0) {
                throw ApprovisionnementException.invalide(
                        "Quantite recue superieure au reste a recevoir pour l article " + ligne.getArticleCode()
                                + " (reste " + restant.toPlainString() + ")");
            }
        }
        return repository.saveReception(reception);
    }

    @Override
    @Transactional(readOnly = true)
    public Reception getReception(UUID id) {
        return loadReception(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reception> listReceptions() {
        return repository.listReceptions();
    }

    @Override
    public Reception controlerReception(UUID id) {
        Reception reception = loadReception(id);
        reception.controler();
        return repository.saveReception(reception);
    }

    @Override
    public Reception accepterReception(UUID id) {
        Reception reception = loadReception(id);
        reception.accepter();
        CommandeAchat commande = loadCommande(reception.getCommandeId());
        for (LigneReception ligne : reception.getLignes()) {
            LigneCommandeAchat ligneCommande = commande.trouverLigne(ligne.getArticleCode());
            ligneCommande.setQuantiteRecue(ligneCommande.getQuantiteRecue().add(ligne.getQuantiteRecue()));
            eventPublisher.publierMouvementStock(new MouvementStockIntention(
                    TypeMouvementStock.ENTREE_ACHAT,
                    ligne.getArticleCode(),
                    ligne.getLotNumero(),
                    ligne.getQuantiteRecue(),
                    ligne.getPrixAchat(),
                    ligne.getId().toString()));
        }
        commande.calculerMontants();
        if (commande.getResteARecevoir().signum() <= 0) {
            commande.marquerReceptionnee();
        } else {
            commande.marquerPartielle();
        }
        repository.saveCommande(commande);
        Reception saved = repository.saveReception(reception);
        eventPublisher.publierReceptionAcceptee(saved);
        return saved;
    }

    @Override
    public Reception refuserReception(UUID id, String motif) {
        Reception reception = loadReception(id);
        reception.refuser(motif);
        Reception saved = repository.saveReception(reception);
        eventPublisher.publierReceptionRefusee(saved, motif);
        return saved;
    }

    // ---------- RAPPROCHEMENT ----------

    @Override
    @Transactional(readOnly = true)
    public Rapprochement rapprocherCommande(UUID commandeId) {
        CommandeAchat commande = loadCommande(commandeId);
        List<Rapprochement.Ecart> ecarts = new ArrayList<>();
        BigDecimal montantReceptions = BigDecimal.ZERO;
        for (Reception reception : repository.findReceptionsByCommande(commandeId)) {
            if (!reception.estHonoree()) {
                continue;
            }
            for (LigneReception ligne : reception.getLignes()) {
                if (!ligne.isConforme()) {
                    continue;
                }
                montantReceptions = montantReceptions.add(
                        ligne.getQuantiteRecue().multiply(ligne.getPrixAchat()));
                LigneCommandeAchat ligneCommande = commande.trouverLigne(ligne.getArticleCode());
                BigDecimal ecart = ligne.getPrixAchat().subtract(ligneCommande.getPrixUnitaire());
                boolean qteDepassee = ligne.getQuantiteRecue().compareTo(ligneCommande.getQuantiteCommandee()) > 0;
                if (ecart.signum() != 0 || qteDepassee) {
                    ecarts.add(new Rapprochement.Ecart(ligne.getArticleCode(),
                            ligneCommande.getQuantiteCommandee(),
                            ligne.getQuantiteRecue(),
                            ligneCommande.getPrixUnitaire(),
                            ligne.getPrixAchat(),
                            ecart));
                }
            }
        }
        return new Rapprochement(commande.getNumero(), commande.getStatut().name(),
                commande.getMontantTTC(), montantReceptions.setScale(2, RoundingMode.HALF_UP),
                ecarts.isEmpty(), ecarts);
    }

    // ---------- HELPERS ----------

    private FournisseurBoulangerie loadFournisseur(UUID id) {
        return repository.findFournisseur(id)
                .orElseThrow(() -> ApprovisionnementException.notFound("Fournisseur", id));
    }

    private CommandeAchat loadCommande(UUID id) {
        return repository.findCommande(id)
                .orElseThrow(() -> ApprovisionnementException.notFound("CommandeAchat", id));
    }

    private Reception loadReception(UUID id) {
        return repository.findReception(id)
                .orElseThrow(() -> ApprovisionnementException.notFound("Reception", id));
    }
}

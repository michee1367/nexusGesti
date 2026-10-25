package com.nexusgesti.boulangerie.vente.adapter.out.persistence;

import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.AcompteEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.CommandeClientEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.LigneCommandeClientEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.LigneVenteEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.PaiementVenteEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.SessionCaisseEntity;
import com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity.VenteEntity;
import com.nexusgesti.boulangerie.vente.domain.model.Acompte;
import com.nexusgesti.boulangerie.vente.domain.model.CanalVente;
import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneCommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneVente;
import com.nexusgesti.boulangerie.vente.domain.model.ModePaiementCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.PaiementVente;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.StatutCommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.StatutVente;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;
import com.nexusgesti.boulangerie.vente.domain.port.out.VenteRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VentePersistenceAdapter implements VenteRepository {

    private final SessionCaisseJpaRepository sessionRepository;
    private final VenteJpaRepository venteRepository;
    private final LigneVenteJpaRepository ligneVenteRepository;
    private final PaiementVenteJpaRepository paiementVenteRepository;
    private final CommandeClientJpaRepository commandeRepository;
    private final LigneCommandeClientJpaRepository ligneCommandeRepository;
    private final AcompteJpaRepository acompteRepository;

    public VentePersistenceAdapter(SessionCaisseJpaRepository sessionRepository,
                                   VenteJpaRepository venteRepository,
                                   LigneVenteJpaRepository ligneVenteRepository,
                                   PaiementVenteJpaRepository paiementVenteRepository,
                                   CommandeClientJpaRepository commandeRepository,
                                   LigneCommandeClientJpaRepository ligneCommandeRepository,
                                   AcompteJpaRepository acompteRepository) {
        this.sessionRepository = sessionRepository;
        this.venteRepository = venteRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.paiementVenteRepository = paiementVenteRepository;
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.acompteRepository = acompteRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- SESSION DE CAISSE ----------

    @Override
    @Transactional
    public SessionCaisse saveSession(SessionCaisse session) {
        if (session.getIdSession() == null) {
            session.setIdSession(UUID.randomUUID());
        }
        SessionCaisseEntity entity = new SessionCaisseEntity();
        entity.setId(session.getIdSession());
        entity.setDate(session.getDate());
        entity.setFondInitial(session.getFondInitial());
        entity.setTotalEncaisse(session.getTotalEncaisse());
        entity.setEcart(session.getEcart());
        entity.setOuvertePar(session.getOuvertePar());
        entity.setCloturee(session.isCloturee());
        entity.setDateCloture(session.getDateCloture());
        entity.setTenantId(tenant());
        sessionRepository.save(entity);
        return session;
    }

    @Override
    public Optional<SessionCaisse> findSession(UUID id) {
        return sessionRepository.findById(id).map(this::toDomainSession);
    }

    @Override
    public Optional<SessionCaisse> findSessionOuverte() {
        return sessionRepository.findFirstByClotureeFalse().map(this::toDomainSession);
    }

    @Override
    public List<SessionCaisse> listSessions() {
        List<SessionCaisse> out = new ArrayList<>();
        sessionRepository.findAll().forEach(e -> out.add(toDomainSession(e)));
        return out;
    }

    // ---------- VENTE ----------

    @Override
    @Transactional
    public Vente saveVente(Vente vente) {
        if (vente.getIdVente() == null) {
            vente.setIdVente(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneVenteRepository.deleteByVenteId(vente.getIdVente());
        paiementVenteRepository.deleteByVenteId(vente.getIdVente());

        VenteEntity entity = new VenteEntity();
        entity.setId(vente.getIdVente());
        entity.setNumeroTicket(vente.getNumeroTicket());
        entity.setDate(vente.getDate());
        entity.setSessionId(vente.getSessionId());
        entity.setCanal(vente.getCanal().name());
        entity.setClientCode(vente.getClientCode());
        entity.setMontantHT(vente.getMontantHT());
        entity.setMontantTVA(vente.getMontantTVA());
        entity.setMontantTTC(vente.getMontantTTC());
        entity.setModePaiement(vente.getModePaiement().name());
        entity.setPointsFideliteGagnes(vente.getPointsFideliteGagnes());
        entity.setCleIdempotence(vente.getCleIdempotence());
        entity.setStatut(vente.getStatut().name());
        entity.setMotifAnnulation(vente.getMotifAnnulation());
        entity.setTenantId(tenantId);
        venteRepository.save(entity);

        for (LigneVente ligne : vente.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            LigneVenteEntity ligneEntity = new LigneVenteEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setVenteId(vente.getIdVente());
            ligneEntity.setProduitCode(ligne.getProduitCode());
            ligneEntity.setDesignation(ligne.getDesignation());
            ligneEntity.setQuantite(ligne.getQuantite());
            ligneEntity.setPrixUnitaireHT(ligne.getPrixUnitaireHT());
            ligneEntity.setTauxTva(ligne.getTauxTva());
            ligneEntity.setRemisePct(ligne.getRemisePct());
            ligneEntity.setMontantHT(ligne.getMontantHT());
            ligneEntity.setMontantTVA(ligne.getMontantTVA());
            ligneEntity.setLotNumero(ligne.getLotNumero());
            ligneEntity.setTenantId(tenantId);
            ligneVenteRepository.save(ligneEntity);
        }
        for (PaiementVente paiement : vente.getPaiements()) {
            if (paiement.getId() == null) {
                paiement.setId(UUID.randomUUID());
            }
            PaiementVenteEntity paiementEntity = new PaiementVenteEntity();
            paiementEntity.setId(paiement.getId());
            paiementEntity.setVenteId(vente.getIdVente());
            paiementEntity.setMode(paiement.getMode().name());
            paiementEntity.setMontant(paiement.getMontant());
            paiementEntity.setDevise(paiement.getDevise());
            paiementEntity.setMontantRecu(paiement.getMontantRecu());
            paiementEntity.setRendu(paiement.getRendu());
            paiementEntity.setReferenceExterne(paiement.getReferenceExterne());
            paiementEntity.setTenantId(tenantId);
            paiementVenteRepository.save(paiementEntity);
        }
        return vente;
    }

    @Override
    public Optional<Vente> findVente(UUID id) {
        return venteRepository.findById(id).map(this::toDomainVente);
    }

    @Override
    public List<Vente> listVentes(UUID sessionId) {
        List<Vente> out = new ArrayList<>();
        if (sessionId == null) {
            venteRepository.findAll().forEach(e -> out.add(toDomainVente(e)));
        } else {
            venteRepository.findBySessionIdOrderByDateAsc(sessionId).forEach(e -> out.add(toDomainVente(e)));
        }
        return out;
    }

    @Override
    public int countVentesBySession(UUID sessionId) {
        return (int) venteRepository.countBySessionId(sessionId);
    }

    // ---------- COMMANDE CLIENT ----------

    @Override
    @Transactional
    public CommandeClient saveCommande(CommandeClient commande) {
        if (commande.getIdCommande() == null) {
            commande.setIdCommande(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneCommandeRepository.deleteByCommandeId(commande.getIdCommande());
        acompteRepository.deleteByCommandeId(commande.getIdCommande());

        CommandeClientEntity entity = new CommandeClientEntity();
        entity.setId(commande.getIdCommande());
        entity.setNumero(commande.getNumero());
        entity.setClientCode(commande.getClientCode());
        entity.setDateCommande(commande.getDateCommande());
        entity.setDateRetrait(commande.getDateRetrait());
        entity.setMontantHT(commande.getMontantHT());
        entity.setMontantTTC(commande.getMontantTTC());
        entity.setAcompte(commande.getAcompte());
        entity.setStatut(commande.getStatut().name());
        entity.setMotifAnnulation(commande.getMotifAnnulation());
        entity.setTenantId(tenantId);
        commandeRepository.save(entity);

        for (LigneCommandeClient ligne : commande.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            LigneCommandeClientEntity ligneEntity = new LigneCommandeClientEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setCommandeId(commande.getIdCommande());
            ligneEntity.setProduitCode(ligne.getProduitCode());
            ligneEntity.setDesignation(ligne.getDesignation());
            ligneEntity.setQuantite(ligne.getQuantite());
            ligneEntity.setPrixUnitaire(ligne.getPrixUnitaire());
            ligneEntity.setPersonnalisation(ligne.getPersonnalisation());
            ligneEntity.setTenantId(tenantId);
            ligneCommandeRepository.save(ligneEntity);
        }
        for (Acompte acompte : commande.getAcomptes()) {
            if (acompte.getId() == null) {
                acompte.setId(UUID.randomUUID());
            }
            AcompteEntity acompteEntity = new AcompteEntity();
            acompteEntity.setId(acompte.getId());
            acompteEntity.setCommandeId(commande.getIdCommande());
            acompteEntity.setMontant(acompte.getMontant());
            acompteEntity.setDate(acompte.getDate());
            acompteEntity.setMode(acompte.getMode().name());
            acompteEntity.setReference(acompte.getReference());
            acompteEntity.setTenantId(tenantId);
            acompteRepository.save(acompteEntity);
        }
        return commande;
    }

    @Override
    public Optional<CommandeClient> findCommande(UUID id) {
        return commandeRepository.findById(id).map(this::toDomainCommande);
    }

    @Override
    public List<CommandeClient> listCommandes() {
        List<CommandeClient> out = new ArrayList<>();
        commandeRepository.findAll().forEach(e -> out.add(toDomainCommande(e)));
        return out;
    }

    @Override
    public int countCommandes() {
        return (int) commandeRepository.count();
    }

    // ---------- MAPPING ----------

    private SessionCaisse toDomainSession(SessionCaisseEntity entity) {
        SessionCaisse session = new SessionCaisse();
        session.setIdSession(entity.getId());
        session.setDate(entity.getDate());
        session.setFondInitial(entity.getFondInitial());
        session.setTotalEncaisse(entity.getTotalEncaisse());
        session.setEcart(entity.getEcart());
        session.setOuvertePar(entity.getOuvertePar());
        session.setCloturee(Boolean.TRUE.equals(entity.getCloturee()));
        session.setDateCloture(entity.getDateCloture());
        return session;
    }

    private Vente toDomainVente(VenteEntity entity) {
        Vente vente = new Vente();
        vente.setIdVente(entity.getId());
        vente.setNumeroTicket(entity.getNumeroTicket());
        vente.setDate(entity.getDate());
        vente.setSessionId(entity.getSessionId());
        vente.setCanal(CanalVente.valueOf(entity.getCanal()));
        vente.setClientCode(entity.getClientCode());
        vente.setMontantHT(entity.getMontantHT());
        vente.setMontantTVA(entity.getMontantTVA());
        vente.setMontantTTC(entity.getMontantTTC());
        vente.setModePaiement(ModePaiementCaisse.valueOf(entity.getModePaiement()));
        vente.setPointsFideliteGagnes(entity.getPointsFideliteGagnes());
        vente.setCleIdempotence(entity.getCleIdempotence());
        vente.setStatut(StatutVente.valueOf(entity.getStatut()));
        vente.setMotifAnnulation(entity.getMotifAnnulation());

        List<LigneVente> lignes = new ArrayList<>();
        for (LigneVenteEntity ligne : ligneVenteRepository.findByVenteId(entity.getId())) {
            LigneVente model = new LigneVente();
            model.setId(ligne.getId());
            model.setProduitCode(ligne.getProduitCode());
            model.setDesignation(ligne.getDesignation());
            model.setQuantite(ligne.getQuantite());
            model.setPrixUnitaireHT(ligne.getPrixUnitaireHT());
            model.setTauxTva(ligne.getTauxTva());
            model.setRemisePct(ligne.getRemisePct());
            model.setMontantHT(ligne.getMontantHT());
            model.setMontantTVA(ligne.getMontantTVA());
            model.setLotNumero(ligne.getLotNumero());
            lignes.add(model);
        }
        vente.setLignes(lignes);

        List<PaiementVente> paiements = new ArrayList<>();
        for (PaiementVenteEntity paiement : paiementVenteRepository.findByVenteId(entity.getId())) {
            PaiementVente model = new PaiementVente();
            model.setId(paiement.getId());
            model.setMode(ModePaiementCaisse.valueOf(paiement.getMode()));
            model.setMontant(paiement.getMontant());
            model.setDevise(paiement.getDevise());
            model.setMontantRecu(paiement.getMontantRecu());
            model.setRendu(paiement.getRendu());
            model.setReferenceExterne(paiement.getReferenceExterne());
            paiements.add(model);
        }
        vente.setPaiements(paiements);
        return vente;
    }

    private CommandeClient toDomainCommande(CommandeClientEntity entity) {
        CommandeClient commande = new CommandeClient();
        commande.setIdCommande(entity.getId());
        commande.setNumero(entity.getNumero());
        commande.setClientCode(entity.getClientCode());
        commande.setDateCommande(entity.getDateCommande());
        commande.setDateRetrait(entity.getDateRetrait());
        commande.setMontantHT(entity.getMontantHT());
        commande.setMontantTTC(entity.getMontantTTC());
        commande.setAcompte(entity.getAcompte());
        commande.setStatut(StatutCommandeClient.valueOf(entity.getStatut()));
        commande.setMotifAnnulation(entity.getMotifAnnulation());

        List<LigneCommandeClient> lignes = new ArrayList<>();
        for (LigneCommandeClientEntity ligne : ligneCommandeRepository.findByCommandeId(entity.getId())) {
            LigneCommandeClient model = new LigneCommandeClient();
            model.setId(ligne.getId());
            model.setProduitCode(ligne.getProduitCode());
            model.setDesignation(ligne.getDesignation());
            model.setQuantite(ligne.getQuantite());
            model.setPrixUnitaire(ligne.getPrixUnitaire());
            model.setPersonnalisation(ligne.getPersonnalisation());
            lignes.add(model);
        }
        commande.setLignes(lignes);

        List<Acompte> acomptes = new ArrayList<>();
        for (AcompteEntity acompte : acompteRepository.findByCommandeId(entity.getId())) {
            Acompte model = new Acompte();
            model.setId(acompte.getId());
            model.setMontant(acompte.getMontant());
            model.setDate(acompte.getDate());
            model.setMode(ModePaiementCaisse.valueOf(acompte.getMode()));
            model.setReference(acompte.getReference());
            acomptes.add(model);
        }
        commande.setAcomptes(acomptes);
        return commande;
    }
}

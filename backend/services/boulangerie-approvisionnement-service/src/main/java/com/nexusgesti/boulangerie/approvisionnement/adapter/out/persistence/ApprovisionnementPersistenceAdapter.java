package com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence;

import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.CommandeAchatEntity;
import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.FournisseurEntity;
import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.LigneCommandeAchatEntity;
import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.LigneReceptionEntity;
import com.nexusgesti.boulangerie.approvisionnement.adapter.out.persistence.entity.ReceptionEntity;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.LigneReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutCommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.StatutReception;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.out.ApprovisionnementRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApprovisionnementPersistenceAdapter implements ApprovisionnementRepository {

    private final FournisseurJpaRepository fournisseurRepository;
    private final CommandeAchatJpaRepository commandeRepository;
    private final LigneCommandeAchatJpaRepository ligneCommandeRepository;
    private final ReceptionJpaRepository receptionRepository;
    private final LigneReceptionJpaRepository ligneReceptionRepository;

    public ApprovisionnementPersistenceAdapter(FournisseurJpaRepository fournisseurRepository,
                                               CommandeAchatJpaRepository commandeRepository,
                                               LigneCommandeAchatJpaRepository ligneCommandeRepository,
                                               ReceptionJpaRepository receptionRepository,
                                               LigneReceptionJpaRepository ligneReceptionRepository) {
        this.fournisseurRepository = fournisseurRepository;
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.receptionRepository = receptionRepository;
        this.ligneReceptionRepository = ligneReceptionRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- FOURNISSEUR ----------

    @Override
    @Transactional
    public FournisseurBoulangerie saveFournisseur(FournisseurBoulangerie fournisseur) {
        if (fournisseur.getIdFournisseur() == null) {
            fournisseur.setIdFournisseur(UUID.randomUUID());
        }
        FournisseurEntity entity = new FournisseurEntity();
        entity.setId(fournisseur.getIdFournisseur());
        entity.setCode(fournisseur.getCode());
        entity.setTiersCode(fournisseur.getTiersCode());
        entity.setRaisonSociale(fournisseur.getRaisonSociale());
        entity.setDelaiLivraisonJours(fournisseur.getDelaiLivraisonJours());
        entity.setConditionsReglement(fournisseur.getConditionsReglement());
        entity.setCategorieProduits(fournisseur.getCategorieProduits());
        entity.setMontantMiniCommande(fournisseur.getMontantMiniCommande());
        entity.setActif(fournisseur.isActif());
        entity.setTenantId(tenant());
        fournisseurRepository.save(entity);
        return fournisseur;
    }

    @Override
    public Optional<FournisseurBoulangerie> findFournisseur(UUID id) {
        return fournisseurRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<FournisseurBoulangerie> listFournisseurs() {
        List<FournisseurBoulangerie> out = new ArrayList<>();
        fournisseurRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- COMMANDE ----------

    @Override
    @Transactional
    public CommandeAchat saveCommande(CommandeAchat commande) {
        if (commande.getIdCommande() == null) {
            commande.setIdCommande(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneCommandeRepository.deleteByCommandeId(commande.getIdCommande());

        CommandeAchatEntity entity = new CommandeAchatEntity();
        entity.setId(commande.getIdCommande());
        entity.setNumero(commande.getNumero());
        entity.setFournisseurTiersCode(commande.getFournisseurTiersCode());
        entity.setDate(commande.getDate());
        entity.setDateLivraisonPrevue(commande.getDateLivraisonPrevue());
        entity.setStatut(commande.getStatut().name());
        entity.setMontantHT(commande.getMontantHT());
        entity.setMontantTVA(commande.getMontantTVA());
        entity.setMontantTTC(commande.getMontantTTC());
        entity.setDevise(commande.getDevise());
        entity.setMotifAnnulation(commande.getMotifAnnulation());
        entity.setTenantId(tenantId);
        commandeRepository.save(entity);

        for (LigneCommandeAchat ligne : commande.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setCommandeId(commande.getIdCommande());
            LigneCommandeAchatEntity ligneEntity = new LigneCommandeAchatEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setCommandeId(commande.getIdCommande());
            ligneEntity.setArticleCode(ligne.getArticleCode());
            ligneEntity.setDesignation(ligne.getDesignation());
            ligneEntity.setQuantiteCommandee(ligne.getQuantiteCommandee());
            ligneEntity.setQuantiteRecue(ligne.getQuantiteRecue());
            ligneEntity.setPrixUnitaire(ligne.getPrixUnitaire());
            ligneEntity.setTauxTva(ligne.getTauxTva());
            ligneEntity.setMontantHT(ligne.getMontantHT());
            ligneEntity.setTenantId(tenantId);
            ligneCommandeRepository.save(ligneEntity);
        }
        return commande;
    }

    @Override
    public Optional<CommandeAchat> findCommande(UUID id) {
        return commandeRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CommandeAchat> listCommandes() {
        List<CommandeAchat> out = new ArrayList<>();
        commandeRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- RECEPTION ----------

    @Override
    @Transactional
    public Reception saveReception(Reception reception) {
        if (reception.getIdReception() == null) {
            reception.setIdReception(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneReceptionRepository.deleteByReceptionId(reception.getIdReception());

        ReceptionEntity entity = new ReceptionEntity();
        entity.setId(reception.getIdReception());
        entity.setNumero(reception.getNumero());
        entity.setCommandeId(reception.getCommandeId());
        entity.setCommandeNumero(reception.getCommandeNumero());
        entity.setDate(reception.getDate());
        entity.setRecuPar(reception.getRecuPar());
        entity.setBonLivraison(reception.getBonLivraison());
        entity.setStatut(reception.getStatut().name());
        entity.setMotif(reception.getMotif());
        entity.setTenantId(tenantId);
        receptionRepository.save(entity);

        for (LigneReception ligne : reception.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setReceptionId(reception.getIdReception());
            LigneReceptionEntity ligneEntity = new LigneReceptionEntity();
            ligneEntity.setId(ligne.getId());
            ligneEntity.setReceptionId(reception.getIdReception());
            ligneEntity.setArticleCode(ligne.getArticleCode());
            ligneEntity.setQuantiteCommandee(ligne.getQuantiteCommandee());
            ligneEntity.setQuantiteRecue(ligne.getQuantiteRecue());
            ligneEntity.setPrixAchat(ligne.getPrixAchat());
            ligneEntity.setLotNumero(ligne.getLotNumero());
            ligneEntity.setDatePeremption(ligne.getDatePeremption());
            ligneEntity.setConforme(ligne.isConforme());
            ligneEntity.setMotifNonConformite(ligne.getMotifNonConformite());
            ligneEntity.setTenantId(tenantId);
            ligneReceptionRepository.save(ligneEntity);
        }
        return reception;
    }

    @Override
    public Optional<Reception> findReception(UUID id) {
        return receptionRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Reception> listReceptions() {
        List<Reception> out = new ArrayList<>();
        receptionRepository.findAll().forEach(e -> out.add(toDomain(e)));
        return out;
    }

    @Override
    public List<Reception> findReceptionsByCommande(UUID commandeId) {
        List<Reception> out = new ArrayList<>();
        receptionRepository.findByCommandeId(commandeId).forEach(e -> out.add(toDomain(e)));
        return out;
    }

    // ---------- COMPTEURS ----------

    @Override
    public int countCommandes() {
        return (int) commandeRepository.count();
    }

    @Override
    public int countReceptions() {
        return (int) receptionRepository.count();
    }

    // ---------- MAPPING ----------

    private FournisseurBoulangerie toDomain(FournisseurEntity entity) {
        FournisseurBoulangerie fournisseur = new FournisseurBoulangerie();
        fournisseur.setIdFournisseur(entity.getId());
        fournisseur.setCode(entity.getCode());
        fournisseur.setTiersCode(entity.getTiersCode());
        fournisseur.setRaisonSociale(entity.getRaisonSociale());
        fournisseur.setDelaiLivraisonJours(entity.getDelaiLivraisonJours());
        fournisseur.setConditionsReglement(entity.getConditionsReglement());
        fournisseur.setCategorieProduits(entity.getCategorieProduits());
        fournisseur.setMontantMiniCommande(entity.getMontantMiniCommande());
        fournisseur.setActif(Boolean.TRUE.equals(entity.getActif()));
        return fournisseur;
    }

    private CommandeAchat toDomain(CommandeAchatEntity entity) {
        CommandeAchat commande = new CommandeAchat();
        commande.setIdCommande(entity.getId());
        commande.setNumero(entity.getNumero());
        commande.setFournisseurTiersCode(entity.getFournisseurTiersCode());
        commande.setDate(entity.getDate());
        commande.setDateLivraisonPrevue(entity.getDateLivraisonPrevue());
        commande.setStatut(entity.getStatut() == null ? null : StatutCommandeAchat.valueOf(entity.getStatut()));
        commande.setMontantHT(entity.getMontantHT());
        commande.setMontantTVA(entity.getMontantTVA());
        commande.setMontantTTC(entity.getMontantTTC());
        commande.setDevise(entity.getDevise());
        commande.setMotifAnnulation(entity.getMotifAnnulation());

        List<LigneCommandeAchat> lignes = new ArrayList<>();
        for (LigneCommandeAchatEntity ligne : ligneCommandeRepository.findByCommandeId(entity.getId())) {
            LigneCommandeAchat model = new LigneCommandeAchat();
            model.setId(ligne.getId());
            model.setCommandeId(ligne.getCommandeId());
            model.setArticleCode(ligne.getArticleCode());
            model.setDesignation(ligne.getDesignation());
            model.setQuantiteCommandee(ligne.getQuantiteCommandee());
            model.setQuantiteRecue(ligne.getQuantiteRecue());
            model.setPrixUnitaire(ligne.getPrixUnitaire());
            model.setTauxTva(ligne.getTauxTva());
            lignes.add(model);
        }
        commande.setLignes(lignes);
        return commande;
    }

    private Reception toDomain(ReceptionEntity entity) {
        Reception reception = new Reception();
        reception.setIdReception(entity.getId());
        reception.setNumero(entity.getNumero());
        reception.setCommandeId(entity.getCommandeId());
        reception.setCommandeNumero(entity.getCommandeNumero());
        reception.setDate(entity.getDate());
        reception.setRecuPar(entity.getRecuPar());
        reception.setBonLivraison(entity.getBonLivraison());
        reception.setStatut(entity.getStatut() == null ? null : StatutReception.valueOf(entity.getStatut()));
        reception.setMotif(entity.getMotif());

        List<LigneReception> lignes = new ArrayList<>();
        for (LigneReceptionEntity ligne : ligneReceptionRepository.findByReceptionId(entity.getId())) {
            LigneReception model = new LigneReception();
            model.setId(ligne.getId());
            model.setReceptionId(ligne.getReceptionId());
            model.setArticleCode(ligne.getArticleCode());
            model.setQuantiteCommandee(ligne.getQuantiteCommandee());
            model.setQuantiteRecue(ligne.getQuantiteRecue());
            model.setPrixAchat(ligne.getPrixAchat());
            model.setLotNumero(ligne.getLotNumero());
            model.setDatePeremption(ligne.getDatePeremption());
            model.setConforme(Boolean.TRUE.equals(ligne.getConforme()));
            model.setMotifNonConformite(ligne.getMotifNonConformite());
            lignes.add(model);
        }
        reception.setLignes(lignes);
        return reception;
    }
}

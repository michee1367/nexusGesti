package com.nexusgesti.people.adapter.out.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.people.adapter.out.persistence.entity.*;
import com.nexusgesti.people.domain.model.*;
import com.nexusgesti.people.domain.port.out.PeopleRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PeoplePersistenceAdapter implements PeopleRepository {

    private final EleveJpaRepository eleveJpaRepository;
    private final DossierEleveJpaRepository dossierJpaRepository;
    private final FichierJointJpaRepository fichierJpaRepository;
    private final InscriptionJpaRepository inscriptionJpaRepository;
    private final CongeEtudeJpaRepository congeJpaRepository;
    private final SanctionDisciplinaireJpaRepository sanctionJpaRepository;
    private final ParentJpaRepository parentJpaRepository;
    private final LienParentaliteJpaRepository lienJpaRepository;
    private final ServiceEtablissementJpaRepository serviceJpaRepository;
    private final AgentJpaRepository agentJpaRepository;
    private final EnseignantJpaRepository enseignantJpaRepository;
    private final PieceIdentiteEnseignantJpaRepository pieceJpaRepository;
    private final AffectationEnseignantJpaRepository affectationJpaRepository;
    private final TitulariteJpaRepository titulariteJpaRepository;
    private final ObjectMapper objectMapper;

    public PeoplePersistenceAdapter(EleveJpaRepository eleveJpaRepository,
                                    DossierEleveJpaRepository dossierJpaRepository,
                                    FichierJointJpaRepository fichierJpaRepository,
                                    InscriptionJpaRepository inscriptionJpaRepository,
                                    CongeEtudeJpaRepository congeJpaRepository,
                                    SanctionDisciplinaireJpaRepository sanctionJpaRepository,
                                    ParentJpaRepository parentJpaRepository,
                                    LienParentaliteJpaRepository lienJpaRepository,
                                    ServiceEtablissementJpaRepository serviceJpaRepository,
                                    AgentJpaRepository agentJpaRepository,
                                    EnseignantJpaRepository enseignantJpaRepository,
                                    PieceIdentiteEnseignantJpaRepository pieceJpaRepository,
                                    AffectationEnseignantJpaRepository affectationJpaRepository,
                                    TitulariteJpaRepository titulariteJpaRepository,
                                    ObjectMapper objectMapper) {
        this.eleveJpaRepository = eleveJpaRepository;
        this.dossierJpaRepository = dossierJpaRepository;
        this.fichierJpaRepository = fichierJpaRepository;
        this.inscriptionJpaRepository = inscriptionJpaRepository;
        this.congeJpaRepository = congeJpaRepository;
        this.sanctionJpaRepository = sanctionJpaRepository;
        this.parentJpaRepository = parentJpaRepository;
        this.lienJpaRepository = lienJpaRepository;
        this.serviceJpaRepository = serviceJpaRepository;
        this.agentJpaRepository = agentJpaRepository;
        this.enseignantJpaRepository = enseignantJpaRepository;
        this.pieceJpaRepository = pieceJpaRepository;
        this.affectationJpaRepository = affectationJpaRepository;
        this.titulariteJpaRepository = titulariteJpaRepository;
        this.objectMapper = objectMapper;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- ELEVE ----------

    @Override
    @Transactional
    public Eleve saveEleve(Eleve eleve) {
        if (eleve.getIdEleve() == null) {
            eleve.setIdEleve(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        eleveJpaRepository.save(eleveToEntity(eleve, tenantId));

        if (eleve.getDossier() != null) {
            saveDossier(eleve.getIdEleve(), eleve.getDossier(), tenantId);
        }

        inscriptionJpaRepository.deleteByEleveId(eleve.getIdEleve());
        for (Inscription i : eleve.getInscriptions()) {
            if (i.getIdInscription() == null) {
                i.setIdInscription(UUID.randomUUID());
            }
            i.setEleveId(eleve.getIdEleve());
            inscriptionJpaRepository.save(inscriptionToEntity(i, tenantId));
        }

        congeJpaRepository.deleteByEleveId(eleve.getIdEleve());
        for (CongeEtude c : eleve.getConges()) {
            if (c.getId() == null) {
                c.setId(UUID.randomUUID());
            }
            c.setEleveId(eleve.getIdEleve());
            congeJpaRepository.save(congeToEntity(c, tenantId));
        }

        sanctionJpaRepository.deleteByEleveId(eleve.getIdEleve());
        for (SanctionDisciplinaire s : eleve.getSanctions()) {
            if (s.getId() == null) {
                s.setId(UUID.randomUUID());
            }
            s.setEleveId(eleve.getIdEleve());
            sanctionJpaRepository.save(sanctionToEntity(s, tenantId));
        }
        return findEleve(eleve.getIdEleve()).orElseThrow();
    }

    private void saveDossier(UUID eleveId, DossierEleve dossier, UUID tenantId) {
        if (dossier.getId() == null) {
            dossier.setId(UUID.randomUUID());
        }
        dossier.setEleveId(eleveId);
        DossierEleveEntity de = new DossierEleveEntity();
        de.setId(dossier.getId());
        de.setEleveId(eleveId);
        de.setReference(dossier.getReference());
        de.setDateOuverture(dossier.getDateOuverture());
        de.setStatut(dossier.getStatut() == null ? null : dossier.getStatut().name());
        de.setTenantId(tenantId);
        dossierJpaRepository.save(de);

        fichierJpaRepository.deleteByDossierId(dossier.getId());
        for (FichierJoint f : dossier.getPieces()) {
            if (f.getId() == null) {
                f.setId(UUID.randomUUID());
            }
            FichierJointEntity fe = new FichierJointEntity();
            fe.setId(f.getId());
            fe.setDossierId(dossier.getId());
            fe.setCategorie(f.getCategorie() == null ? null : f.getCategorie().name());
            fe.setNom(f.getNom());
            fe.setTypeMime(f.getTypeMime());
            fe.setTaille(f.getTaille());
            fe.setUri(f.getUri());
            fe.setDateDepot(f.getDateDepot());
            fe.setDeposePar(f.getDeposePar());
            fe.setDocumentOfficiel(f.isDocumentOfficiel());
            fe.setTenantId(tenantId);
            fichierJpaRepository.save(fe);
        }
    }

    @Override
    public Optional<Eleve> findEleve(UUID id) {
        return eleveJpaRepository.findById(id).map(this::eleveToDomain);
    }

    @Override
    public List<Eleve> listEleves() {
        return eleveJpaRepository.findAll().stream().map(this::eleveToDomain).toList();
    }

    // ---------- PARENT ----------

    @Override
    public Parent saveParent(Parent parent) {
        if (parent.getId() == null) {
            parent.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ParentEntity e = new ParentEntity();
        e.setId(parent.getId());
        e.setMatricule(parent.getMatricule());
        e.setNom(parent.getNom());
        e.setPostNom(parent.getPostNom());
        e.setPrenom(parent.getPrenom());
        e.setSexe(parent.getSexe() == null ? null : parent.getSexe().name());
        e.setTelephone(parent.getTelephone());
        e.setTelephoneSecondaire(parent.getTelephoneSecondaire());
        e.setEmail(parent.getEmail());
        applyAdresse(parent.getAdresse(), e::setAdresseRue, e::setAdresseQuartier, e::setAdresseCommune,
                e::setAdresseVille, e::setAdresseProvince, e::setAdressePays, e::setAdresseTelephone);
        e.setProfession(parent.getProfession());
        e.setStatut(parent.getStatut());
        e.setTenantId(tenantId);
        parentJpaRepository.save(e);
        return parent;
    }

    @Override
    public Optional<Parent> findParent(UUID id) {
        return parentJpaRepository.findById(id).map(this::parentToDomain);
    }

    @Override
    public List<Parent> listParents() {
        return parentJpaRepository.findAll().stream().map(this::parentToDomain).toList();
    }

    // ---------- LIEN PARENTALITE ----------

    @Override
    public LienParentalite saveLien(LienParentalite lien) {
        if (lien.getId() == null) {
            lien.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        LienParentaliteEntity e = new LienParentaliteEntity();
        e.setId(lien.getId());
        e.setEleveId(lien.getEleveId());
        e.setParentId(lien.getParentId());
        e.setType(lien.getType() == null ? null : lien.getType().name());
        e.setPrincipal(lien.isPrincipal());
        e.setPayeur(lien.isPayeur());
        e.setReceveurNotifications(lien.isReceveurNotifications());
        e.setDateDebut(lien.getDateDebut());
        e.setTenantId(tenantId);
        lienJpaRepository.save(e);
        return lien;
    }

    @Override
    public List<LienParentalite> listLiens(UUID eleveId) {
        return lienJpaRepository.findByEleveId(eleveId).stream().map(this::lienToDomain).toList();
    }

    // ---------- SERVICE ETABLISSEMENT ----------

    @Override
    public ServiceEtablissement saveService(ServiceEtablissement service) {
        if (service.getId() == null) {
            service.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ServiceEtablissementEntity e = serviceJpaRepository.findById(service.getId())
                .orElseGet(ServiceEtablissementEntity::new);
        e.setId(service.getId());
        e.setCode(service.getCode());
        e.setLibelle(service.getLibelle());
        e.setType(service.getType() == null ? null : service.getType().name());
        e.setParentId(service.getParentId());
        e.setResponsableId(service.getResponsableId());
        e.setEmail(service.getEmail());
        e.setTenantId(tenantId);
        serviceJpaRepository.save(e);
        return service;
    }

    @Override
    public Optional<ServiceEtablissement> findService(UUID id) {
        return serviceJpaRepository.findById(id).map(this::serviceToDomain);
    }

    @Override
    public List<ServiceEtablissement> listServices() {
        return serviceJpaRepository.findAll().stream().map(this::serviceToDomain).toList();
    }

    // ---------- AGENT ----------

    @Override
    public Agent saveAgent(Agent agent) {
        if (agent.getId() == null) {
            agent.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        AgentEntity e = new AgentEntity();
        e.setId(agent.getId());
        e.setMatricule(agent.getMatricule());
        e.setNom(agent.getNom());
        e.setPostNom(agent.getPostNom());
        e.setPrenom(agent.getPrenom());
        e.setSexe(agent.getSexe() == null ? null : agent.getSexe().name());
        e.setDateNaissance(agent.getDateNaissance());
        e.setTelephone(agent.getTelephone());
        e.setEmail(agent.getEmail());
        applyAdresse(agent.getAdresse(), e::setAdresseRue, e::setAdresseQuartier, e::setAdresseCommune,
                e::setAdresseVille, e::setAdresseProvince, e::setAdressePays, e::setAdresseTelephone);
        e.setFonctionOfficielle(agent.getFonctionOfficielle());
        e.setDateRecrutement(agent.getDateRecrutement());
        e.setStatut(agent.getStatut());
        e.setServiceRattachementId(agent.getServiceRattachementId());
        e.setTenantId(tenantId);
        agentJpaRepository.save(e);
        return agent;
    }

    @Override
    public Optional<Agent> findAgent(UUID id) {
        return agentJpaRepository.findById(id).map(this::agentToDomain);
    }

    @Override
    public List<Agent> listAgents() {
        return agentJpaRepository.findAll().stream().map(this::agentToDomain).toList();
    }

    // ---------- ENSEIGNANT ----------

    @Override
    @Transactional
    public Enseignant saveEnseignant(Enseignant enseignant) {
        if (enseignant.getId() == null) {
            enseignant.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        enseignantJpaRepository.save(enseignantToEntity(enseignant, tenantId));

        pieceJpaRepository.deleteByEnseignantId(enseignant.getId());
        for (PieceIdentiteEnseignant p : enseignant.getPieces()) {
            if (p.getId() == null) {
                p.setId(UUID.randomUUID());
            }
            p.setEnseignantId(enseignant.getId());
            PieceIdentiteEnseignantEntity pe = new PieceIdentiteEnseignantEntity();
            pe.setId(p.getId());
            pe.setEnseignantId(enseignant.getId());
            pe.setType(p.getType());
            pe.setNumero(p.getNumero());
            pe.setDateDelivrance(p.getDateDelivrance());
            pe.setDateExpiration(p.getDateExpiration());
            pe.setPays(p.getPays());
            pe.setTenantId(tenantId);
            pieceJpaRepository.save(pe);
        }

        affectationJpaRepository.deleteByEnseignantId(enseignant.getId());
        for (AffectationEnseignant a : enseignant.getAffectations()) {
            if (a.getId() == null) {
                a.setId(UUID.randomUUID());
            }
            a.setEnseignantId(enseignant.getId());
            affectationJpaRepository.save(affectationToEntity(a, tenantId));
        }
        return findEnseignant(enseignant.getId()).orElseThrow();
    }

    @Override
    public Optional<Enseignant> findEnseignant(UUID id) {
        return enseignantJpaRepository.findById(id).map(this::enseignantToDomain);
    }

    @Override
    public List<Enseignant> listEnseignants() {
        return enseignantJpaRepository.findAll().stream().map(this::enseignantToDomain).toList();
    }

    // ---------- AFFECTATION ----------

    @Override
    public AffectationEnseignant saveAffectation(UUID enseignantId, AffectationEnseignant affectation) {
        if (affectation.getId() == null) {
            affectation.setId(UUID.randomUUID());
        }
        affectation.setEnseignantId(enseignantId);
        affectationJpaRepository.save(affectationToEntity(affectation, tenant()));
        return affectation;
    }

    @Override
    public Optional<UUID> findEnseignantIdByAffectation(UUID affectationId) {
        return affectationJpaRepository.findById(affectationId).map(AffectationEnseignantEntity::getEnseignantId);
    }

    @Override
    public List<AffectationEnseignant> listAffectations(UUID enseignantId) {
        return affectationJpaRepository.findByEnseignantId(enseignantId).stream().map(this::affectationToDomain).toList();
    }

    @Override
    public List<AffectationEnseignant> listAffectationsClasse(UUID classeId) {
        return affectationJpaRepository.findByClasseId(classeId).stream().map(this::affectationToDomain).toList();
    }

    // ---------- TITULARITE ----------

    @Override
    public Titularite saveTitularite(Titularite titularite) {
        if (titularite.getId() == null) {
            titularite.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        TitulariteEntity e = new TitulariteEntity();
        e.setId(titularite.getId());
        e.setClasseId(titularite.getClasseId());
        e.setEnseignantId(titularite.getEnseignantId());
        e.setDateDelegation(titularite.getDateDelegation());
        e.setDateRetrait(titularite.getDateRetrait());
        e.setType(titularite.getType());
        e.setMotif(titularite.getMotif());
        e.setTenantId(tenantId);
        titulariteJpaRepository.save(e);
        return titularite;
    }

    @Override
    public List<Titularite> listTitularites(UUID classeId) {
        return titulariteJpaRepository.findByClasseId(classeId).stream().map(this::titulariteToDomain).toList();
    }

    // ---------- MAPPERS ENTITY ----------

    private EleveEntity eleveToEntity(Eleve eleve, UUID tenantId) {
        EleveEntity e = new EleveEntity();
        e.setId(eleve.getIdEleve());
        e.setMatricule(eleve.getMatricule());
        e.setNom(eleve.getNom());
        e.setPostNom(eleve.getPostNom());
        e.setPrenom(eleve.getPrenom());
        e.setSexe(eleve.getSexe() == null ? null : eleve.getSexe().name());
        e.setDateNaissance(eleve.getDateNaissance());
        e.setLieuNaissance(eleve.getLieuNaissance());
        e.setNationalite(eleve.getNationalite());
        applyAdresse(eleve.getAdresse(), e::setAdresseRue, e::setAdresseQuartier, e::setAdresseCommune,
                e::setAdresseVille, e::setAdresseProvince, e::setAdressePays, e::setAdresseTelephone);
        e.setPhoto(eleve.getPhoto());
        e.setStatut(eleve.getStatut() == null ? null : eleve.getStatut().name());
        e.setDateInscription(eleve.getDateInscription());
        e.setDateSortie(eleve.getDateSortie());
        e.setEtablissementOrigine(eleve.getEtablissementOrigine());
        e.setEtablissementDestination(eleve.getEtablissementDestination());
        e.setMotifTransfert(eleve.getMotifTransfert());
        e.setStatutDossier(eleve.getStatutDossier() == null ? null : eleve.getStatutDossier().name());
        e.setTenantId(tenantId);
        return e;
    }

    private EnseignantEntity enseignantToEntity(Enseignant enseignant, UUID tenantId) {
        EnseignantEntity e = new EnseignantEntity();
        e.setId(enseignant.getId());
        e.setMatricule(enseignant.getMatricule());
        e.setNom(enseignant.getNom());
        e.setPostNom(enseignant.getPostNom());
        e.setPrenom(enseignant.getPrenom());
        e.setSexe(enseignant.getSexe() == null ? null : enseignant.getSexe().name());
        e.setDateNaissance(enseignant.getDateNaissance());
        e.setTelephone(enseignant.getTelephone());
        e.setEmail(enseignant.getEmail());
        applyAdresse(enseignant.getAdresse(), e::setAdresseRue, e::setAdresseQuartier, e::setAdresseCommune,
                e::setAdresseVille, e::setAdresseProvince, e::setAdressePays, e::setAdresseTelephone);
        e.setDiplome(enseignant.getDiplome());
        e.setDiplomePlus(enseignant.getDiplomePlus());
        e.setSpecialite(json(enseignant.getSpecialite()));
        e.setDateRecrutement(enseignant.getDateRecrutement());
        e.setStatut(enseignant.getStatut() == null ? null : enseignant.getStatut().name());
        e.setVolumeHoraireHebdo(enseignant.getVolumeHoraireHebdo());
        e.setServiceRattachementId(enseignant.getServiceRattachementId());
        e.setPhoto(enseignant.getPhoto());
        e.setTenantId(tenantId);
        return e;
    }

    private AffectationEnseignantEntity affectationToEntity(AffectationEnseignant a, UUID tenantId) {
        AffectationEnseignantEntity e = new AffectationEnseignantEntity();
        e.setId(a.getId());
        e.setEnseignantId(a.getEnseignantId());
        e.setClasseId(a.getClasseId());
        e.setMatiereId(a.getMatiereId());
        e.setType(a.getType() == null ? null : a.getType().name());
        e.setTitulaire(a.isTitulaire());
        e.setVolumeHoraireHebdo(a.getVolumeHoraireHebdo());
        e.setDateDebut(a.getDateDebut());
        e.setDateFin(a.getDateFin());
        e.setPrincipale(a.isPrincipale());
        e.setRemplacementDe(a.getRemplacementDe());
        e.setMotif(a.getMotif());
        e.setTenantId(tenantId);
        return e;
    }

    private InscriptionEntity inscriptionToEntity(Inscription i, UUID tenantId) {
        InscriptionEntity e = new InscriptionEntity();
        e.setId(i.getIdInscription());
        e.setEleveId(i.getEleveId());
        e.setAnneeId(i.getAnneeId());
        e.setClasseId(i.getClasseId());
        e.setDateInscription(i.getDateInscription());
        e.setStatut(i.getStatut() == null ? null : i.getStatut().name());
        e.setRedoublant(i.isRedoublant());
        e.setNouveau(i.isNouveau());
        e.setEcoleOrigine(i.getEcoleOrigine());
        e.setMoyenneAnneePrecedente(i.getMoyenneAnneePrecedente());
        e.setClassementAnneePrecedente(i.getClassementAnneePrecedente());
        e.setFraisRegles(i.isFraisRegles());
        e.setMotifAnnulation(i.getMotifAnnulation());
        e.setTenantId(tenantId);
        return e;
    }

    private CongeEtudeEntity congeToEntity(CongeEtude c, UUID tenantId) {
        CongeEtudeEntity e = new CongeEtudeEntity();
        e.setId(c.getId());
        e.setEleveId(c.getEleveId());
        e.setType(c.getType() == null ? null : c.getType().name());
        e.setDateDebut(c.getDateDebut());
        e.setDateFin(c.getDateFin());
        e.setMotif(c.getMotif());
        e.setJustificatifId(c.getJustificatifId());
        e.setReprisePrevue(c.getReprisePrevue());
        e.setTenantId(tenantId);
        return e;
    }

    private SanctionDisciplinaireEntity sanctionToEntity(SanctionDisciplinaire s, UUID tenantId) {
        SanctionDisciplinaireEntity e = new SanctionDisciplinaireEntity();
        e.setId(s.getId());
        e.setEleveId(s.getEleveId());
        e.setDateSanction(s.getDateSanction());
        e.setType(s.getType() == null ? null : s.getType().name());
        e.setMotif(s.getMotif());
        e.setDureeJours(s.getDureeJours());
        e.setDateFin(s.getDateFin());
        e.setDecidePar(s.getDecidePar());
        e.setInstance(s.getInstance());
        e.setTenantId(tenantId);
        return e;
    }

    // ---------- MAPPERS DOMAIN ----------

    private Eleve eleveToDomain(EleveEntity e) {
        Eleve eleve = new Eleve();
        eleve.setIdEleve(e.getId());
        eleve.setMatricule(e.getMatricule());
        eleve.setNom(e.getNom());
        eleve.setPostNom(e.getPostNom());
        eleve.setPrenom(e.getPrenom());
        eleve.setSexe(e.getSexe() == null ? null : Sexe.valueOf(e.getSexe()));
        eleve.setDateNaissance(e.getDateNaissance());
        eleve.setLieuNaissance(e.getLieuNaissance());
        eleve.setNationalite(e.getNationalite());
        eleve.setAdresse(readAdresse(e.getAdresseRue(), e.getAdresseQuartier(), e.getAdresseCommune(),
                e.getAdresseVille(), e.getAdresseProvince(), e.getAdressePays(), e.getAdresseTelephone()));
        eleve.setPhoto(e.getPhoto());
        eleve.setStatut(e.getStatut() == null ? null : StatutEleve.valueOf(e.getStatut()));
        eleve.setDateInscription(e.getDateInscription());
        eleve.setDateSortie(e.getDateSortie());
        eleve.setEtablissementOrigine(e.getEtablissementOrigine());
        eleve.setEtablissementDestination(e.getEtablissementDestination());
        eleve.setMotifTransfert(e.getMotifTransfert());
        eleve.setStatutDossier(e.getStatutDossier() == null ? null : StatutDossier.valueOf(e.getStatutDossier()));
        dossierJpaRepository.findByEleveId(e.getId()).ifPresent(de -> eleve.setDossier(dossierToDomain(de)));
        eleve.setInscriptions(inscriptionJpaRepository.findByEleveId(e.getId()).stream().map(this::inscriptionToDomain).toList());
        eleve.setConges(congeJpaRepository.findByEleveId(e.getId()).stream().map(this::congeToDomain).toList());
        eleve.setSanctions(sanctionJpaRepository.findByEleveId(e.getId()).stream().map(this::sanctionToDomain).toList());
        return eleve;
    }

    private DossierEleve dossierToDomain(DossierEleveEntity e) {
        DossierEleve d = new DossierEleve();
        d.setId(e.getId());
        d.setEleveId(e.getEleveId());
        d.setReference(e.getReference());
        d.setDateOuverture(e.getDateOuverture());
        d.setStatut(e.getStatut() == null ? null : StatutDossier.valueOf(e.getStatut()));
        d.setPieces(fichierJpaRepository.findByDossierId(e.getId()).stream().map(this::fichierToDomain).toList());
        return d;
    }

    private FichierJoint fichierToDomain(FichierJointEntity e) {
        FichierJoint f = new FichierJoint();
        f.setId(e.getId());
        f.setCategorie(e.getCategorie() == null ? null : CategoriePiece.valueOf(e.getCategorie()));
        f.setNom(e.getNom());
        f.setTypeMime(e.getTypeMime());
        f.setTaille(e.getTaille());
        f.setUri(e.getUri());
        f.setDateDepot(e.getDateDepot());
        f.setDeposePar(e.getDeposePar());
        f.setDocumentOfficiel(e.isDocumentOfficiel());
        return f;
    }

    private Inscription inscriptionToDomain(InscriptionEntity e) {
        Inscription i = new Inscription();
        i.setIdInscription(e.getId());
        i.setEleveId(e.getEleveId());
        i.setAnneeId(e.getAnneeId());
        i.setClasseId(e.getClasseId());
        i.setDateInscription(e.getDateInscription());
        i.setStatut(e.getStatut() == null ? null : StatutInscription.valueOf(e.getStatut()));
        i.setRedoublant(e.isRedoublant());
        i.setNouveau(e.isNouveau());
        i.setEcoleOrigine(e.getEcoleOrigine());
        i.setMoyenneAnneePrecedente(e.getMoyenneAnneePrecedente());
        i.setClassementAnneePrecedente(e.getClassementAnneePrecedente());
        i.setFraisRegles(e.isFraisRegles());
        i.setMotifAnnulation(e.getMotifAnnulation());
        return i;
    }

    private CongeEtude congeToDomain(CongeEtudeEntity e) {
        CongeEtude c = new CongeEtude();
        c.setId(e.getId());
        c.setEleveId(e.getEleveId());
        c.setType(e.getType() == null ? null : TypeConge.valueOf(e.getType()));
        c.setDateDebut(e.getDateDebut());
        c.setDateFin(e.getDateFin());
        c.setMotif(e.getMotif());
        c.setJustificatifId(e.getJustificatifId());
        c.setReprisePrevue(e.getReprisePrevue());
        return c;
    }

    private SanctionDisciplinaire sanctionToDomain(SanctionDisciplinaireEntity e) {
        SanctionDisciplinaire s = new SanctionDisciplinaire();
        s.setId(e.getId());
        s.setEleveId(e.getEleveId());
        s.setDateSanction(e.getDateSanction());
        s.setType(e.getType() == null ? null : TypeSanction.valueOf(e.getType()));
        s.setMotif(e.getMotif());
        s.setDureeJours(e.getDureeJours());
        s.setDateFin(e.getDateFin());
        s.setDecidePar(e.getDecidePar());
        s.setInstance(e.getInstance());
        return s;
    }

    private Parent parentToDomain(ParentEntity e) {
        Parent p = new Parent();
        p.setId(e.getId());
        p.setMatricule(e.getMatricule());
        p.setNom(e.getNom());
        p.setPostNom(e.getPostNom());
        p.setPrenom(e.getPrenom());
        p.setSexe(e.getSexe() == null ? null : Sexe.valueOf(e.getSexe()));
        p.setTelephone(e.getTelephone());
        p.setTelephoneSecondaire(e.getTelephoneSecondaire());
        p.setEmail(e.getEmail());
        p.setAdresse(readAdresse(e.getAdresseRue(), e.getAdresseQuartier(), e.getAdresseCommune(),
                e.getAdresseVille(), e.getAdresseProvince(), e.getAdressePays(), e.getAdresseTelephone()));
        p.setProfession(e.getProfession());
        p.setStatut(e.getStatut());
        return p;
    }

    private LienParentalite lienToDomain(LienParentaliteEntity e) {
        LienParentalite l = new LienParentalite();
        l.setId(e.getId());
        l.setEleveId(e.getEleveId());
        l.setParentId(e.getParentId());
        l.setType(e.getType() == null ? null : TypeLien.valueOf(e.getType()));
        l.setPrincipal(e.isPrincipal());
        l.setPayeur(e.isPayeur());
        l.setReceveurNotifications(e.isReceveurNotifications());
        l.setDateDebut(e.getDateDebut());
        return l;
    }

    private ServiceEtablissement serviceToDomain(ServiceEtablissementEntity e) {
        ServiceEtablissement s = new ServiceEtablissement();
        s.setId(e.getId());
        s.setCode(e.getCode());
        s.setLibelle(e.getLibelle());
        s.setType(e.getType() == null ? null : TypeService.valueOf(e.getType()));
        s.setParentId(e.getParentId());
        s.setResponsableId(e.getResponsableId());
        s.setEmail(e.getEmail());
        return s;
    }

    private Agent agentToDomain(AgentEntity e) {
        Agent a = new Agent();
        a.setId(e.getId());
        a.setMatricule(e.getMatricule());
        a.setNom(e.getNom());
        a.setPostNom(e.getPostNom());
        a.setPrenom(e.getPrenom());
        a.setSexe(e.getSexe() == null ? null : Sexe.valueOf(e.getSexe()));
        a.setDateNaissance(e.getDateNaissance());
        a.setTelephone(e.getTelephone());
        a.setEmail(e.getEmail());
        a.setAdresse(readAdresse(e.getAdresseRue(), e.getAdresseQuartier(), e.getAdresseCommune(),
                e.getAdresseVille(), e.getAdresseProvince(), e.getAdressePays(), e.getAdresseTelephone()));
        a.setFonctionOfficielle(e.getFonctionOfficielle());
        a.setDateRecrutement(e.getDateRecrutement());
        a.setStatut(e.getStatut());
        a.setServiceRattachementId(e.getServiceRattachementId());
        return a;
    }

    private Enseignant enseignantToDomain(EnseignantEntity e) {
        Enseignant en = new Enseignant();
        en.setId(e.getId());
        en.setMatricule(e.getMatricule());
        en.setNom(e.getNom());
        en.setPostNom(e.getPostNom());
        en.setPrenom(e.getPrenom());
        en.setSexe(e.getSexe() == null ? null : Sexe.valueOf(e.getSexe()));
        en.setDateNaissance(e.getDateNaissance());
        en.setTelephone(e.getTelephone());
        en.setEmail(e.getEmail());
        en.setAdresse(readAdresse(e.getAdresseRue(), e.getAdresseQuartier(), e.getAdresseCommune(),
                e.getAdresseVille(), e.getAdresseProvince(), e.getAdressePays(), e.getAdresseTelephone()));
        en.setDiplome(e.getDiplome());
        en.setDiplomePlus(e.getDiplomePlus());
        en.setSpecialite(parseJson(e.getSpecialite()));
        en.setDateRecrutement(e.getDateRecrutement());
        en.setStatut(e.getStatut() == null ? null : StatutEnseignant.valueOf(e.getStatut()));
        en.setVolumeHoraireHebdo(e.getVolumeHoraireHebdo());
        en.setServiceRattachementId(e.getServiceRattachementId());
        en.setPhoto(e.getPhoto());
        en.setPieces(pieceJpaRepository.findByEnseignantId(e.getId()).stream().map(this::pieceToDomain).toList());
        en.setAffectations(affectationJpaRepository.findByEnseignantId(e.getId()).stream().map(this::affectationToDomain).toList());
        return en;
    }

    private PieceIdentiteEnseignant pieceToDomain(PieceIdentiteEnseignantEntity e) {
        PieceIdentiteEnseignant p = new PieceIdentiteEnseignant();
        p.setId(e.getId());
        p.setEnseignantId(e.getEnseignantId());
        p.setType(e.getType());
        p.setNumero(e.getNumero());
        p.setDateDelivrance(e.getDateDelivrance());
        p.setDateExpiration(e.getDateExpiration());
        p.setPays(e.getPays());
        return p;
    }

    private AffectationEnseignant affectationToDomain(AffectationEnseignantEntity e) {
        AffectationEnseignant a = new AffectationEnseignant();
        a.setId(e.getId());
        a.setEnseignantId(e.getEnseignantId());
        a.setClasseId(e.getClasseId());
        a.setMatiereId(e.getMatiereId());
        a.setType(e.getType() == null ? null : TypeAffection.valueOf(e.getType()));
        a.setTitulaire(e.isTitulaire());
        a.setVolumeHoraireHebdo(e.getVolumeHoraireHebdo());
        a.setDateDebut(e.getDateDebut());
        a.setDateFin(e.getDateFin());
        a.setPrincipale(e.isPrincipale());
        a.setRemplacementDe(e.getRemplacementDe());
        a.setMotif(e.getMotif());
        return a;
    }

    private Titularite titulariteToDomain(TitulariteEntity e) {
        Titularite t = new Titularite();
        t.setId(e.getId());
        t.setClasseId(e.getClasseId());
        t.setEnseignantId(e.getEnseignantId());
        t.setDateDelegation(e.getDateDelegation());
        t.setDateRetrait(e.getDateRetrait());
        t.setType(e.getType());
        t.setMotif(e.getMotif());
        return t;
    }

    // ---------- HELPERS ----------

    private void applyAdresse(Adresse adresse,
                              java.util.function.Consumer<String> rue,
                              java.util.function.Consumer<String> quartier,
                              java.util.function.Consumer<String> commune,
                              java.util.function.Consumer<String> ville,
                              java.util.function.Consumer<String> province,
                              java.util.function.Consumer<String> pays,
                              java.util.function.Consumer<String> telephone) {
        if (adresse == null) {
            return;
        }
        rue.accept(adresse.getRue());
        quartier.accept(adresse.getQuartier());
        commune.accept(adresse.getCommune());
        ville.accept(adresse.getVille());
        province.accept(adresse.getProvince());
        pays.accept(adresse.getPays());
        telephone.accept(adresse.getTelephone());
    }

    private Adresse readAdresse(String rue, String quartier, String commune, String ville,
                                String province, String pays, String telephone) {
        boolean vide = rue == null && quartier == null && commune == null && ville == null
                && province == null && pays == null && telephone == null;
        if (vide) {
            return null;
        }
        Adresse a = new Adresse();
        a.setRue(rue);
        a.setQuartier(quartier);
        a.setCommune(commune);
        a.setVille(ville);
        a.setProvince(province);
        a.setPays(pays);
        a.setTelephone(telephone);
        return a;
    }

    private String json(List<String> values) {
        try {
            return objectMapper.writeValueAsString(values == null ? new ArrayList<>() : values);
        } catch (Exception ex) {
            return "[]";
        }
    }

    private List<String> parseJson(String value) {
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {});
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }
}

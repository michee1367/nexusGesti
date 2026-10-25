package com.nexusgesti.exam.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.exam.adapter.out.persistence.entity.*;
import com.nexusgesti.exam.domain.model.*;
import com.nexusgesti.exam.domain.port.out.ExamRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ExamPersistenceAdapter implements ExamRepository {

    private final SessionExamenJpaRepository sessionRepository;
    private final EpreuveJpaRepository epreuveRepository;
    private final CreneauExamenJpaRepository creneauRepository;
    private final ConvocationJpaRepository convocationRepository;
    private final LigneConvocationJpaRepository ligneConvocationRepository;
    private final NoteExamenJpaRepository noteRepository;
    private final CorrectionJpaRepository correctionRepository;
    private final DeliberationJpaRepository deliberationRepository;
    private final DecisionJpaRepository decisionRepository;
    private final DecisionUeJpaRepository decisionUeRepository;
    private final DetailUeJpaRepository detailUeRepository;
    private final ResultatOfficielJpaRepository resultatRepository;
    private final RattrapageJpaRepository rattrapageRepository;
    private final ProcesVerbalJpaRepository procesVerbalRepository;
    private final SignatureJpaRepository signatureRepository;
    private final ObjectMapper objectMapper;

    public ExamPersistenceAdapter(SessionExamenJpaRepository sessionRepository,
                                  EpreuveJpaRepository epreuveRepository,
                                  CreneauExamenJpaRepository creneauRepository,
                                  ConvocationJpaRepository convocationRepository,
                                  LigneConvocationJpaRepository ligneConvocationRepository,
                                  NoteExamenJpaRepository noteRepository,
                                  CorrectionJpaRepository correctionRepository,
                                  DeliberationJpaRepository deliberationRepository,
                                  DecisionJpaRepository decisionRepository,
                                  DecisionUeJpaRepository decisionUeRepository,
                                  DetailUeJpaRepository detailUeRepository,
                                  ResultatOfficielJpaRepository resultatRepository,
                                  RattrapageJpaRepository rattrapageRepository,
                                  ProcesVerbalJpaRepository procesVerbalRepository,
                                  SignatureJpaRepository signatureRepository,
                                  ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.epreuveRepository = epreuveRepository;
        this.creneauRepository = creneauRepository;
        this.convocationRepository = convocationRepository;
        this.ligneConvocationRepository = ligneConvocationRepository;
        this.noteRepository = noteRepository;
        this.correctionRepository = correctionRepository;
        this.deliberationRepository = deliberationRepository;
        this.decisionRepository = decisionRepository;
        this.decisionUeRepository = decisionUeRepository;
        this.detailUeRepository = detailUeRepository;
        this.resultatRepository = resultatRepository;
        this.rattrapageRepository = rattrapageRepository;
        this.procesVerbalRepository = procesVerbalRepository;
        this.signatureRepository = signatureRepository;
        this.objectMapper = objectMapper;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- SESSION ----------

    @Override
    @Transactional
    public SessionExamen saveSession(SessionExamen session) {
        if (session.getIdSession() == null) {
            session.setIdSession(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        SessionExamenEntity e = sessionEntity(session, tenantId);
        sessionRepository.save(e);

        for (Epreuve epreuve : session.getEpreuves()) {
            if (epreuve.getIdEpreuve() == null) {
                epreuve.setIdEpreuve(UUID.randomUUID());
            }
            epreuve.setSessionId(session.getIdSession());
            epreuveRepository.save(epreuveEntity(epreuve, tenantId));
        }
        for (Creneau creneau : session.getCreneaux()) {
            if (creneau.getId() == null) {
                creneau.setId(UUID.randomUUID());
            }
            creneau.setSessionId(session.getIdSession());
            creneauRepository.save(creneauEntity(creneau, tenantId));
        }
        for (Convocation convocation : session.getConvocations()) {
            saveConvocation(convocation);
        }

        return findSession(session.getIdSession()).orElse(session);
    }

    @Override
    public Optional<SessionExamen> findSession(UUID id) {
        return sessionRepository.findById(id).map(e -> {
            SessionExamen session = sessionToDomain(e);
            session.setEpreuves(epreuveRepository.findBySessionId(id).stream()
                    .map(this::epreuveToDomain).toList());
            session.setCreneaux(creneauRepository.findBySessionId(id).stream()
                    .map(this::creneauToDomain).toList());
            session.setConvocations(convocationRepository.findBySessionId(id).stream()
                    .map(this::convocationToDomain).toList());
            return session;
        });
    }

    @Override
    public List<SessionExamen> listSessions() {
        return sessionRepository.findAll().stream().map(e -> {
            SessionExamen session = sessionToDomain(e);
            session.setEpreuves(epreuveRepository.findBySessionId(e.getId()).stream()
                    .map(this::epreuveToDomain).toList());
            return session;
        }).toList();
    }

    // ---------- EPREUVE ----------

    @Override
    @Transactional
    public Epreuve saveEpreuve(Epreuve epreuve) {
        if (epreuve.getIdEpreuve() == null) {
            epreuve.setIdEpreuve(UUID.randomUUID());
        }
        epreuveRepository.save(epreuveEntity(epreuve, tenant()));
        return findEpreuve(epreuve.getIdEpreuve()).orElse(epreuve);
    }

    @Override
    public Optional<Epreuve> findEpreuve(UUID id) {
        return epreuveRepository.findById(id).map(this::epreuveToDomain);
    }

    @Override
    public List<Epreuve> listEpreuvesSession(UUID sessionId) {
        return epreuveRepository.findBySessionId(sessionId).stream().map(this::epreuveToDomain).toList();
    }

    // ---------- CRENEAU ----------

    @Override
    @Transactional
    public Creneau saveCreneau(Creneau creneau) {
        if (creneau.getId() == null) {
            creneau.setId(UUID.randomUUID());
        }
        creneauRepository.save(creneauEntity(creneau, tenant()));
        return findCreneau(creneau.getId());
    }

    @Override
    public List<Creneau> listCreneauxSession(UUID sessionId) {
        return creneauRepository.findBySessionId(sessionId).stream().map(this::creneauToDomain).toList();
    }

    private Creneau findCreneau(UUID id) {
        return creneauRepository.findById(id).map(this::creneauToDomain).orElse(null);
    }

    // ---------- CONVOCATION ----------

    @Override
    @Transactional
    public Convocation saveConvocation(Convocation convocation) {
        if (convocation.getId() == null) {
            convocation.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneConvocationRepository.deleteByConvocationId(convocation.getId());
        convocationRepository.save(convocationEntity(convocation, tenantId));

        for (LigneConvocation ligne : convocation.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setConvocationId(convocation.getId());
            ligneConvocationRepository.save(ligneEntity(ligne, tenantId));
        }
        return findConvocation(convocation.getId()).orElse(convocation);
    }

    @Override
    public Optional<Convocation> findConvocation(UUID id) {
        return convocationRepository.findById(id).map(this::convocationToDomain);
    }

    @Override
    public List<Convocation> listConvocationsSession(UUID sessionId) {
        return convocationRepository.findBySessionId(sessionId).stream()
                .map(this::convocationToDomain).toList();
    }

    @Override
    public long countConvocationsSession(UUID sessionId) {
        return sessionId == null ? convocationRepository.count() : convocationRepository.countBySessionId(sessionId);
    }

    // ---------- NOTE ----------

    @Override
    @Transactional
    public NoteExamen saveNote(NoteExamen note) {
        if (note.getId() == null) {
            note.setId(UUID.randomUUID());
        }
        noteRepository.save(noteEntity(note, tenant()));
        return findNote(note.getId()).orElse(note);
    }

    @Override
    public Optional<NoteExamen> findNote(UUID id) {
        return noteRepository.findById(id).map(this::noteToDomain);
    }

    @Override
    public List<NoteExamen> listNotesEpreuve(UUID epreuveId) {
        return noteRepository.findByEpreuveId(epreuveId).stream().map(this::noteToDomain).toList();
    }

    @Override
    public List<NoteExamen> listNotesEleve(UUID eleveId) {
        return noteRepository.findByEleveId(eleveId).stream().map(this::noteToDomain).toList();
    }

    // ---------- CORRECTION ----------

    @Override
    @Transactional
    public Correction saveCorrection(Correction correction) {
        if (correction.getId() == null) {
            correction.setId(UUID.randomUUID());
        }
        correctionRepository.save(correctionEntity(correction, tenant()));
        return findCorrection(correction.getId()).orElse(correction);
    }

    @Override
    public Optional<Correction> findCorrection(UUID id) {
        return correctionRepository.findById(id).map(this::correctionToDomain);
    }

    @Override
    public List<Correction> listCorrectionsEpreuve(UUID epreuveId) {
        return correctionRepository.findByEpreuveId(epreuveId).stream().map(this::correctionToDomain).toList();
    }

    // ---------- DELIBERATION ----------

    @Override
    @Transactional
    public Deliberation saveDeliberation(Deliberation deliberation) {
        if (deliberation.getId() == null) {
            deliberation.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        decisionRepository.deleteByDeliberationId(deliberation.getId());
        decisionUeRepository.deleteByDeliberationId(deliberation.getId());
        deliberationRepository.save(deliberationEntity(deliberation, tenantId));

        for (Decision decision : deliberation.getDecisions()) {
            if (decision.getId() == null) {
                decision.setId(UUID.randomUUID());
            }
            decision.setDeliberationId(deliberation.getId());
            decisionRepository.save(decisionEntity(decision, tenantId));
        }
        for (DecisionUE decisionUe : deliberation.getDecisionsUE()) {
            if (decisionUe.getId() == null) {
                decisionUe.setId(UUID.randomUUID());
            }
            decisionUe.setDeliberationId(deliberation.getId());
            saveDecisionUe(decisionUe);
        }

        return findDeliberation(deliberation.getId()).orElse(deliberation);
    }

    @Override
    public Optional<Deliberation> findDeliberation(UUID id) {
        return deliberationRepository.findById(id).map(this::deliberationToDomain);
    }

    @Override
    public List<Deliberation> listDeliberations(UUID sessionId) {
        return deliberationRepository.findBySessionId(sessionId).stream()
                .map(this::deliberationToDomain).toList();
    }

    // ---------- RESULTAT ----------

    @Override
    @Transactional
    public ResultatOfficiel saveResultat(ResultatOfficiel resultat) {
        if (resultat.getId() == null) {
            resultat.setId(UUID.randomUUID());
        }
        resultatRepository.save(resultatEntity(resultat, tenant()));
        return findResultat(resultat.getId()).orElse(resultat);
    }

    @Override
    public Optional<ResultatOfficiel> findResultat(UUID id) {
        return resultatRepository.findById(id).map(this::resultatToDomain);
    }

    @Override
    public List<ResultatOfficiel> listResultatsDecision(UUID decisionId) {
        return resultatRepository.findByDecisionId(decisionId).stream().map(this::resultatToDomain).toList();
    }

    // ---------- RATTRAPAGE ----------

    @Override
    @Transactional
    public Rattrapage saveRattrapage(Rattrapage rattrapage) {
        if (rattrapage.getId() == null) {
            rattrapage.setId(UUID.randomUUID());
        }
        rattrapageRepository.save(rattrapageEntity(rattrapage, tenant()));
        return findRattrapage(rattrapage.getId()).orElse(rattrapage);
    }

    @Override
    public Optional<Rattrapage> findRattrapage(UUID id) {
        return rattrapageRepository.findById(id).map(this::rattrapageToDomain);
    }

    @Override
    public List<Rattrapage> listRattrapagesEleve(UUID eleveId) {
        return rattrapageRepository.findByEleveId(eleveId).stream().map(this::rattrapageToDomain).toList();
    }

    // ---------- PROCES VERBAL ----------

    @Override
    @Transactional
    public ProcesVerbal saveProcesVerbal(ProcesVerbal procesVerbal) {
        if (procesVerbal.getId() == null) {
            procesVerbal.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        signatureRepository.deleteByProcesVerbalId(procesVerbal.getId());
        procesVerbalRepository.save(procesVerbalEntity(procesVerbal, tenantId));

        for (Signature signature : procesVerbal.getSignatures()) {
            if (signature.getId() == null) {
                signature.setId(UUID.randomUUID());
            }
            signature.setProcesVerbalId(procesVerbal.getId());
            signatureRepository.save(signatureEntity(signature, tenantId));
        }
        return findProcesVerbal(procesVerbal.getId()).orElse(procesVerbal);
    }

    @Override
    public Optional<ProcesVerbal> findProcesVerbal(UUID id) {
        return procesVerbalRepository.findById(id).map(this::procesVerbalToDomain);
    }

    @Override
    public List<ProcesVerbal> listProcesVerbauxSession(UUID sessionId) {
        return procesVerbalRepository.findBySessionId(sessionId).stream()
                .map(this::procesVerbalToDomain).toList();
    }

    // ---------- MAPPERS ----------

    private SessionExamenEntity sessionEntity(SessionExamen s, UUID tenantId) {
        SessionExamenEntity e = new SessionExamenEntity();
        e.setId(s.getIdSession());
        e.setReference(s.getReference());
        e.setLibelle(s.getLibelle());
        e.setType(s.getType() == null ? null : s.getType().name());
        e.setAnneeId(s.getAnneeId());
        e.setPeriodeId(s.getPeriodeId());
        e.setClasseId(s.getClasseId());
        e.setDateOuverture(s.getDateOuverture());
        e.setDateCloture(s.getDateCloture());
        e.setStatut(s.getStatut() == null ? null : s.getStatut().name());
        e.setAnneeUniversitaire(s.isAnneeUniversitaire());
        e.setConvocationEmise(s.isConvocationEmise());
        e.setProcesVerbalId(s.getProcesVerbalId());
        e.setGrilleDeliberation(s.getGrilleDeliberation());
        e.setNbCandidatsAttendus(s.getNbCandidatsAttendus());
        e.setNbCandidatsConvoques(s.getNbCandidatsConvoques());
        e.setMotifAnnulation(s.getMotifAnnulation());
        e.setAnnuleLe(s.getAnnuleLe());
        e.setTenantId(tenantId);
        return e;
    }

    private SessionExamen sessionToDomain(SessionExamenEntity e) {
        SessionExamen s = new SessionExamen();
        s.setIdSession(e.getId());
        s.setReference(e.getReference());
        s.setLibelle(e.getLibelle());
        s.setType(e.getType() == null ? null : TypeSession.valueOf(e.getType()));
        s.setAnneeId(e.getAnneeId());
        s.setPeriodeId(e.getPeriodeId());
        s.setClasseId(e.getClasseId());
        s.setDateOuverture(e.getDateOuverture());
        s.setDateCloture(e.getDateCloture());
        s.setStatut(e.getStatut() == null ? null : StatutSession.valueOf(e.getStatut()));
        s.setAnneeUniversitaire(e.isAnneeUniversitaire());
        s.setConvocationEmise(e.isConvocationEmise());
        s.setProcesVerbalId(e.getProcesVerbalId());
        s.setGrilleDeliberation(e.getGrilleDeliberation());
        s.setNbCandidatsAttendus(e.getNbCandidatsAttendus());
        s.setNbCandidatsConvoques(e.getNbCandidatsConvoques());
        s.setMotifAnnulation(e.getMotifAnnulation());
        s.setAnnuleLe(e.getAnnuleLe());
        return s;
    }

    private EpreuveEntity epreuveEntity(Epreuve d, UUID tenantId) {
        EpreuveEntity e = new EpreuveEntity();
        e.setId(d.getIdEpreuve());
        e.setSessionId(d.getSessionId());
        e.setReference(d.getReference());
        e.setLibelle(d.getLibelle());
        e.setMatiereId(d.getMatiereId());
        e.setUeId(d.getUeId());
        e.setType(d.getType() == null ? null : d.getType().name());
        e.setDateEpreuve(d.getDate());
        e.setHeureDebut(d.getHeureDebut());
        e.setDureeMinutes(d.getDureeMinutes());
        e.setBareme(d.getBareme());
        e.setCoefficient(d.getCoefficient());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setComptabiliseDansMoyenne(d.isComptabiliseDansMoyenne());
        e.setPeriodeRattachement(d.getPeriodeRattachement());
        e.setEvaluationOrigine(d.getEvaluationOrigine());
        e.setSalleParDefaut(d.getSalleParDefaut());
        e.setConsignes(d.getConsignes());
        e.setAnonymise(d.isAnonymise());
        e.setNoteEliminatoire(d.getNoteEliminatoire());
        e.setDeclareeHorsMoyenne(d.isDeclareeHorsMoyenne());
        e.setTenantId(tenantId);
        return e;
    }

    private Epreuve epreuveToDomain(EpreuveEntity e) {
        Epreuve d = new Epreuve();
        d.setIdEpreuve(e.getId());
        d.setSessionId(e.getSessionId());
        d.setReference(e.getReference());
        d.setLibelle(e.getLibelle());
        d.setMatiereId(e.getMatiereId());
        d.setUeId(e.getUeId());
        d.setType(e.getType() == null ? null : TypeEpreuve.valueOf(e.getType()));
        d.setDate(e.getDateEpreuve());
        d.setHeureDebut(e.getHeureDebut());
        d.setDureeMinutes(e.getDureeMinutes());
        d.setBareme(e.getBareme());
        d.setCoefficient(e.getCoefficient());
        d.setStatut(e.getStatut() == null ? null : StatutEpreuve.valueOf(e.getStatut()));
        d.setComptabiliseDansMoyenne(e.isComptabiliseDansMoyenne());
        d.setPeriodeRattachement(e.getPeriodeRattachement());
        d.setEvaluationOrigine(e.getEvaluationOrigine());
        d.setSalleParDefaut(e.getSalleParDefaut());
        d.setConsignes(e.getConsignes());
        d.setAnonymise(e.isAnonymise());
        d.setNoteEliminatoire(e.getNoteEliminatoire());
        d.setDeclareeHorsMoyenne(e.isDeclareeHorsMoyenne());
        return d;
    }

    private CreneauExamenEntity creneauEntity(Creneau d, UUID tenantId) {
        CreneauExamenEntity e = new CreneauExamenEntity();
        e.setId(d.getId());
        e.setSessionId(d.getSessionId());
        e.setDateCreneau(d.getDate());
        e.setType(d.getType() == null ? null : d.getType().name());
        e.setHeureDebut(d.getHeureDebut());
        e.setHeureFin(d.getHeureFin());
        e.setOrdre(d.getOrdre());
        e.setTenantId(tenantId);
        return e;
    }

    private Creneau creneauToDomain(CreneauExamenEntity e) {
        Creneau d = new Creneau();
        d.setId(e.getId());
        d.setSessionId(e.getSessionId());
        d.setDate(e.getDateCreneau());
        d.setType(e.getType() == null ? null : TypeRetard.valueOf(e.getType()));
        d.setHeureDebut(e.getHeureDebut());
        d.setHeureFin(e.getHeureFin());
        d.setOrdre(e.getOrdre());
        return d;
    }

    private ConvocationEntity convocationEntity(Convocation d, UUID tenantId) {
        ConvocationEntity e = new ConvocationEntity();
        e.setId(d.getId());
        e.setSessionId(d.getSessionId());
        e.setEleveId(d.getEleveId());
        e.setNumero(d.getNumero());
        e.setDateConvocation(d.getDate());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setGenereeLe(d.getGenereeLe());
        e.setNotifieeLe(d.getNotifieeLe());
        e.setCanal(d.getCanal());
        e.setAccuse(d.isAccuse());
        e.setQrCode(d.getQrCode());
        e.setEmplacement(d.getEmplacement());
        e.setUrlTelechargement(d.getUrlTelechargement());
        e.setSignee(d.isSignee());
        e.setTenantId(tenantId);
        return e;
    }

    private Convocation convocationToDomain(ConvocationEntity e) {
        Convocation d = new Convocation();
        d.setId(e.getId());
        d.setSessionId(e.getSessionId());
        d.setEleveId(e.getEleveId());
        d.setNumero(e.getNumero());
        d.setDate(e.getDateConvocation());
        d.setStatut(e.getStatut() == null ? null : StatutConvocation.valueOf(e.getStatut()));
        d.setGenereeLe(e.getGenereeLe());
        d.setNotifieeLe(e.getNotifieeLe());
        d.setCanal(e.getCanal());
        d.setAccuse(e.isAccuse());
        d.setQrCode(e.getQrCode());
        d.setEmplacement(e.getEmplacement());
        d.setUrlTelechargement(e.getUrlTelechargement());
        d.setSignee(e.isSignee());
        d.setLignes(ligneConvocationRepository.findByConvocationId(e.getId()).stream()
                .map(this::ligneToDomain).toList());
        return d;
    }

    private LigneConvocationEntity ligneEntity(LigneConvocation d, UUID tenantId) {
        LigneConvocationEntity e = new LigneConvocationEntity();
        e.setId(d.getId());
        e.setConvocationId(d.getConvocationId());
        e.setEpreuveId(d.getEpreuveId());
        e.setDateLigne(d.getDate());
        e.setHeureDebut(d.getHeureDebut());
        e.setSalle(d.getSalle());
        e.setNumeroPlace(d.getNumeroPlace());
        e.setTenantId(tenantId);
        return e;
    }

    private LigneConvocation ligneToDomain(LigneConvocationEntity e) {
        LigneConvocation d = new LigneConvocation();
        d.setId(e.getId());
        d.setConvocationId(e.getConvocationId());
        d.setEpreuveId(e.getEpreuveId());
        d.setDate(e.getDateLigne());
        d.setHeureDebut(e.getHeureDebut());
        d.setSalle(e.getSalle());
        d.setNumeroPlace(e.getNumeroPlace());
        return d;
    }

    private NoteExamenEntity noteEntity(NoteExamen d, UUID tenantId) {
        NoteExamenEntity e = new NoteExamenEntity();
        e.setId(d.getId());
        e.setEpreuveId(d.getEpreuveId());
        e.setEleveId(d.getEleveId());
        e.setValeur(d.getValeur());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setAbsent(d.isAbsent());
        e.setHorsMoyenne(d.isHorsMoyenne());
        e.setExclue(d.isExclue());
        e.setMotifExclusion(d.getMotifExclusion());
        e.setPeriodeImputation(d.getPeriodeImputation());
        e.setCorrecteur(d.getCorrecteur());
        e.setSecondeCorrection(d.isSecondeCorrection());
        e.setCoefficient(d.getCoefficient());
        e.setSaisiLe(d.getSaisiLe());
        e.setTenantId(tenantId);
        return e;
    }

    private NoteExamen noteToDomain(NoteExamenEntity e) {
        NoteExamen d = new NoteExamen();
        d.setId(e.getId());
        d.setEpreuveId(e.getEpreuveId());
        d.setEleveId(e.getEleveId());
        d.setValeur(e.getValeur());
        d.setStatut(e.getStatut() == null ? null : StatutNoteExamen.valueOf(e.getStatut()));
        d.setAbsent(e.isAbsent());
        d.setHorsMoyenne(e.isHorsMoyenne());
        d.setExclue(e.isExclue());
        d.setMotifExclusion(e.getMotifExclusion());
        d.setPeriodeImputation(e.getPeriodeImputation());
        d.setCorrecteur(e.getCorrecteur());
        d.setSecondeCorrection(e.isSecondeCorrection());
        d.setCoefficient(e.getCoefficient());
        d.setSaisiLe(e.getSaisiLe());
        return d;
    }

    private CorrectionEntity correctionEntity(Correction d, UUID tenantId) {
        CorrectionEntity e = new CorrectionEntity();
        e.setId(d.getId());
        e.setEpreuveId(d.getEpreuveId());
        e.setCorrecteur(d.getCorrecteur());
        e.setDateCorrection(d.getDate());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setNbNotesCorrigees(d.getNbNotesCorrigees());
        e.setMoyenneBrute(d.getMoyenneBrute());
        e.setMoyenneRetenue(d.getMoyenneRetenue());
        e.setPlusForteMoyenne(d.getPlusForteMoyenne());
        e.setPlusFaibleMoyenne(d.getPlusFaibleMoyenne());
        e.setTenantId(tenantId);
        return e;
    }

    private Correction correctionToDomain(CorrectionEntity e) {
        Correction d = new Correction();
        d.setId(e.getId());
        d.setEpreuveId(e.getEpreuveId());
        d.setCorrecteur(e.getCorrecteur());
        d.setDate(e.getDateCorrection());
        d.setStatut(e.getStatut() == null ? null : StatutCorrection.valueOf(e.getStatut()));
        d.setNbNotesCorrigees(e.getNbNotesCorrigees());
        d.setMoyenneBrute(e.getMoyenneBrute());
        d.setMoyenneRetenue(e.getMoyenneRetenue());
        d.setPlusForteMoyenne(e.getPlusForteMoyenne());
        d.setPlusFaibleMoyenne(e.getPlusFaibleMoyenne());
        return d;
    }

    private DeliberationEntity deliberationEntity(Deliberation d, UUID tenantId) {
        DeliberationEntity e = new DeliberationEntity();
        e.setId(d.getId());
        e.setSessionId(d.getSessionId());
        e.setReference(d.getReference());
        e.setDateDeliberation(d.getDate());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setPresident(d.getPresident());
        e.setMembres(writeJson(d.getMembres()));
        e.setQuorumAtteint(d.isQuorumAtteint());
        e.setGrilleVersion(d.getGrilleVersion());
        e.setDateCloture(d.getDateCloture());
        e.setTenantId(tenantId);
        return e;
    }

    private Deliberation deliberationToDomain(DeliberationEntity e) {
        Deliberation d = new Deliberation();
        d.setId(e.getId());
        d.setSessionId(e.getSessionId());
        d.setReference(e.getReference());
        d.setDate(e.getDateDeliberation());
        d.setStatut(e.getStatut() == null ? null : StatutDeliberation.valueOf(e.getStatut()));
        d.setPresident(e.getPresident());
        d.setMembres(readJsonUuids(e.getMembres()));
        d.setQuorumAtteint(e.isQuorumAtteint());
        d.setGrilleVersion(e.getGrilleVersion());
        d.setDateCloture(e.getDateCloture());
        d.setDecisions(decisionRepository.findByDeliberationId(e.getId()).stream()
                .map(this::decisionToDomain).toList());
        d.setDecisionsUE(decisionUeRepository.findByDeliberationId(e.getId()).stream()
                .map(this::decisionUeToDomain).toList());
        return d;
    }

    private DecisionEntity decisionEntity(Decision d, UUID tenantId) {
        DecisionEntity e = new DecisionEntity();
        e.setId(d.getId());
        e.setDeliberationId(d.getDeliberationId());
        e.setEleveId(d.getEleveId());
        e.setClasseId(d.getClasseId());
        e.setMoyenneGenerale(d.getMoyenneGenerale());
        e.setMoyennePeriode(d.getMoyennePeriode());
        e.setRang(d.getRang());
        e.setEffectif(d.getEffectif());
        e.setTauxReussite(d.getTauxReussite());
        e.setNbAbsences(d.getNbAbsences());
        e.setDecision(d.getDecision() == null ? null : d.getDecision().name());
        e.setMention(d.getMention());
        e.setSeuilApplique(d.getSeuilApplique());
        e.setCr(d.getCr() == null ? null : d.getCr().name());
        e.setMotif(d.getMotif());
        e.setDateDecision(d.getDateDecision());
        e.setNotifie(d.isNotifie());
        e.setDateNotification(d.getDateNotification());
        e.setTenantId(tenantId);
        return e;
    }

    private Decision decisionToDomain(DecisionEntity e) {
        Decision d = new Decision();
        d.setId(e.getId());
        d.setDeliberationId(e.getDeliberationId());
        d.setEleveId(e.getEleveId());
        d.setClasseId(e.getClasseId());
        d.setMoyenneGenerale(e.getMoyenneGenerale());
        d.setMoyennePeriode(e.getMoyennePeriode());
        d.setRang(e.getRang());
        d.setEffectif(e.getEffectif());
        d.setTauxReussite(e.getTauxReussite());
        d.setNbAbsences(e.getNbAbsences());
        d.setDecision(e.getDecision() == null ? null : DecisionType.valueOf(e.getDecision()));
        d.setMention(e.getMention());
        d.setSeuilApplique(e.getSeuilApplique());
        d.setCr(e.getCr() == null ? null : DecisionCode.valueOf(e.getCr()));
        d.setMotif(e.getMotif());
        d.setDateDecision(e.getDateDecision());
        d.setNotifie(e.isNotifie());
        d.setDateNotification(e.getDateNotification());
        return d;
    }

    private DecisionUE saveDecisionUe(DecisionUE d) {
        if (d.getId() == null) {
            d.setId(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        detailUeRepository.deleteByDecisionUeId(d.getId());
        decisionUeRepository.save(decisionUeEntity(d, tenantId));

        for (DetailUE detail : d.getDetails()) {
            if (detail.getId() == null) {
                detail.setId(UUID.randomUUID());
            }
            detail.setDecisionUeId(d.getId());
            detailUeRepository.save(detailUeEntity(detail, tenantId));
        }
        return d;
    }

    private DecisionUeEntity decisionUeEntity(DecisionUE d, UUID tenantId) {
        DecisionUeEntity e = new DecisionUeEntity();
        e.setId(d.getId());
        e.setDeliberationId(d.getDeliberationId());
        e.setEleveId(d.getEleveId());
        e.setUeId(d.getUeId());
        e.setMoyenneUe(d.getMoyenneUE());
        e.setCredits(d.getCredits());
        e.setDecision(d.getDecision() == null ? null : d.getDecision().name());
        e.setTenantId(tenantId);
        return e;
    }

    private DecisionUE decisionUeToDomain(DecisionUeEntity e) {
        DecisionUE d = new DecisionUE();
        d.setId(e.getId());
        d.setDeliberationId(e.getDeliberationId());
        d.setEleveId(e.getEleveId());
        d.setUeId(e.getUeId());
        d.setMoyenneUE(e.getMoyenneUe());
        d.setCredits(e.getCredits());
        d.setDecision(e.getDecision() == null ? null : DecisionType.valueOf(e.getDecision()));
        d.setDetails(detailUeRepository.findByDecisionUeId(e.getId()).stream()
                .map(this::detailUeToDomain).toList());
        return d;
    }

    private DetailUeEntity detailUeEntity(DetailUE d, UUID tenantId) {
        DetailUeEntity e = new DetailUeEntity();
        e.setId(d.getId());
        e.setDecisionUeId(d.getDecisionUeId());
        e.setCoursId(d.getCoursId());
        e.setMoyenneCours(d.getMoyenneCours());
        e.setCoefficient(d.getCoefficient());
        e.setAdmis(d.isAdmis());
        e.setTenantId(tenantId);
        return e;
    }

    private DetailUE detailUeToDomain(DetailUeEntity e) {
        DetailUE d = new DetailUE();
        d.setId(e.getId());
        d.setDecisionUeId(e.getDecisionUeId());
        d.setCoursId(e.getCoursId());
        d.setMoyenneCours(e.getMoyenneCours());
        d.setCoefficient(e.getCoefficient());
        d.setAdmis(e.isAdmis());
        return d;
    }

    private ResultatOfficielEntity resultatEntity(ResultatOfficiel d, UUID tenantId) {
        ResultatOfficielEntity e = new ResultatOfficielEntity();
        e.setId(d.getId());
        e.setDecisionId(d.getDecisionId());
        e.setReference(d.getReference());
        e.setPublie(d.isPublie());
        e.setDatePublication(d.getDatePublication());
        e.setGenerateur(d.getGenerateur());
        e.setUrlDocument(d.getUrlDocument());
        e.setTenantId(tenantId);
        return e;
    }

    private ResultatOfficiel resultatToDomain(ResultatOfficielEntity e) {
        ResultatOfficiel d = new ResultatOfficiel();
        d.setId(e.getId());
        d.setDecisionId(e.getDecisionId());
        d.setReference(e.getReference());
        d.setPublie(e.isPublie());
        d.setDatePublication(e.getDatePublication());
        d.setGenerateur(e.getGenerateur());
        d.setUrlDocument(e.getUrlDocument());
        return d;
    }

    private RattrapageEntity rattrapageEntity(Rattrapage d, UUID tenantId) {
        RattrapageEntity e = new RattrapageEntity();
        e.setId(d.getId());
        e.setSessionOrigineId(d.getSessionOrigineId());
        e.setSessionRattrapageId(d.getSessionRattrapageId());
        e.setEleveId(d.getEleveId());
        e.setEpreuvesConcernees(writeJson(d.getEpreuvesConcernees()));
        e.setConvocationId(d.getConvocationId());
        e.setNePasPresenter(d.isNePasPresenter());
        e.setMotif(d.getMotif());
        e.setTenantId(tenantId);
        return e;
    }

    private Rattrapage rattrapageToDomain(RattrapageEntity e) {
        Rattrapage d = new Rattrapage();
        d.setId(e.getId());
        d.setSessionOrigineId(e.getSessionOrigineId());
        d.setSessionRattrapageId(e.getSessionRattrapageId());
        d.setEleveId(e.getEleveId());
        d.setEpreuvesConcernees(readJsonUuids(e.getEpreuvesConcernees()));
        d.setConvocationId(e.getConvocationId());
        d.setNePasPresenter(e.isNePasPresenter());
        d.setMotif(e.getMotif());
        return d;
    }

    private ProcesVerbalEntity procesVerbalEntity(ProcesVerbal d, UUID tenantId) {
        ProcesVerbalEntity e = new ProcesVerbalEntity();
        e.setId(d.getId());
        e.setSessionId(d.getSessionId());
        e.setReference(d.getReference());
        e.setStatut(d.getStatut() == null ? null : d.getStatut().name());
        e.setDateRedaction(d.getDateRedaction());
        e.setRedigePar(d.getRedigePar());
        e.setPresenceDirecteur(d.isPresenceDirecteur());
        e.setObservations(d.getObservations());
        e.setNbAbsences(d.getNbAbsences());
        e.setNbIncidents(d.getNbIncidents());
        e.setDateCloture(d.getDateCloture());
        e.setMotifContestation(d.getMotifContestation());
        e.setTenantId(tenantId);
        return e;
    }

    private ProcesVerbal procesVerbalToDomain(ProcesVerbalEntity e) {
        ProcesVerbal d = new ProcesVerbal();
        d.setId(e.getId());
        d.setSessionId(e.getSessionId());
        d.setReference(e.getReference());
        d.setStatut(e.getStatut() == null ? null : StatutPV.valueOf(e.getStatut()));
        d.setDateRedaction(e.getDateRedaction());
        d.setRedigePar(e.getRedigePar());
        d.setPresenceDirecteur(e.isPresenceDirecteur());
        d.setObservations(e.getObservations());
        d.setNbAbsences(e.getNbAbsences());
        d.setNbIncidents(e.getNbIncidents());
        d.setDateCloture(e.getDateCloture());
        d.setMotifContestation(e.getMotifContestation());
        d.setSignatures(signatureRepository.findByProcesVerbalId(e.getId()).stream()
                .map(this::signatureToDomain).toList());
        return d;
    }

    private SignatureEntity signatureEntity(Signature d, UUID tenantId) {
        SignatureEntity e = new SignatureEntity();
        e.setId(d.getId());
        e.setProcesVerbalId(d.getProcesVerbalId());
        e.setSignataire(d.getSignataire());
        e.setRole(d.getRole());
        e.setDateSignature(d.getDateSignature());
        e.setEmpreinte(d.getEmpreinte());
        e.setValidee(d.isValidee());
        e.setTenantId(tenantId);
        return e;
    }

    private Signature signatureToDomain(SignatureEntity e) {
        Signature d = new Signature();
        d.setId(e.getId());
        d.setProcesVerbalId(e.getProcesVerbalId());
        d.setSignataire(e.getSignataire());
        d.setRole(e.getRole());
        d.setDateSignature(e.getDateSignature());
        d.setEmpreinte(e.getEmpreinte());
        d.setValidee(e.isValidee());
        return d;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value == null ? List.of() : value);
        } catch (JsonProcessingException ex) {
            return "[]";
        }
    }

    private List<UUID> readJsonUuids(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, UUID.class));
        } catch (JsonProcessingException ex) {
            return new ArrayList<>();
        }
    }
}
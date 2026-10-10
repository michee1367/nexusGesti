package com.nexusgesti.assessment.adapter.out.persistence;

import com.nexusgesti.assessment.adapter.out.persistence.entity.*;
import com.nexusgesti.assessment.domain.model.*;
import com.nexusgesti.assessment.domain.port.out.AssessmentRepository;
import com.nexusgesti.common.tenancy.TenantContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AssessmentPersistenceAdapter implements AssessmentRepository {

    private final EvaluationJpaRepository evaluationJpaRepository;
    private final FeuilleNoteJpaRepository feuilleJpaRepository;
    private final NoteJpaRepository noteJpaRepository;
    private final BulletinJpaRepository bulletinJpaRepository;
    private final LigneBulletinJpaRepository ligneBulletinJpaRepository;
    private final CouponNotesJpaRepository couponJpaRepository;

    public AssessmentPersistenceAdapter(EvaluationJpaRepository evaluationJpaRepository,
                                        FeuilleNoteJpaRepository feuilleJpaRepository,
                                        NoteJpaRepository noteJpaRepository,
                                        BulletinJpaRepository bulletinJpaRepository,
                                        LigneBulletinJpaRepository ligneBulletinJpaRepository,
                                        CouponNotesJpaRepository couponJpaRepository) {
        this.evaluationJpaRepository = evaluationJpaRepository;
        this.feuilleJpaRepository = feuilleJpaRepository;
        this.noteJpaRepository = noteJpaRepository;
        this.bulletinJpaRepository = bulletinJpaRepository;
        this.ligneBulletinJpaRepository = ligneBulletinJpaRepository;
        this.couponJpaRepository = couponJpaRepository;
    }

    private UUID tenant() {
        return TenantContext.require().asUuid();
    }

    // ---------- EVALUATION ----------

    @Override
    @Transactional
    public Evaluation saveEvaluation(Evaluation evaluation) {
        if (evaluation.getIdEvaluation() == null) {
            evaluation.setIdEvaluation(UUID.randomUUID());
        }
        UUID tenantId = tenant();

        EvaluationEntity e = new EvaluationEntity();
        e.setId(evaluation.getIdEvaluation());
        e.setReference(evaluation.getReference());
        e.setLibelle(evaluation.getLibelle());
        e.setType(evaluation.getType() == null ? null : evaluation.getType().name());
        e.setClasseId(evaluation.getClasseId());
        e.setCoursId(evaluation.getCoursId());
        e.setUeId(evaluation.getUeId());
        e.setSemestreId(evaluation.getSemestreId());
        e.setMatiereId(evaluation.getMatiereId());
        e.setEnseignantId(evaluation.getEnseignantId());
        e.setAnneeId(evaluation.getAnneeId());
        e.setPeriodeId(evaluation.getPeriodeId());
        e.setDateEvaluation(evaluation.getDate());
        e.setHeureDebut(evaluation.getHeureDebut());
        e.setDureeMinutes(evaluation.getDuree());
        e.setSalleId(evaluation.getSalleId());
        e.setStatut(evaluation.getStatut() == null ? null : evaluation.getStatut().name());
        e.setGrilleId(evaluation.getGrilleId());
        e.setGrilleVersion(evaluation.getGrilleVersion());
        e.setBaremeType(evaluation.getBaremeType());
        e.setNoteMax(evaluation.getNoteMax());
        e.setCoefficientDeclare(evaluation.getCoefficientDeclare());
        e.setConsignes(evaluation.getConsignes());
        e.setAnonymise(evaluation.isAnonymise());
        e.setCompteeDansMoyenne(evaluation.isCompteeDansMoyenne());
        e.setDeclencheSession(evaluation.isDeclencheSession());
        e.setAnnuleLe(evaluation.getAnnuleLe());
        e.setMotifAnnulation(evaluation.getMotifAnnulation());
        e.setCreeLe(evaluation.getCreeLe());
        e.setTenantId(tenantId);
        evaluationJpaRepository.save(e);

        if (evaluation.getFeuille() != null) {
            FeuilleNote f = evaluation.getFeuille();
            if (f.getId() == null) {
                f.setId(UUID.randomUUID());
            }
            f.setEvaluationId(evaluation.getIdEvaluation());
            FeuilleNoteEntity fe = new FeuilleNoteEntity();
            fe.setId(f.getId());
            fe.setEvaluationId(f.getEvaluationId());
            fe.setStatut(f.getStatut() == null ? null : f.getStatut().name());
            fe.setNbNotesAttendues(f.getNbNotesAttendues());
            fe.setNbNotesSaisies(f.getNbNotesSaisies());
            fe.setNbAbsents(f.getNbAbsents());
            fe.setOuverteLe(f.getOuverteLe());
            fe.setClotureeLe(f.getClotureeLe());
            fe.setVerrouillee(f.isVerrouillee());
            fe.setTenantId(tenantId);
            feuilleJpaRepository.save(fe);
        }

        for (Note note : evaluation.getNotes()) {
            if (note.getId() == null) {
                note.setId(UUID.randomUUID());
            }
            note.setEvaluationId(evaluation.getIdEvaluation());
            if (evaluation.getFeuille() != null) {
                note.setFeuilleId(evaluation.getFeuille().getId());
            }
            noteJpaRepository.save(noteEntity(note, tenantId));
        }

        return findEvaluation(evaluation.getIdEvaluation()).orElse(evaluation);
    }

    @Override
    public Optional<Evaluation> findEvaluation(UUID id) {
        return evaluationJpaRepository.findById(id).map(e -> {
            Evaluation evaluation = evaluationToDomain(e);
            List<Note> notes = noteJpaRepository.findByEvaluationId(id).stream()
                    .map(this::noteToDomain).toList();
            evaluation.setNotes(notes);
            feuilleJpaRepository.findByEvaluationId(id)
                    .ifPresent(f -> evaluation.setFeuille(feuilleToDomain(f)));
            return evaluation;
        });
    }

    @Override
    public List<Evaluation> listEvaluations() {
        return evaluationJpaRepository.findAll().stream().map(this::evaluationToDomain).toList();
    }

    @Override
    public List<Evaluation> listEvaluationsClasse(UUID classeId) {
        return evaluationJpaRepository.findByClasseId(classeId).stream().map(this::evaluationToDomain).toList();
    }

    @Override
    public List<Evaluation> listEvaluationsByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return evaluationJpaRepository.findAllById(ids).stream().map(this::evaluationToDomain).toList();
    }

    // ---------- NOTES ----------

    @Override
    public List<Note> listNotes() {
        return noteJpaRepository.findAll().stream().map(this::noteToDomain).toList();
    }

    @Override
    public List<Note> listNotesByEvaluation(UUID evaluationId) {
        return noteJpaRepository.findByEvaluationId(evaluationId).stream().map(this::noteToDomain).toList();
    }

    @Override
    public List<Note> listNotesEleve(UUID eleveId) {
        return noteJpaRepository.findByEleveId(eleveId).stream().map(this::noteToDomain).toList();
    }

    @Override
    public List<Note> listNotesByEvaluationIds(List<UUID> evaluationIds) {
        if (evaluationIds == null || evaluationIds.isEmpty()) {
            return List.of();
        }
        return noteJpaRepository.findByEvaluationIdIn(evaluationIds).stream().map(this::noteToDomain).toList();
    }

    // ---------- BULLETIN ----------

    @Override
    @Transactional
    public Bulletin saveBulletin(Bulletin bulletin) {
        if (bulletin.getIdBulletin() == null) {
            bulletin.setIdBulletin(UUID.randomUUID());
        }
        UUID tenantId = tenant();
        ligneBulletinJpaRepository.deleteByBulletinId(bulletin.getIdBulletin());

        BulletinEntity be = new BulletinEntity();
        be.setId(bulletin.getIdBulletin());
        be.setReference(bulletin.getReference());
        be.setEleveId(bulletin.getEleveId());
        be.setAnneeId(bulletin.getAnneeId());
        be.setPeriodeId(bulletin.getPeriodeId());
        be.setClasseId(bulletin.getClasseId());
        be.setType(bulletin.getType() == null ? null : bulletin.getType().name());
        be.setStatut(bulletin.getStatut() == null ? null : bulletin.getStatut().name());
        be.setVersion(bulletin.getVersion() == null ? 1 : bulletin.getVersion());
        be.setGrilleId(bulletin.getGrilleId());
        be.setGrilleVersion(bulletin.getGrilleVersion());
        be.setGrilleDateActivation(bulletin.getGrilleDateActivation());
        be.setMoyenneGenerale(bulletin.getMoyenneGenerale());
        be.setMoyennePeriode(bulletin.getMoyennePeriode());
        be.setMoyenneAnnuelle(bulletin.getMoyenneAnnuelle());
        be.setRang(bulletin.getRang());
        be.setEffectif(bulletin.getEffectif());
        be.setAppreciationGenerale(bulletin.getAppreciationGenerale());
        be.setMentionGenerale(bulletin.getMentionGenerale());
        be.setDecision(bulletin.getDecision());
        be.setNbAbsencesJustifiees(bulletin.getNbAbsencesJustifiees());
        be.setNbAbsencesInjustifiees(bulletin.getNbAbsencesInjustifiees());
        be.setNbRetards(bulletin.getNbRetards());
        be.setTauxPresence(bulletin.getTauxPresence());
        be.setRegleAssiduite(bulletin.getRegleAssiduite());
        be.setRespecteRegleAssiduite(bulletin.getRespecteRegleAssiduite());
        be.setAvertissementAssiduite(bulletin.getAvertissementAssiduite());
        be.setRetardJours(bulletin.getRetardJours());
        be.setRetardHeures(bulletin.getRetardHeures());
        be.setGenereLe(bulletin.getGenereLe());
        be.setPublieLe(bulletin.getPublieLe());
        be.setGenerePar(bulletin.getGenerePar());
        be.setRectifieLe(bulletin.getRectifieLe());
        be.setMotifRectification(bulletin.getMotifRectification());
        be.setCreePar(bulletin.getCreePar());
        be.setTenantId(tenantId);
        bulletinJpaRepository.save(be);

        for (LigneBulletin ligne : bulletin.getLignes()) {
            if (ligne.getId() == null) {
                ligne.setId(UUID.randomUUID());
            }
            ligne.setBulletinId(bulletin.getIdBulletin());
            ligneBulletinJpaRepository.save(ligneEntity(ligne, tenantId));
        }

        return findBulletin(bulletin.getIdBulletin()).orElse(bulletin);
    }

    @Override
    public Optional<Bulletin> findBulletin(UUID id) {
        return bulletinJpaRepository.findById(id).map(be -> {
            Bulletin bulletin = bulletinToDomain(be);
            bulletin.setLignes(ligneBulletinJpaRepository.findByBulletinId(id).stream()
                    .map(this::ligneToDomain).toList());
            bulletin.setCoupons(couponJpaRepository.findByBulletinId(id).stream()
                    .map(this::couponToDomain).toList());
            return bulletin;
        });
    }

    @Override
    public List<Bulletin> listBulletins() {
        return bulletinJpaRepository.findAll().stream().map(be -> {
            Bulletin bulletin = bulletinToDomain(be);
            bulletin.setLignes(ligneBulletinJpaRepository.findByBulletinId(be.getId()).stream()
                    .map(this::ligneToDomain).toList());
            return bulletin;
        }).toList();
    }

    // ---------- COUPON ----------

    @Override
    public CouponNotes saveCoupon(CouponNotes coupon) {
        if (coupon.getId() == null) {
            coupon.setId(UUID.randomUUID());
        }
        couponJpaRepository.save(couponEntity(coupon, tenant()));
        return findCoupon(coupon.getId()).orElse(coupon);
    }

    @Override
    public Optional<CouponNotes> findCoupon(UUID id) {
        return couponJpaRepository.findById(id).map(this::couponToDomain);
    }

    @Override
    public List<CouponNotes> listCouponsBulletin(UUID bulletinId) {
        return couponJpaRepository.findByBulletinId(bulletinId).stream().map(this::couponToDomain).toList();
    }

    @Override
    public long countCouponsAnnee(UUID anneeId) {
        return anneeId == null ? couponJpaRepository.count() : couponJpaRepository.countByAnneeId(anneeId);
    }

    // ---------- MAPPERS ----------

    private Evaluation evaluationToDomain(EvaluationEntity e) {
        Evaluation d = new Evaluation();
        d.setIdEvaluation(e.getId());
        d.setReference(e.getReference());
        d.setLibelle(e.getLibelle());
        d.setType(e.getType() == null ? null : TypeEvaluation.valueOf(e.getType()));
        d.setClasseId(e.getClasseId());
        d.setCoursId(e.getCoursId());
        d.setUeId(e.getUeId());
        d.setSemestreId(e.getSemestreId());
        d.setMatiereId(e.getMatiereId());
        d.setEnseignantId(e.getEnseignantId());
        d.setAnneeId(e.getAnneeId());
        d.setPeriodeId(e.getPeriodeId());
        d.setDate(e.getDateEvaluation());
        d.setHeureDebut(e.getHeureDebut());
        d.setDuree(e.getDureeMinutes());
        d.setSalleId(e.getSalleId());
        d.setStatut(e.getStatut() == null ? null : StatutEvaluation.valueOf(e.getStatut()));
        d.setGrilleId(e.getGrilleId());
        d.setGrilleVersion(e.getGrilleVersion());
        d.setBaremeType(e.getBaremeType());
        d.setNoteMax(e.getNoteMax());
        d.setCoefficientDeclare(e.getCoefficientDeclare());
        d.setConsignes(e.getConsignes());
        d.setAnonymise(e.isAnonymise());
        d.setCompteeDansMoyenne(e.isCompteeDansMoyenne());
        d.setDeclencheSession(e.isDeclencheSession());
        d.setAnnuleLe(e.getAnnuleLe());
        d.setMotifAnnulation(e.getMotifAnnulation());
        d.setCreeLe(e.getCreeLe());
        d.setNotes(new ArrayList<>());
        return d;
    }

    private FeuilleNote feuilleToDomain(FeuilleNoteEntity e) {
        FeuilleNote f = new FeuilleNote();
        f.setId(e.getId());
        f.setEvaluationId(e.getEvaluationId());
        f.setStatut(e.getStatut() == null ? null : StatutBulletin.valueOf(e.getStatut()));
        f.setNbNotesAttendues(e.getNbNotesAttendues());
        f.setNbNotesSaisies(e.getNbNotesSaisies());
        f.setNbAbsents(e.getNbAbsents());
        f.setOuverteLe(e.getOuverteLe());
        f.setClotureeLe(e.getClotureeLe());
        f.setVerrouillee(e.isVerrouillee());
        return f;
    }

    private NoteEntity noteEntity(Note note, UUID tenantId) {
        NoteEntity e = new NoteEntity();
        e.setId(note.getId());
        e.setEvaluationId(note.getEvaluationId());
        e.setFeuilleId(note.getFeuilleId());
        e.setEleveId(note.getEleveId());
        e.setValeur(note.getValeur());
        e.setStatut(note.getStatut() == null ? null : note.getStatut().name());
        e.setAbsent(note.isAbsent());
        e.setExclue(note.isExclue());
        e.setMotifExclusion(note.getMotifExclusion());
        e.setCoefficientReel(note.getCoefficientReel());
        e.setSource(note.getSource() == null ? null : note.getSource().name());
        e.setSaisiePar(note.getSaisiePar());
        e.setSaisieLe(note.getSaisieLe());
        e.setApprouvee(note.isApprouvee());
        e.setMotifContestation(note.getMotifContestation());
        e.setDateContestation(note.getDateContestation());
        e.setRemplaceePar(note.getRemplaceePar());
        e.setTenantId(tenantId);
        return e;
    }

    private Note noteToDomain(NoteEntity e) {
        Note n = new Note();
        n.setId(e.getId());
        n.setEvaluationId(e.getEvaluationId());
        n.setFeuilleId(e.getFeuilleId());
        n.setEleveId(e.getEleveId());
        n.setValeur(e.getValeur());
        n.setStatut(e.getStatut() == null ? null : StatutNote.valueOf(e.getStatut()));
        n.setAbsent(e.isAbsent());
        n.setExclue(e.isExclue());
        n.setMotifExclusion(e.getMotifExclusion());
        n.setCoefficientReel(e.getCoefficientReel());
        n.setSource(e.getSource() == null ? null : TypeSourceNote.valueOf(e.getSource()));
        n.setSaisiePar(e.getSaisiePar());
        n.setSaisieLe(e.getSaisieLe());
        n.setApprouvee(e.isApprouvee());
        n.setMotifContestation(e.getMotifContestation());
        n.setDateContestation(e.getDateContestation());
        n.setRemplaceePar(e.getRemplaceePar());
        return n;
    }

    private LigneBulletinEntity ligneEntity(LigneBulletin l, UUID tenantId) {
        LigneBulletinEntity e = new LigneBulletinEntity();
        e.setId(l.getId());
        e.setBulletinId(l.getBulletinId());
        e.setNiveau(l.getNiveau());
        e.setCode(l.getCode());
        e.setLibelle(l.getLibelle());
        e.setParentCode(l.getParentCode());
        e.setCoefficient(l.getCoefficient());
        e.setPoidsNormalise(l.getPoidsNormalise());
        e.setMoyennePeriode(l.getMoyennePeriode());
        e.setMoyenneAnnuelle(l.getMoyenneAnnuelle());
        e.setMoyenneUe(l.getMoyenneUe());
        e.setMoyenneCours(l.getMoyenneCours());
        e.setRang(l.getRang());
        e.setEffectif(l.getEffectif());
        e.setMention(l.getMention());
        e.setAppreciation(l.getAppreciation());
        e.setAbsent(l.isAbsent());
        e.setCasParticulier(l.getCasParticulier());
        e.setUeId(l.getUeId());
        e.setCoursId(l.getCoursId());
        e.setMatiereId(l.getMatiereId());
        e.setTenantId(tenantId);
        return e;
    }

    private LigneBulletin ligneToDomain(LigneBulletinEntity e) {
        LigneBulletin l = new LigneBulletin();
        l.setId(e.getId());
        l.setBulletinId(e.getBulletinId());
        l.setNiveau(e.getNiveau());
        l.setCode(e.getCode());
        l.setLibelle(e.getLibelle());
        l.setParentCode(e.getParentCode());
        l.setCoefficient(e.getCoefficient());
        l.setPoidsNormalise(e.getPoidsNormalise());
        l.setMoyennePeriode(e.getMoyennePeriode());
        l.setMoyenneAnnuelle(e.getMoyenneAnnuelle());
        l.setMoyenneUe(e.getMoyenneUe());
        l.setMoyenneCours(e.getMoyenneCours());
        l.setRang(e.getRang());
        l.setEffectif(e.getEffectif());
        l.setMention(e.getMention());
        l.setAppreciation(e.getAppreciation());
        l.setAbsent(e.isAbsent());
        l.setCasParticulier(e.getCasParticulier());
        l.setUeId(e.getUeId());
        l.setCoursId(e.getCoursId());
        l.setMatiereId(e.getMatiereId());
        return l;
    }

    private Bulletin bulletinToDomain(BulletinEntity e) {
        Bulletin b = new Bulletin();
        b.setIdBulletin(e.getId());
        b.setReference(e.getReference());
        b.setEleveId(e.getEleveId());
        b.setAnneeId(e.getAnneeId());
        b.setPeriodeId(e.getPeriodeId());
        b.setClasseId(e.getClasseId());
        b.setType(e.getType() == null ? null : TypeBulletin.valueOf(e.getType()));
        b.setStatut(e.getStatut() == null ? null : StatutBulletin.valueOf(e.getStatut()));
        b.setVersion(e.getVersion());
        b.setGrilleId(e.getGrilleId());
        b.setGrilleVersion(e.getGrilleVersion());
        b.setGrilleDateActivation(e.getGrilleDateActivation());
        b.setMoyenneGenerale(e.getMoyenneGenerale());
        b.setMoyennePeriode(e.getMoyennePeriode());
        b.setMoyenneAnnuelle(e.getMoyenneAnnuelle());
        b.setRang(e.getRang());
        b.setEffectif(e.getEffectif());
        b.setAppreciationGenerale(e.getAppreciationGenerale());
        b.setMentionGenerale(e.getMentionGenerale());
        b.setDecision(e.getDecision());
        b.setNbAbsencesJustifiees(e.getNbAbsencesJustifiees());
        b.setNbAbsencesInjustifiees(e.getNbAbsencesInjustifiees());
        b.setNbRetards(e.getNbRetards());
        b.setTauxPresence(e.getTauxPresence());
        b.setRegleAssiduite(e.getRegleAssiduite());
        b.setRespecteRegleAssiduite(e.getRespecteRegleAssiduite());
        b.setAvertissementAssiduite(e.getAvertissementAssiduite());
        b.setRetardJours(e.getRetardJours());
        b.setRetardHeures(e.getRetardHeures());
        b.setGenereLe(e.getGenereLe());
        b.setPublieLe(e.getPublieLe());
        b.setGenerePar(e.getGenerePar());
        b.setRectifieLe(e.getRectifieLe());
        b.setMotifRectification(e.getMotifRectification());
        b.setCreePar(e.getCreePar());
        return b;
    }

    private CouponNotesEntity couponEntity(CouponNotes c, UUID tenantId) {
        CouponNotesEntity e = new CouponNotesEntity();
        e.setId(c.getId());
        e.setNumero(c.getNumero());
        e.setBulletinId(c.getBulletinId());
        e.setEleveId(c.getEleveId());
        e.setAnneeId(c.getAnneeId());
        e.setPeriodeId(c.getPeriodeId());
        e.setClasse(c.getClasse());
        e.setDateEdition(c.getDateEdition());
        e.setStatut(c.getStatut() == null ? null : c.getStatut().name());
        e.setNoteMoyenne(c.getNoteMoyenne());
        e.setMention(c.getMention());
        e.setUrlQr(c.getUrlQr());
        e.setCodeBarre(c.getCodeBarre());
        e.setEmpreinte(c.getEmpreinte());
        e.setDetacheLe(c.getDetacheLe());
        e.setDetachePar(c.getDetachePar());
        e.setSignePar(c.getSignePar());
        e.setSigneLe(c.getSigneLe());
        e.setObservation(c.getObservation());
        e.setDateRendu(c.getDateRendu());
        e.setTenantId(tenantId);
        return e;
    }

    private CouponNotes couponToDomain(CouponNotesEntity e) {
        CouponNotes c = new CouponNotes();
        c.setId(e.getId());
        c.setNumero(e.getNumero());
        c.setBulletinId(e.getBulletinId());
        c.setEleveId(e.getEleveId());
        c.setAnneeId(e.getAnneeId());
        c.setPeriodeId(e.getPeriodeId());
        c.setClasse(e.getClasse());
        c.setDateEdition(e.getDateEdition());
        c.setStatut(e.getStatut() == null ? null : StatutCoupon.valueOf(e.getStatut()));
        c.setNoteMoyenne(e.getNoteMoyenne());
        c.setMention(e.getMention());
        c.setUrlQr(e.getUrlQr());
        c.setCodeBarre(e.getCodeBarre());
        c.setEmpreinte(e.getEmpreinte());
        c.setDetacheLe(e.getDetacheLe());
        c.setDetachePar(e.getDetachePar());
        c.setSignePar(e.getSignePar());
        c.setSigneLe(e.getSigneLe());
        c.setObservation(e.getObservation());
        c.setDateRendu(e.getDateRendu());
        return c;
    }
}

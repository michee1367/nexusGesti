package com.nexusgesti.assessment.application.service;

import com.nexusgesti.assessment.domain.AssessmentException;
import com.nexusgesti.assessment.domain.model.*;
import com.nexusgesti.assessment.domain.port.in.AssessmentUseCase;
import com.nexusgesti.assessment.domain.port.out.AssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentService implements AssessmentUseCase {

    private final AssessmentRepository repository;
    private final MoteurCalculMoyenne moteur = new MoteurCalculMoyenne();

    public AssessmentService(AssessmentRepository repository) {
        this.repository = repository;
    }

    // ---------- EVALUATION ----------

    @Override
    public List<Evaluation> listEvaluations(UUID classeId, UUID periodeId) {
        if (classeId != null) {
            return repository.listEvaluationsClasse(classeId);
        }
        return repository.listEvaluations();
    }

    @Override
    public Evaluation getEvaluation(UUID id) {
        return repository.findEvaluation(id)
                .orElseThrow(() -> AssessmentException.notFound("Evaluation", id));
    }

    @Override
    @Transactional
    public Evaluation creerEvaluation(Evaluation evaluation) {
        if (evaluation.getReference() == null || evaluation.getReference().isBlank()) {
            evaluation.setReference(reference("EVAL"));
        }
        if (evaluation.getStatut() == null) {
            evaluation.setStatut(StatutEvaluation.BROUILLON);
        }
        if (evaluation.getCreeLe() == null) {
            evaluation.setCreeLe(Instant.now());
        }
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation updateEvaluation(UUID id, Evaluation evaluation) {
        Evaluation existante = getEvaluation(id);
        evaluation.setIdEvaluation(id);
        if (evaluation.getReference() == null) {
            evaluation.setReference(existante.getReference());
        }
        if (evaluation.getStatut() == null) {
            evaluation.setStatut(existante.getStatut());
        }
        if (evaluation.getCreeLe() == null) {
            evaluation.setCreeLe(existante.getCreeLe());
        }
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation ouvrirEvaluation(UUID id) {
        Evaluation evaluation = getEvaluation(id);
        evaluation.ouvrir();
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation cloturerEvaluation(UUID id) {
        Evaluation evaluation = getEvaluation(id);
        evaluation.cloturer();
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation publierEvaluation(UUID id) {
        Evaluation evaluation = getEvaluation(id);
        evaluation.publier();
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation annulerEvaluation(UUID id, String motif) {
        Evaluation evaluation = getEvaluation(id);
        evaluation.annuler(motif);
        return repository.saveEvaluation(evaluation);
    }

    @Override
    public FeuilleNote getFeuille(UUID evaluationId) {
        FeuilleNote feuille = getEvaluation(evaluationId).getFeuille();
        if (feuille == null) {
            throw AssessmentException.notFound("FeuilleNote", evaluationId);
        }
        return feuille;
    }

    // ---------- NOTES ----------

    @Override
    @Transactional
    public Evaluation saisirNote(UUID evaluationId, UUID eleveId, BigDecimal valeur, UUID par) {
        Evaluation evaluation = getEvaluation(evaluationId);
        evaluation.saisirNote(eleveId, valeur, par);
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation marquerAbsent(UUID evaluationId, UUID eleveId, UUID par) {
        Evaluation evaluation = getEvaluation(evaluationId);
        evaluation.marquerAbsent(eleveId, par);
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation exclureNote(UUID evaluationId, UUID eleveId, String motif) {
        Evaluation evaluation = getEvaluation(evaluationId);
        Note note = evaluation.getNote(eleveId).orElseThrow(() -> AssessmentException.notFound("Note", eleveId));
        note.exclure(motif);
        return repository.saveEvaluation(evaluation);
    }

    @Override
    @Transactional
    public Evaluation contesterNote(UUID evaluationId, UUID eleveId, String motif) {
        Evaluation evaluation = getEvaluation(evaluationId);
        Note note = evaluation.getNote(eleveId).orElseThrow(() -> AssessmentException.notFound("Note", eleveId));
        note.contester(motif);
        return repository.saveEvaluation(evaluation);
    }

    @Override
    public List<Note> listNotes(UUID evaluationId, UUID eleveId) {
        if (evaluationId != null) {
            return repository.listNotesByEvaluation(evaluationId);
        }
        if (eleveId != null) {
            return repository.listNotesEleve(eleveId);
        }
        return repository.listNotes();
    }

    // ---------- BULLETIN ----------

    @Override
    public List<Bulletin> listBulletins(UUID eleveId, UUID classeId, UUID periodeId) {
        return repository.listBulletins().stream()
                .filter(b -> eleveId == null || eleveId.equals(b.getEleveId()))
                .filter(b -> classeId == null || classeId.equals(b.getClasseId()))
                .filter(b -> periodeId == null || periodeId.equals(b.getPeriodeId()))
                .toList();
    }

    @Override
    public Bulletin getBulletin(UUID id) {
        return repository.findBulletin(id)
                .orElseThrow(() -> AssessmentException.notFound("Bulletin", id));
    }

    @Override
    @Transactional
    public Bulletin creerBulletin(Bulletin bulletin, UUID par) {
        if (bulletin.getReference() == null || bulletin.getReference().isBlank()) {
            bulletin.setReference(reference("BUL"));
        }
        bulletin.setStatut(StatutBulletin.BROUILLON);
        bulletin.setVersion(1);
        bulletin.setCreePar(par);
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin ajouterLigne(UUID bulletinId, LigneBulletin ligne) {
        Bulletin bulletin = getBulletin(bulletinId);
        bulletin.ajouterLigne(ligne);
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin definirAbsences(UUID bulletinId, SyntheseAbsences synthese) {
        Bulletin bulletin = getBulletin(bulletinId);
        bulletin.definirSyntheseAbsences(synthese);
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin calculerBulletin(UUID id) {
        Bulletin bulletin = getBulletin(id);
        bulletin.calculer();
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin publierBulletin(UUID id) {
        Bulletin bulletin = getBulletin(id);
        bulletin.publier();
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin genererBulletin(UUID id) {
        Bulletin bulletin = getBulletin(id);
        bulletin.genererDocuments();
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin signerBulletin(UUID id, UUID par) {
        Bulletin bulletin = getBulletin(id);
        bulletin.signer(par);
        return repository.saveBulletin(bulletin);
    }

    @Override
    @Transactional
    public Bulletin rectifierBulletin(UUID id, String motif) {
        Bulletin bulletin = getBulletin(id);
        bulletin.rectifier(motif);
        return repository.saveBulletin(bulletin);
    }

    // ---------- COUPON ----------

    @Override
    public List<CouponNotes> listCoupons(UUID bulletinId) {
        return repository.listCouponsBulletin(bulletinId);
    }

    @Override
    @Transactional
    public CouponNotes creerCoupon(UUID bulletinId, String observation) {
        Bulletin bulletin = getBulletin(bulletinId);
        CouponNotes coupon = new CouponNotes();
        coupon.setBulletinId(bulletinId);
        coupon.setEleveId(bulletin.getEleveId());
        coupon.setAnneeId(bulletin.getAnneeId());
        coupon.setPeriodeId(bulletin.getPeriodeId());
        coupon.setClasse(bulletin.getClasseId() == null ? null : bulletin.getClasseId().toString());
        coupon.setDateEdition(LocalDate.now());
        coupon.setNoteMoyenne(bulletin.getMoyenneGenerale());
        coupon.setMention(bulletin.getMentionGenerale());
        coupon.setObservation(observation);
        long numero = repository.countCouponsAnnee(bulletin.getAnneeId()) + 1;
        coupon.setNumero(String.format("CN-%06d", numero));
        coupon.setCodeBarre(coupon.getNumero());
        coupon.setUrlQr("/api/v1/ped/bulletins/" + bulletinId + "/coupons");
        coupon.calculerEmpreinte();
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponNotes tirerCoupon(UUID couponId) {
        CouponNotes coupon = getCoupon(couponId);
        coupon.tirer();
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponNotes detacherCoupon(UUID couponId, UUID par) {
        CouponNotes coupon = getCoupon(couponId);
        coupon.detacher(par);
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponNotes signerCoupon(UUID couponId, UUID par) {
        CouponNotes coupon = getCoupon(couponId);
        coupon.signer(par);
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponNotes rendreCoupon(UUID couponId) {
        CouponNotes coupon = getCoupon(couponId);
        coupon.rendre();
        return repository.saveCoupon(coupon);
    }

    private CouponNotes getCoupon(UUID id) {
        return repository.findCoupon(id)
                .orElseThrow(() -> AssessmentException.notFound("CouponNotes", id));
    }

    // ---------- MOYENNES ----------

    @Override
    public ResultatMoyenne moyenneEvaluation(UUID evaluationId) {
        return moteur.calculer(repository.listNotesByEvaluation(evaluationId));
    }

    @Override
    public ResultatMoyenne moyenneEleve(UUID eleveId, UUID periodeId) {
        List<Note> notes = repository.listNotesEleve(eleveId);
        if (periodeId != null && !notes.isEmpty()) {
            List<UUID> evaluationIds = notes.stream().map(Note::getEvaluationId).distinct().toList();
            Set<UUID> dansPeriode = repository.listEvaluationsByIds(evaluationIds).stream()
                    .filter(e -> periodeId.equals(e.getPeriodeId()))
                    .map(Evaluation::getIdEvaluation)
                    .collect(Collectors.toSet());
            notes = notes.stream().filter(n -> dansPeriode.contains(n.getEvaluationId())).toList();
        }
        return moteur.calculer(notes);
    }

    @Override
    public List<Classement> classementClasse(UUID classeId) {
        List<Evaluation> evaluations = repository.listEvaluationsClasse(classeId);
        if (evaluations.isEmpty()) {
            return List.of();
        }
        List<UUID> evaluationIds = evaluations.stream().map(Evaluation::getIdEvaluation).toList();
        Map<UUID, List<Note>> parEleve = repository.listNotesByEvaluationIds(evaluationIds).stream()
                .filter(Note::comptePourMoyenne)
                .collect(Collectors.groupingBy(Note::getEleveId));
        return moteur.classer(parEleve);
    }

    private String reference(String prefixe) {
        return prefixe + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

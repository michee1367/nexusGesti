package com.nexusgesti.assessment.domain.port.out;

import com.nexusgesti.assessment.domain.model.Bulletin;
import com.nexusgesti.assessment.domain.model.CouponNotes;
import com.nexusgesti.assessment.domain.model.Evaluation;
import com.nexusgesti.assessment.domain.model.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentRepository {

    Evaluation saveEvaluation(Evaluation evaluation);
    Optional<Evaluation> findEvaluation(UUID id);
    List<Evaluation> listEvaluations();
    List<Evaluation> listEvaluationsClasse(UUID classeId);
    List<Evaluation> listEvaluationsByIds(List<UUID> ids);

    List<Note> listNotes();
    List<Note> listNotesByEvaluation(UUID evaluationId);
    List<Note> listNotesEleve(UUID eleveId);
    List<Note> listNotesByEvaluationIds(List<UUID> evaluationIds);

    Bulletin saveBulletin(Bulletin bulletin);
    Optional<Bulletin> findBulletin(UUID id);
    List<Bulletin> listBulletins();

    CouponNotes saveCoupon(CouponNotes coupon);
    Optional<CouponNotes> findCoupon(UUID id);
    List<CouponNotes> listCouponsBulletin(UUID bulletinId);
    long countCouponsAnnee(UUID anneeId);
}

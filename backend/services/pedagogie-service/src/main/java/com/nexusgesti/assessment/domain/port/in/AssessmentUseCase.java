package com.nexusgesti.assessment.domain.port.in;

import com.nexusgesti.assessment.domain.model.Bulletin;
import com.nexusgesti.assessment.domain.model.Classement;
import com.nexusgesti.assessment.domain.model.CouponNotes;
import com.nexusgesti.assessment.domain.model.Evaluation;
import com.nexusgesti.assessment.domain.model.FeuilleNote;
import com.nexusgesti.assessment.domain.model.LigneBulletin;
import com.nexusgesti.assessment.domain.model.Note;
import com.nexusgesti.assessment.domain.model.ResultatMoyenne;
import com.nexusgesti.assessment.domain.model.SyntheseAbsences;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface AssessmentUseCase {

    List<Evaluation> listEvaluations(UUID classeId, UUID periodeId);
    Evaluation getEvaluation(UUID id);
    Evaluation creerEvaluation(Evaluation evaluation);
    Evaluation updateEvaluation(UUID id, Evaluation evaluation);
    Evaluation ouvrirEvaluation(UUID id);
    Evaluation cloturerEvaluation(UUID id);
    Evaluation publierEvaluation(UUID id);
    Evaluation annulerEvaluation(UUID id, String motif);
    FeuilleNote getFeuille(UUID evaluationId);

    Evaluation saisirNote(UUID evaluationId, UUID eleveId, BigDecimal valeur, UUID par);
    Evaluation marquerAbsent(UUID evaluationId, UUID eleveId, UUID par);
    Evaluation exclureNote(UUID evaluationId, UUID eleveId, String motif);
    Evaluation contesterNote(UUID evaluationId, UUID eleveId, String motif);
    List<Note> listNotes(UUID evaluationId, UUID eleveId);

    List<Bulletin> listBulletins(UUID eleveId, UUID classeId, UUID periodeId);
    Bulletin getBulletin(UUID id);
    Bulletin creerBulletin(Bulletin bulletin, UUID par);
    Bulletin ajouterLigne(UUID bulletinId, LigneBulletin ligne);
    Bulletin definirAbsences(UUID bulletinId, SyntheseAbsences synthese);
    Bulletin calculerBulletin(UUID id);
    Bulletin publierBulletin(UUID id);
    Bulletin genererBulletin(UUID id);
    Bulletin signerBulletin(UUID id, UUID par);
    Bulletin rectifierBulletin(UUID id, String motif);

    List<CouponNotes> listCoupons(UUID bulletinId);
    CouponNotes creerCoupon(UUID bulletinId, String observation);
    CouponNotes tirerCoupon(UUID couponId);
    CouponNotes detacherCoupon(UUID couponId, UUID par);
    CouponNotes signerCoupon(UUID couponId, UUID par);
    CouponNotes rendreCoupon(UUID couponId);

    ResultatMoyenne moyenneEvaluation(UUID evaluationId);
    ResultatMoyenne moyenneEleve(UUID eleveId, UUID periodeId);
    List<Classement> classementClasse(UUID classeId);
}

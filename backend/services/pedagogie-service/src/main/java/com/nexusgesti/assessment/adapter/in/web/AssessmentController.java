package com.nexusgesti.assessment.adapter.in.web;

import com.nexusgesti.assessment.domain.model.*;
import com.nexusgesti.assessment.domain.port.in.AssessmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AssessmentController {

    private final AssessmentUseCase assessmentUseCase;

    public AssessmentController(AssessmentUseCase assessmentUseCase) {
        this.assessmentUseCase = assessmentUseCase;
    }

    // ---------- EVALUATIONS ----------

    @GetMapping("/evaluations")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Evaluation> listEvaluations(@RequestParam(required = false) UUID classeId,
                                            @RequestParam(required = false) UUID periodeId) {
        return assessmentUseCase.listEvaluations(classeId, periodeId);
    }

    @PostMapping("/evaluations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Evaluation> creerEvaluation(@RequestBody Evaluation evaluation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentUseCase.creerEvaluation(evaluation));
    }

    @GetMapping("/evaluations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Evaluation getEvaluation(@PathVariable UUID id) {
        return assessmentUseCase.getEvaluation(id);
    }

    @PutMapping("/evaluations/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation updateEvaluation(@PathVariable UUID id, @RequestBody Evaluation evaluation) {
        return assessmentUseCase.updateEvaluation(id, evaluation);
    }

    @PostMapping("/evaluations/{id}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation ouvrirEvaluation(@PathVariable UUID id) {
        return assessmentUseCase.ouvrirEvaluation(id);
    }

    @PostMapping("/evaluations/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation cloturerEvaluation(@PathVariable UUID id) {
        return assessmentUseCase.cloturerEvaluation(id);
    }

    @PostMapping("/evaluations/{id}/publier")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation publierEvaluation(@PathVariable UUID id) {
        return assessmentUseCase.publierEvaluation(id);
    }

    @PostMapping("/evaluations/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation annulerEvaluation(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return assessmentUseCase.annulerEvaluation(id, request == null ? null : request.motif());
    }

    @GetMapping("/evaluations/{id}/feuille")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public FeuilleNote getFeuille(@PathVariable UUID id) {
        return assessmentUseCase.getFeuille(id);
    }

    @GetMapping("/evaluations/{id}/notes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Note> listNotesEvaluation(@PathVariable UUID id) {
        return assessmentUseCase.listNotes(id, null);
    }

    @PostMapping("/evaluations/{id}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation saisirNote(@PathVariable UUID id, @RequestBody SaisirNoteRequest request,
                                 Authentication authentication) {
        return assessmentUseCase.saisirNote(id, request.eleveId(), request.valeur(), auteur(authentication));
    }

    @PostMapping("/evaluations/{id}/notes/absence")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation marquerAbsent(@PathVariable UUID id, @RequestBody EleveRequest request,
                                    Authentication authentication) {
        return assessmentUseCase.marquerAbsent(id, request.eleveId(), auteur(authentication));
    }

    @PostMapping("/evaluations/{id}/notes/exclusion")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation exclureNote(@PathVariable UUID id, @RequestBody MotifEleveRequest request) {
        return assessmentUseCase.exclureNote(id, request.eleveId(), request.motif());
    }

    @PostMapping("/evaluations/{id}/notes/contestation")
    @PreAuthorize("hasRole('ADMIN')")
    public Evaluation contesterNote(@PathVariable UUID id, @RequestBody MotifEleveRequest request) {
        return assessmentUseCase.contesterNote(id, request.eleveId(), request.motif());
    }

    // ---------- NOTES ----------

    @GetMapping("/notes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Note> listNotes(@RequestParam(required = false) UUID evaluationId,
                                @RequestParam(required = false) UUID eleveId) {
        return assessmentUseCase.listNotes(evaluationId, eleveId);
    }

    // ---------- BULLETINS ----------

    @GetMapping("/bulletins")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Bulletin> listBulletins(@RequestParam(required = false) UUID eleveId,
                                        @RequestParam(required = false) UUID classeId,
                                        @RequestParam(required = false) UUID periodeId) {
        return assessmentUseCase.listBulletins(eleveId, classeId, periodeId);
    }

    @PostMapping("/bulletins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bulletin> creerBulletin(@RequestBody Bulletin bulletin, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentUseCase.creerBulletin(bulletin, auteur(authentication)));
    }

    @GetMapping("/bulletins/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Bulletin getBulletin(@PathVariable UUID id) {
        return assessmentUseCase.getBulletin(id);
    }

    @PostMapping("/bulletins/{id}/lignes")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin ajouterLigne(@PathVariable UUID id, @RequestBody LigneBulletin ligne) {
        return assessmentUseCase.ajouterLigne(id, ligne);
    }

    @PostMapping("/bulletins/{id}/absences")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin definirAbsences(@PathVariable UUID id, @RequestBody SyntheseAbsences synthese) {
        return assessmentUseCase.definirAbsences(id, synthese);
    }

    @PostMapping("/bulletins/{id}/calculer")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin calculerBulletin(@PathVariable UUID id) {
        return assessmentUseCase.calculerBulletin(id);
    }

    @PostMapping("/bulletins/{id}/publier")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin publierBulletin(@PathVariable UUID id) {
        return assessmentUseCase.publierBulletin(id);
    }

    @PostMapping("/bulletins/{id}/generer")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin genererBulletin(@PathVariable UUID id) {
        return assessmentUseCase.genererBulletin(id);
    }

    @PostMapping("/bulletins/{id}/signer")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin signerBulletin(@PathVariable UUID id, Authentication authentication) {
        return assessmentUseCase.signerBulletin(id, auteur(authentication));
    }

    @PostMapping("/bulletins/{id}/rectifier")
    @PreAuthorize("hasRole('ADMIN')")
    public Bulletin rectifierBulletin(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return assessmentUseCase.rectifierBulletin(id, request == null ? null : request.motif());
    }

    // ---------- COUPONS ----------

    @GetMapping("/bulletins/{id}/coupons")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CouponNotes> listCoupons(@PathVariable UUID id) {
        return assessmentUseCase.listCoupons(id);
    }

    @PostMapping("/bulletins/{id}/coupons")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CouponNotes> creerCoupon(@PathVariable UUID id,
                                                   @RequestBody(required = false) ObservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assessmentUseCase.creerCoupon(id, request == null ? null : request.observation()));
    }

    @PostMapping("/bulletins/{id}/coupons/{couponId}/tirer")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponNotes tirerCoupon(@PathVariable UUID id, @PathVariable UUID couponId) {
        return assessmentUseCase.tirerCoupon(couponId);
    }

    @PostMapping("/bulletins/{id}/coupons/{couponId}/detacher")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponNotes detacherCoupon(@PathVariable UUID id, @PathVariable UUID couponId,
                                      Authentication authentication) {
        return assessmentUseCase.detacherCoupon(couponId, auteur(authentication));
    }

    @PostMapping("/bulletins/{id}/coupons/{couponId}/signer")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponNotes signerCoupon(@PathVariable UUID id, @PathVariable UUID couponId,
                                    Authentication authentication) {
        return assessmentUseCase.signerCoupon(couponId, auteur(authentication));
    }

    @PostMapping("/bulletins/{id}/coupons/{couponId}/rendre")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponNotes rendreCoupon(@PathVariable UUID id, @PathVariable UUID couponId) {
        return assessmentUseCase.rendreCoupon(couponId);
    }

    // ---------- MOYENNES ----------

    @GetMapping("/moyennes/evaluation/{evaluationId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResultatMoyenne moyenneEvaluation(@PathVariable UUID evaluationId) {
        return assessmentUseCase.moyenneEvaluation(evaluationId);
    }

    @GetMapping("/moyennes/eleve/{eleveId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResultatMoyenne moyenneEleve(@PathVariable UUID eleveId,
                                        @RequestParam(required = false) UUID periodeId) {
        return assessmentUseCase.moyenneEleve(eleveId, periodeId);
    }

    @GetMapping("/moyennes/classe/{classeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Classement> classementClasse(@PathVariable UUID classeId) {
        return assessmentUseCase.classementClasse(classeId);
    }

    private UUID auteur(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}

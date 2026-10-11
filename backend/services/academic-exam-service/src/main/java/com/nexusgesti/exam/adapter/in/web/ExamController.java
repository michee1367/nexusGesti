package com.nexusgesti.exam.adapter.in.web;

import com.nexusgesti.exam.domain.model.*;
import com.nexusgesti.exam.domain.port.in.ExamUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ExamController {

    private final ExamUseCase examUseCase;

    public ExamController(ExamUseCase examUseCase) {
        this.examUseCase = examUseCase;
    }

    // ---------- SESSIONS ----------

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<SessionExamen> listSessions(@RequestParam(required = false) UUID anneeId,
                                            @RequestParam(required = false) UUID classeId) {
        return examUseCase.listSessions(anneeId, classeId);
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionExamen> creerSession(@RequestBody SessionExamen session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.creerSession(session));
    }

    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public SessionExamen getSession(@PathVariable UUID id) {
        return examUseCase.getSession(id);
    }

    @PostMapping("/sessions/{id}/programmer")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionExamen programmerSession(@PathVariable UUID id) {
        return examUseCase.programmerSession(id);
    }

    @PostMapping("/sessions/{id}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionExamen ouvrirSession(@PathVariable UUID id) {
        return examUseCase.ouvrirSession(id);
    }

    @PostMapping("/sessions/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionExamen cloturerSession(@PathVariable UUID id) {
        return examUseCase.cloturerSession(id);
    }

    @PostMapping("/sessions/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionExamen annulerSession(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return examUseCase.annulerSession(id, request == null ? null : request.motif());
    }

    // ---------- EPREUVES ----------

    @GetMapping("/sessions/{sessionId}/epreuves")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Epreuve> listEpreuves(@PathVariable UUID sessionId) {
        return examUseCase.listEpreuves(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/epreuves")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Epreuve> ajouterEpreuve(@PathVariable UUID sessionId, @RequestBody Epreuve epreuve) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.ajouterEpreuve(sessionId, epreuve));
    }

    @GetMapping("/epreuves/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Epreuve getEpreuve(@PathVariable UUID id) {
        return examUseCase.getEpreuve(id);
    }

    @PostMapping("/epreuves/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public Epreuve validerEpreuve(@PathVariable UUID id) {
        return examUseCase.validerEpreuve(id);
    }

    @PostMapping("/epreuves/{id}/hors-moyenne")
    @PreAuthorize("hasRole('ADMIN')")
    public Epreuve declarerEpreuveHorsMoyenne(@PathVariable UUID id) {
        return examUseCase.declarerEpreuveHorsMoyenne(id);
    }

    @PostMapping("/epreuves/{id}/periode")
    @PreAuthorize("hasRole('ADMIN')")
    public Epreuve rattacherEpreuvePeriode(@PathVariable UUID id, @RequestBody EleveRequest periodeId) {
        return examUseCase.rattacherEpreuvePeriode(id, periodeId == null ? null : periodeId.eleveId());
    }

    // ---------- CRENEAUX ----------

    @GetMapping("/sessions/{sessionId}/creneaux")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Creneau> listCreneaux(@PathVariable UUID sessionId) {
        return examUseCase.listCreneaux(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/creneaux")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Creneau> ajouterCreneau(@PathVariable UUID sessionId, @RequestBody Creneau creneau) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.ajouterCreneau(sessionId, creneau));
    }

    // ---------- CONVOCATIONS ----------

    @GetMapping("/sessions/{sessionId}/convocations")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Convocation> listConvocations(@PathVariable UUID sessionId) {
        return examUseCase.listConvocations(sessionId);
    }

    @PostMapping("/sessions/{sessionId}/convocations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Convocation> creerConvocation(@PathVariable UUID sessionId,
                                                        @RequestBody Convocation convocation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.creerConvocation(sessionId, convocation));
    }

    @GetMapping("/convocations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Convocation getConvocation(@PathVariable UUID id) {
        return examUseCase.getConvocation(id);
    }

    @PostMapping("/convocations/{id}/emettre")
    @PreAuthorize("hasRole('ADMIN')")
    public Convocation emettreConvocation(@PathVariable UUID id) {
        return examUseCase.emettreConvocation(id);
    }

    @PostMapping("/convocations/{id}/notifier")
    @PreAuthorize("hasRole('ADMIN')")
    public Convocation notifierConvocation(@PathVariable UUID id, @RequestBody(required = false) CanalRequest request) {
        return examUseCase.notifierConvocation(id, request == null ? null : request.canal());
    }

    // ---------- NOTES ----------

    @GetMapping("/notes-examen")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<NoteExamen> listNotes(@RequestParam(required = false) UUID epreuveId,
                                      @RequestParam(required = false) UUID eleveId) {
        if (epreuveId != null) {
            return examUseCase.listNotesEpreuve(epreuveId);
        }
        if (eleveId != null) {
            return examUseCase.listNotesEleve(eleveId);
        }
        return List.of();
    }

    @PostMapping("/epreuves/{epreuveId}/notes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoteExamen> saisirNote(@PathVariable UUID epreuveId, @RequestBody SaisirNoteRequest request,
                                                 Authentication authentication) {
        UUID correcteur = request.correcteur() != null ? request.correcteur() : auteur(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examUseCase.saisirNoteExamen(epreuveId, request.eleveId(), request.valeur(), correcteur));
    }

    @PostMapping("/epreuves/{epreuveId}/notes/absence")
    @PreAuthorize("hasRole('ADMIN')")
    public NoteExamen marquerAbsent(@PathVariable UUID epreuveId, @RequestBody EleveRequest request,
                                    Authentication authentication) {
        return examUseCase.marquerAbsentExamen(epreuveId, request.eleveId(), auteur(authentication));
    }

    @PostMapping("/notes-examen/{id}/exclure")
    @PreAuthorize("hasRole('ADMIN')")
    public NoteExamen exclureNote(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return examUseCase.exclureNoteExamen(id, request == null ? null : request.motif());
    }

    @PostMapping("/notes-examen/{id}/contester")
    @PreAuthorize("hasRole('ADMIN')")
    public NoteExamen contesterNote(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return examUseCase.contesterNoteExamen(id, request == null ? null : request.motif());
    }

    @PostMapping("/notes-examen/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public NoteExamen validerNote(@PathVariable UUID id) {
        return examUseCase.validerNoteExamen(id);
    }

    // ---------- CORRECTIONS ----------

    @PostMapping("/epreuves/{epreuveId}/corrections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Correction> ouvrirCorrection(@PathVariable UUID epreuveId,
                                                       @RequestBody(required = false) EleveRequest request,
                                                       Authentication authentication) {
        UUID correcteur = request != null && request.eleveId() != null ? request.eleveId() : auteur(authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.ouvrirCorrection(epreuveId, correcteur));
    }

    @GetMapping("/corrections/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Correction getCorrection(@PathVariable UUID id) {
        return examUseCase.getCorrection(id);
    }

    @PostMapping("/corrections/{id}/calculer")
    @PreAuthorize("hasRole('ADMIN')")
    public Correction calculerCorrection(@PathVariable UUID id) {
        return examUseCase.calculerCorrection(id);
    }

    @PostMapping("/corrections/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Correction cloturerCorrection(@PathVariable UUID id) {
        return examUseCase.cloturerCorrection(id);
    }

    // ---------- DELIBERATIONS ----------

    @GetMapping("/deliberations")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Deliberation> listDeliberations(@RequestParam(required = false) UUID sessionId) {
        return examUseCase.listDeliberations(sessionId);
    }

    @PostMapping("/deliberations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Deliberation> creerDeliberation(@RequestBody Deliberation deliberation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.creerDeliberation(deliberation));
    }

    @GetMapping("/deliberations/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Deliberation getDeliberation(@PathVariable UUID id) {
        return examUseCase.getDeliberation(id);
    }

    @PostMapping("/deliberations/{id}/planifier")
    @PreAuthorize("hasRole('ADMIN')")
    public Deliberation planifierDeliberation(@PathVariable UUID id) {
        return examUseCase.planifierDeliberation(id);
    }

    @PostMapping("/deliberations/{id}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public Deliberation ouvrirDeliberation(@PathVariable UUID id) {
        return examUseCase.ouvrirDeliberation(id);
    }

    @PostMapping("/deliberations/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Deliberation cloturerDeliberation(@PathVariable UUID id) {
        return examUseCase.cloturerDeliberation(id);
    }

    @PostMapping("/deliberations/{id}/decisions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Decision> decider(@PathVariable UUID id, @RequestBody DeciderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examUseCase.decider(id, request.classeId(), request.eleveId()));
    }

    @PostMapping("/deliberations/{id}/decisions-ue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DecisionUE> ajouterDecisionUE(@PathVariable UUID id, @RequestBody DecisionUE decisionUe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.ajouterDecisionUE(id, decisionUe));
    }

    // ---------- RESULTATS ----------

    @PostMapping("/decisions/{decisionId}/resultats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResultatOfficiel> publierResultat(@PathVariable UUID decisionId,
                                                            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examUseCase.publierResultat(decisionId, auteur(authentication)));
    }

    @GetMapping("/resultats/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResultatOfficiel getResultat(@PathVariable UUID id) {
        return examUseCase.getResultat(id);
    }

    // ---------- RATTRAPAGES ----------

    @GetMapping("/rattrapages")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Rattrapage> listRattrapages(@RequestParam(required = false) UUID eleveId) {
        return examUseCase.listRattrapages(eleveId);
    }

    @PostMapping("/rattrapages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Rattrapage> creerRattrapage(@RequestBody Rattrapage rattrapage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examUseCase.creerRattrapage(rattrapage));
    }

    @GetMapping("/rattrapages/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Rattrapage getRattrapage(@PathVariable UUID id) {
        return examUseCase.getRattrapage(id);
    }

    @PostMapping("/rattrapages/{id}/exempt")
    @PreAuthorize("hasRole('ADMIN')")
    public Rattrapage declarerExempt(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return examUseCase.declarerExemptRattrapage(id, request == null ? null : request.motif());
    }

    // ---------- PROCES VERBAUX ----------

    @GetMapping("/proces-verbaux")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<ProcesVerbal> listProcesVerbaux(@RequestParam(required = false) UUID sessionId) {
        return examUseCase.listProcesVerbaux(sessionId);
    }

    @PostMapping("/proces-verbaux")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProcesVerbal> creerProcesVerbal(@RequestBody ProcesVerbal procesVerbal,
                                                          Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examUseCase.creerProcesVerbal(procesVerbal, auteur(authentication)));
    }

    @GetMapping("/proces-verbaux/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ProcesVerbal getProcesVerbal(@PathVariable UUID id) {
        return examUseCase.getProcesVerbal(id);
    }

    @PostMapping("/proces-verbaux/{id}/soumettre")
    @PreAuthorize("hasRole('ADMIN')")
    public ProcesVerbal soumettreProcesVerbal(@PathVariable UUID id) {
        return examUseCase.soumettreProcesVerbal(id);
    }

    @PostMapping("/proces-verbaux/{id}/signer")
    @PreAuthorize("hasRole('ADMIN')")
    public ProcesVerbal signerProcesVerbal(@PathVariable UUID id, @RequestBody(required = false) RoleRequest request,
                                           Authentication authentication) {
        return examUseCase.signerProcesVerbal(id, auteur(authentication), request == null ? null : request.role());
    }

    @PostMapping("/proces-verbaux/{id}/homologuer")
    @PreAuthorize("hasRole('ADMIN')")
    public ProcesVerbal homologuerProcesVerbal(@PathVariable UUID id) {
        return examUseCase.homologuerProcesVerbal(id);
    }

    @PostMapping("/proces-verbaux/{id}/contester")
    @PreAuthorize("hasRole('ADMIN')")
    public ProcesVerbal contesterProcesVerbal(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return examUseCase.contesterProcesVerbal(id, request == null ? null : request.motif());
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

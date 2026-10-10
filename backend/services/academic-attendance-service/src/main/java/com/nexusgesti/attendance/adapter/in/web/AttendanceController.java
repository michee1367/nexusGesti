package com.nexusgesti.attendance.adapter.in.web;

import com.nexusgesti.attendance.domain.model.FeuillePresence;
import com.nexusgesti.attendance.domain.model.Justificatif;
import com.nexusgesti.attendance.domain.model.Seance;
import com.nexusgesti.attendance.domain.model.SuiviAssiduite;
import com.nexusgesti.attendance.domain.port.in.AttendanceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class AttendanceController {

    private final AttendanceUseCase attendanceUseCase;

    public AttendanceController(AttendanceUseCase attendanceUseCase) {
        this.attendanceUseCase = attendanceUseCase;
    }

    // ---------- SEANCES ----------

    @GetMapping("/seances")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Seance> listSeances(@RequestParam(required = false) LocalDate date) {
        return attendanceUseCase.listSeances(date);
    }

    @PostMapping("/seances")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Seance> creerSeance(@RequestBody Seance seance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceUseCase.creerSeance(seance));
    }

    @GetMapping("/seances/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Seance getSeance(@PathVariable UUID id) {
        return attendanceUseCase.getSeance(id);
    }

    @PutMapping("/seances/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Seance updateSeance(@PathVariable UUID id, @RequestBody Seance seance) {
        return attendanceUseCase.updateSeance(id, seance);
    }

    @PostMapping("/seances/{id}/demarrer")
    @PreAuthorize("hasRole('ADMIN')")
    public Seance demarrerSeance(@PathVariable UUID id) {
        return attendanceUseCase.demarrerSeance(id);
    }

    @PostMapping("/seances/{id}/terminer")
    @PreAuthorize("hasRole('ADMIN')")
    public Seance terminerSeance(@PathVariable UUID id) {
        return attendanceUseCase.terminerSeance(id);
    }

    @PostMapping("/seances/{id}/reporter")
    @PreAuthorize("hasRole('ADMIN')")
    public Seance reporterSeance(@PathVariable UUID id) {
        return attendanceUseCase.reporterSeance(id);
    }

    @PostMapping("/seances/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Seance annulerSeance(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return attendanceUseCase.annulerSeance(id, request == null ? null : request.motif());
    }

    // ---------- APPELS / FEUILLES ----------

    @GetMapping("/appels")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<FeuillePresence> listFeuilles() {
        return attendanceUseCase.listFeuilles();
    }

    @PostMapping("/appels")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FeuillePresence> creerFeuille(@RequestBody FeuillePresence feuille) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceUseCase.creerFeuille(feuille));
    }

    @GetMapping("/appels/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public FeuillePresence getFeuille(@PathVariable UUID id) {
        return attendanceUseCase.getFeuille(id);
    }

    @GetMapping("/appels/{id}/presences")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<com.nexusgesti.attendance.domain.model.PresenceEleve> listPresences(@PathVariable UUID id) {
        return attendanceUseCase.getFeuille(id).getPresences();
    }

    @PostMapping("/appels/{id}/pointer")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence pointer(@PathVariable UUID id, @RequestBody PointerRequest request,
                                   Authentication authentication) {
        return attendanceUseCase.pointer(id, request.eleveId(), request.statut(), auteur(authentication));
    }

    @PostMapping("/appels/{id}/appel-global")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence faireAppelGlobal(@PathVariable UUID id, @RequestBody AppelGlobalRequest request,
                                            Authentication authentication) {
        return attendanceUseCase.faireAppelGlobal(id, request.statut(), auteur(authentication));
    }

    @PostMapping("/appels/{id}/absence")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence marquerAbsence(@PathVariable UUID id, @RequestBody AbsenceRequest request,
                                          Authentication authentication) {
        return attendanceUseCase.marquerAbsence(id, request.eleveId(), request.typeAbsence(), auteur(authentication));
    }

    @PostMapping("/appels/{id}/retard")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence marquerRetard(@PathVariable UUID id, @RequestBody RetardRequest request,
                                         Authentication authentication) {
        int minutes = request.minutes() == null ? 0 : request.minutes();
        return attendanceUseCase.marquerRetard(id, request.eleveId(), minutes, auteur(authentication));
    }

    @PostMapping("/appels/{id}/soumettre")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence soumettre(@PathVariable UUID id) {
        return attendanceUseCase.soumettreFeuille(id);
    }

    @PostMapping("/appels/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence valider(@PathVariable UUID id, Authentication authentication) {
        return attendanceUseCase.validerFeuille(id, auteur(authentication));
    }

    @PostMapping("/appels/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public FeuillePresence cloturer(@PathVariable UUID id) {
        return attendanceUseCase.cloturerFeuille(id);
    }

    // ---------- ABSENCES / JUSTIFICATIFS ----------

    @GetMapping("/absences/justificatifs")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Justificatif> listJustificatifs() {
        return attendanceUseCase.listJustificatifs();
    }

    @PostMapping("/absences/justificatifs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Justificatif> deposerJustificatif(@RequestBody Justificatif justificatif) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceUseCase.deposerJustificatif(justificatif));
    }

    @GetMapping("/absences/justificatifs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Justificatif getJustificatif(@PathVariable UUID id) {
        return attendanceUseCase.getJustificatif(id);
    }

    @PostMapping("/absences/justificatifs/{id}/accepter")
    @PreAuthorize("hasRole('ADMIN')")
    public Justificatif accepterJustificatif(@PathVariable UUID id, Authentication authentication) {
        return attendanceUseCase.accepterJustificatif(id, auteur(authentication));
    }

    @PostMapping("/absences/justificatifs/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public Justificatif rejeterJustificatif(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request,
                                            Authentication authentication) {
        return attendanceUseCase.rejeterJustificatif(id, request == null ? null : request.motif(),
                auteur(authentication));
    }

    // ---------- ASSIDUITE ----------

    @GetMapping("/assiduite/{eleveId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public SuiviAssiduite getSuivi(@PathVariable UUID eleveId,
                                   @RequestParam(required = false) UUID anneeId,
                                   @RequestParam(required = false) UUID periodeId) {
        return attendanceUseCase.getSuivi(eleveId, anneeId, periodeId);
    }

    @GetMapping("/assiduite/classe/{classeId}/taux")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public BigDecimal getTauxClasse(@PathVariable UUID classeId) {
        return attendanceUseCase.getTauxClasse(classeId);
    }

    @GetMapping("/assiduite/registre")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<FeuillePresence> getRegistre(@RequestParam(required = false) UUID classeId,
                                             @RequestParam(required = false) LocalDate date) {
        return attendanceUseCase.getRegistre(classeId, date);
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

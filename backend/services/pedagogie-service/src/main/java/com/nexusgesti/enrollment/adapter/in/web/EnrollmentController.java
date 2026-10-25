package com.nexusgesti.enrollment.adapter.in.web;

import com.nexusgesti.enrollment.domain.model.*;
import com.nexusgesti.enrollment.domain.port.in.EnrollmentUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class EnrollmentController {

    private final EnrollmentUseCase enrollmentUseCase;

    public EnrollmentController(EnrollmentUseCase enrollmentUseCase) {
        this.enrollmentUseCase = enrollmentUseCase;
    }

    // ---------- INSCRIPTIONS UE ----------

    @GetMapping("/inscriptions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<InscriptionUE> listInscriptions() {
        return enrollmentUseCase.listInscriptions();
    }

    @PostMapping("/inscriptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InscriptionUE> creerInscription(@RequestBody InscriptionUE inscription) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentUseCase.creerInscription(inscription));
    }

    @GetMapping("/inscriptions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public InscriptionUE getInscription(@PathVariable UUID id) {
        return enrollmentUseCase.getInscription(id);
    }

    @PutMapping("/inscriptions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public InscriptionUE updateInscription(@PathVariable UUID id, @RequestBody InscriptionUE inscription) {
        return enrollmentUseCase.updateInscription(id, inscription);
    }

    @PostMapping("/inscriptions/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public InscriptionUE validerInscription(@PathVariable UUID id, Authentication authentication) {
        return enrollmentUseCase.validerInscription(id, auteur(authentication));
    }

    @PostMapping("/inscriptions/{id}/refuser")
    @PreAuthorize("hasRole('ADMIN')")
    public InscriptionUE refuserInscription(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return enrollmentUseCase.refuserInscription(id, request.motif());
    }

    @PostMapping("/inscriptions/{id}/liste-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public InscriptionUE mettreEnListeAttente(@PathVariable UUID id, @RequestBody RangRequest request) {
        return enrollmentUseCase.mettreEnListeAttente(id, request.rang() == null ? 0 : request.rang());
    }

    @PostMapping("/inscriptions/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public InscriptionUE annulerInscription(@PathVariable UUID id) {
        return enrollmentUseCase.annulerInscription(id);
    }

    // ---------- GROUPES ----------

    @GetMapping("/inscriptions/groupes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Groupe> listGroupes() {
        return enrollmentUseCase.listGroupes();
    }

    @PostMapping("/inscriptions/groupes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Groupe> creerGroupe(@RequestBody Groupe groupe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentUseCase.creerGroupe(groupe));
    }

    @GetMapping("/inscriptions/groupes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Groupe getGroupe(@PathVariable UUID id) {
        return enrollmentUseCase.getGroupe(id);
    }

    @PutMapping("/inscriptions/groupes/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Groupe updateGroupe(@PathVariable UUID id, @RequestBody Groupe groupe) {
        return enrollmentUseCase.updateGroupe(id, groupe);
    }

    @PostMapping("/inscriptions/groupes/{id}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public Groupe ouvrirGroupe(@PathVariable UUID id) {
        return enrollmentUseCase.ouvrirGroupe(id);
    }

    @PostMapping("/inscriptions/groupes/{id}/clore")
    @PreAuthorize("hasRole('ADMIN')")
    public Groupe cloreGroupe(@PathVariable UUID id) {
        return enrollmentUseCase.cloreGroupe(id);
    }

    @GetMapping("/inscriptions/groupes/{id}/membres")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<MembreGroupe> listMembres(@PathVariable UUID id) {
        return enrollmentUseCase.listMembres(id);
    }

    @PostMapping("/inscriptions/groupes/{id}/membres")
    @PreAuthorize("hasRole('ADMIN')")
    public Groupe ajouterMembre(@PathVariable UUID id, @RequestBody MembreGroupe membre) {
        return enrollmentUseCase.ajouterMembre(id, membre);
    }

    @DeleteMapping("/inscriptions/groupes/{id}/membres/{membreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Groupe exclureMembre(@PathVariable UUID id, @PathVariable UUID membreId) {
        return enrollmentUseCase.exclureMembre(id, membreId);
    }

    // ---------- ADMISSIONS ----------

    @GetMapping("/admissions/regles")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<RegleInscription> listRegles() {
        return enrollmentUseCase.listRegles();
    }

    @PostMapping("/admissions/regles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegleInscription> creerRegle(@RequestBody RegleInscription regle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentUseCase.creerRegle(regle));
    }

    @GetMapping("/admissions/regles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public RegleInscription getRegle(@PathVariable UUID id) {
        return enrollmentUseCase.getRegle(id);
    }

    @PutMapping("/admissions/regles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RegleInscription updateRegle(@PathVariable UUID id, @RequestBody RegleInscription regle) {
        return enrollmentUseCase.updateRegle(id, regle);
    }

    @DeleteMapping("/admissions/regles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimerRegle(@PathVariable UUID id) {
        enrollmentUseCase.supprimerRegle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admissions/evaluer")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public DecisionInscription evaluer(@RequestBody Candidat candidat) {
        return enrollmentUseCase.evaluer(candidat);
    }

    @GetMapping("/admissions/parcours/{eleveId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ParcoursAcademique getParcours(@PathVariable UUID eleveId,
                                          @RequestParam(required = false) UUID programmeId,
                                          @RequestParam(required = false) BigDecimal creditsRequis) {
        return enrollmentUseCase.getParcours(eleveId, programmeId, creditsRequis);
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

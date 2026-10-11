package com.nexusgesti.fees.adapter.in.web;

import com.nexusgesti.fees.domain.model.*;
import com.nexusgesti.fees.domain.port.in.FeesUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class FeesController {

    private final FeesUseCase feesUseCase;

    public FeesController(FeesUseCase feesUseCase) {
        this.feesUseCase = feesUseCase;
    }

    // ---------- GRILLES TARIFAIRES ----------

    @GetMapping("/frais/grilles")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<GrilleTarifaire> listGrilles(@RequestParam(required = false) UUID anneeId,
                                             @RequestParam(required = false) String profil) {
        return feesUseCase.listGrilles(anneeId, profil);
    }

    @GetMapping("/frais/grilles/applicable")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public GrilleTarifaire getGrilleApplicable(@RequestParam(required = false) UUID anneeId,
                                               @RequestParam(required = false) String profil) {
        return feesUseCase.getGrilleApplicable(anneeId, profil);
    }

    @PostMapping("/frais/grilles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleTarifaire> creerGrille(@RequestBody GrilleTarifaire grille) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesUseCase.creerGrille(grille));
    }

    @GetMapping("/frais/grilles/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public GrilleTarifaire getGrille(@PathVariable UUID id) {
        return feesUseCase.getGrille(id);
    }

    @PostMapping("/frais/grilles/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleTarifaire activerGrille(@PathVariable UUID id) {
        return feesUseCase.activerGrille(id);
    }

    @PostMapping("/frais/grilles/{id}/figer")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleTarifaire figerGrille(@PathVariable UUID id) {
        return feesUseCase.figerGrille(id);
    }

    @PostMapping("/frais/grilles/{id}/archiver")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleTarifaire archiverGrille(@PathVariable UUID id) {
        return feesUseCase.archiverGrille(id);
    }

    @PostMapping("/frais/grilles/{id}/lignes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleTarifaire> ajouterLigne(@PathVariable UUID id, @RequestBody LigneGrille ligne) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesUseCase.ajouterLigne(id, ligne));
    }

    @PostMapping("/frais/grilles/{id}/reductions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleTarifaire> ajouterReduction(@PathVariable UUID id, @RequestBody Reduction reduction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesUseCase.ajouterReduction(id, reduction));
    }

    // ---------- AVIS DE PAIEMENT ----------

    @GetMapping("/frais/avis")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<AvisPaiement> listAvis(@RequestParam(required = false) UUID eleveId,
                                       @RequestParam(required = false) UUID anneeId) {
        return feesUseCase.listAvis(eleveId, anneeId);
    }

    @PostMapping("/frais/avis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvisPaiement> creerAvis(@RequestBody AvisPaiement avis) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesUseCase.creerAvis(avis));
    }

    @GetMapping("/frais/avis/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public AvisPaiement getAvis(@PathVariable UUID id) {
        return feesUseCase.getAvis(id);
    }

    @PostMapping("/frais/avis/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public AvisPaiement validerAvis(@PathVariable UUID id) {
        return feesUseCase.validerAvis(id);
    }

    @PostMapping("/frais/avis/{id}/emettre")
    @PreAuthorize("hasRole('ADMIN')")
    public AvisPaiement emettreAvis(@PathVariable UUID id) {
        return feesUseCase.emettreAvis(id);
    }

    @PostMapping("/frais/avis/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public AvisPaiement annulerAvis(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return feesUseCase.annulerAvis(id, request == null ? null : request.motif());
    }

    @PostMapping("/frais/avis/{id}/remise")
    @PreAuthorize("hasRole('ADMIN')")
    public AvisPaiement appliquerRemise(@PathVariable UUID id, @RequestBody RemiseRequest request) {
        return feesUseCase.appliquerRemise(id, request.reductionId(), request.contexte());
    }

    // ---------- PAIEMENTS ----------

    @GetMapping("/paiements")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Paiement> listPaiements(@RequestParam(required = false) UUID eleveId,
                                        @RequestParam(required = false) UUID avisId) {
        return feesUseCase.listPaiements(eleveId, avisId);
    }

    @PostMapping("/paiements")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Paiement> creerPaiement(@RequestBody Paiement paiement,
                                                  @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feesUseCase.creerPaiement(paiement, idempotencyKey));
    }

    @GetMapping("/paiements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Paiement getPaiement(@PathVariable UUID id) {
        return feesUseCase.getPaiement(id);
    }

    @PostMapping("/paiements/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public Paiement validerPaiement(@PathVariable UUID id) {
        return feesUseCase.validerPaiement(id);
    }

    @PostMapping("/paiements/{id}/rejeter")
    @PreAuthorize("hasRole('ADMIN')")
    public Paiement rejeterPaiement(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return feesUseCase.rejeterPaiement(id, request == null ? null : request.motif());
    }

    @PostMapping("/paiements/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Paiement annulerPaiement(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return feesUseCase.annulerPaiement(id, request == null ? null : request.motif());
    }

    // ---------- COUPONS ----------

    @GetMapping("/coupons")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CouponPaiement> listCoupons(@RequestParam(required = false) UUID eleveId,
                                            @RequestParam(required = false) UUID anneeId) {
        return feesUseCase.listCoupons(eleveId, anneeId);
    }

    @GetMapping("/coupons/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public CouponPaiement getCoupon(@PathVariable UUID id) {
        return feesUseCase.getCoupon(id);
    }

    @PostMapping("/coupons/{id}/tirer")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponPaiement tirerCoupon(@PathVariable UUID id) {
        return feesUseCase.tirerCoupon(id);
    }

    @PostMapping("/coupons/{id}/remettre")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponPaiement remettreCoupon(@PathVariable UUID id, @RequestBody DestinataireRequest request) {
        return feesUseCase.remettreCoupon(id, request.destinataire());
    }

    @PostMapping("/coupons/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public CouponPaiement annulerCoupon(@PathVariable UUID id, @RequestBody(required = false) MotifRequest request) {
        return feesUseCase.annulerCoupon(id, request == null ? null : request.motif());
    }

    @PostMapping("/coupons/{id}/verifier")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public boolean verifierCoupon(@PathVariable UUID id) {
        return feesUseCase.verifierCoupon(id);
    }

    // ---------- JOURNALIER ----------

    @GetMapping("/frais/journaliers")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Journalier> listJournaliers(@RequestParam(required = false) LocalDate date,
                                            @RequestParam(required = false) UUID site) {
        return feesUseCase.listJournaliers(date, site);
    }

    @PostMapping("/frais/journaliers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Journalier> ouvrirJournalier(@RequestBody Journalier journalier) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feesUseCase.ouvrirJournalier(journalier));
    }

    @GetMapping("/frais/journaliers/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Journalier getJournalier(@PathVariable UUID id) {
        return feesUseCase.getJournalier(id);
    }

    @PostMapping("/frais/journaliers/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Journalier cloturerJournalier(@PathVariable UUID id) {
        return feesUseCase.cloturerJournalier(id);
    }
}
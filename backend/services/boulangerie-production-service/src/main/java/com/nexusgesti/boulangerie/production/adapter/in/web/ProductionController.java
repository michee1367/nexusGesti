package com.nexusgesti.boulangerie.production.adapter.in.web;

import com.nexusgesti.boulangerie.production.domain.model.Fournee;
import com.nexusgesti.boulangerie.production.domain.model.LigneConsommation;
import com.nexusgesti.boulangerie.production.domain.model.PerteProduction;
import com.nexusgesti.boulangerie.production.domain.model.PlanProduction;
import com.nexusgesti.boulangerie.production.domain.model.TypePerte;
import com.nexusgesti.boulangerie.production.domain.port.in.ProductionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProductionController {

    private final ProductionUseCase productionUseCase;

    public ProductionController(ProductionUseCase productionUseCase) {
        this.productionUseCase = productionUseCase;
    }

    // ---------- PLANS ----------

    @GetMapping("/plans")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<PlanProduction> listPlans() {
        return productionUseCase.listPlans();
    }

    @PostMapping("/plans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanProduction> creerPlan(@RequestBody PlanProduction plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionUseCase.creerPlan(plan));
    }

    @GetMapping("/plans/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public PlanProduction getPlan(@PathVariable UUID id) {
        return productionUseCase.getPlan(id);
    }

    @PostMapping("/plans/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanProduction validerPlan(@PathVariable UUID id) {
        return productionUseCase.validerPlan(id);
    }

    @PostMapping("/plans/{id}/lancer")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanProduction lancerPlan(@PathVariable UUID id) {
        return productionUseCase.lancerPlan(id);
    }

    @PostMapping("/plans/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public PlanProduction cloturerPlan(@PathVariable UUID id) {
        return productionUseCase.cloturerPlan(id);
    }

    // ---------- FOURNEES ----------

    @GetMapping("/fournees")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Fournee> listFournees() {
        return productionUseCase.listFournees();
    }

    @PostMapping("/fournees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Fournee> creerFournee(@RequestBody Fournee fournee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productionUseCase.creerFournee(fournee));
    }

    @GetMapping("/fournees/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Fournee getFournee(@PathVariable UUID id) {
        return productionUseCase.getFournee(id);
    }

    @PostMapping("/fournees/{id}/lancer")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee lancerFournee(@PathVariable UUID id) {
        return productionUseCase.lancerFournee(id);
    }

    @PostMapping("/fournees/{id}/enregistrer-rendu")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee enregistrerRendu(@PathVariable UUID id, @RequestBody RenduRequest request) {
        return productionUseCase.enregistrerRendu(id, request.quantite());
    }

    @PostMapping("/fournees/{id}/cuire")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee cuireFournee(@PathVariable UUID id) {
        return productionUseCase.cuireFournee(id);
    }

    @PostMapping("/fournees/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee validerFournee(@PathVariable UUID id) {
        return productionUseCase.validerFournee(id);
    }

    @PostMapping("/fournees/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee annulerFournee(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return productionUseCase.annulerFournee(id, request.motif());
    }

    @PostMapping("/fournees/{id}/consommations")
    @PreAuthorize("hasRole('ADMIN')")
    public Fournee declarerConsommation(@PathVariable UUID id, @RequestBody List<LigneConsommation> lignes) {
        return productionUseCase.declarerConsommation(id, lignes);
    }

    @GetMapping("/fournees/{id}/pertes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<PerteProduction> listPertes(@PathVariable UUID id) {
        return productionUseCase.listPertes(id);
    }

    @PostMapping("/fournees/{id}/pertes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Fournee> declarerPerte(@PathVariable UUID id, @RequestBody PerteRequest request) {
        PerteProduction perte = new PerteProduction();
        perte.setArticleCode(request.articleCode());
        perte.setQuantite(request.quantite());
        perte.setTypePerte(request.typePerte());
        perte.setMotif(request.motif());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productionUseCase.declarerPerte(id, perte, request.prixMoyen()));
    }

    public record RenduRequest(BigDecimal quantite) {
    }

    public record MotifRequest(String motif) {
    }

    public record PerteRequest(String articleCode,
                               BigDecimal quantite,
                               TypePerte typePerte,
                               String motif,
                               BigDecimal prixMoyen) {
    }
}

package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.model.AnneeScolaire;
import com.nexusgesti.tenant.domain.model.Periode;
import com.nexusgesti.tenant.domain.port.in.AnneeUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/annees")
public class AnneeController {

    private final AnneeUseCase anneeUseCase;

    public AnneeController(AnneeUseCase anneeUseCase) {
        this.anneeUseCase = anneeUseCase;
    }

    private UUID etablissementId() {
        return TenantContext.require().asUuid();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnneeScolaire> creer(@RequestBody AnneeScolaire annee) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anneeUseCase.creer(etablissementId(), annee));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<AnneeScolaire> list() {
        return anneeUseCase.list(etablissementId());
    }

    @GetMapping("/courante")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public AnneeScolaire courante() {
        return anneeUseCase.courante(etablissementId());
    }

    @GetMapping("/{anneeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public AnneeScolaire get(@PathVariable UUID anneeId) {
        return anneeUseCase.get(etablissementId(), anneeId);
    }

    @PutMapping("/{anneeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public AnneeScolaire update(@PathVariable UUID anneeId, @RequestBody AnneeScolaire annee) {
        return anneeUseCase.update(etablissementId(), anneeId, annee);
    }

    @PostMapping("/{anneeId}/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public AnneeScolaire ouvrir(@PathVariable UUID anneeId) {
        return anneeUseCase.ouvrir(etablissementId(), anneeId);
    }

    @PostMapping("/{anneeId}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public AnneeScolaire cloturer(@PathVariable UUID anneeId) {
        return anneeUseCase.cloturer(etablissementId(), anneeId, new Date());
    }

    @PostMapping("/{anneeId}/periodes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Periode> addPeriode(@PathVariable UUID anneeId, @RequestBody Periode periode) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(anneeUseCase.addPeriode(etablissementId(), anneeId, periode));
    }

    @GetMapping("/{anneeId}/periodes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Periode> listPeriodes(@PathVariable UUID anneeId) {
        return anneeUseCase.listPeriodes(etablissementId(), anneeId);
    }

    @PostMapping("/{anneeId}/periodes/{periodeId}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public Periode cloturerPeriode(@PathVariable UUID anneeId, @PathVariable UUID periodeId) {
        return anneeUseCase.cloturerPeriode(etablissementId(), anneeId, periodeId);
    }
}
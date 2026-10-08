package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.model.Referentiel;
import com.nexusgesti.tenant.domain.model.ReferentielItem;
import com.nexusgesti.tenant.domain.port.in.ReferentielUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/referentiels")
public class ReferentielController {

    private final ReferentielUseCase referentielUseCase;

    public ReferentielController(ReferentielUseCase referentielUseCase) {
        this.referentielUseCase = referentielUseCase;
    }

    private UUID etablissementId() {
        return TenantContext.require().asUuid();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Referentiel> list() {
        return referentielUseCase.list(etablissementId());
    }

    @GetMapping("/{referentielId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Referentiel get(@PathVariable UUID referentielId) {
        return referentielUseCase.get(etablissementId(), referentielId);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Referentiel getByCode(@PathVariable String code) {
        return referentielUseCase.getByCode(etablissementId(), code);
    }

    @PostMapping("/{referentielId}/items")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Referentiel> addItem(@PathVariable UUID referentielId,
                                               @RequestBody ReferentielItem item) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referentielUseCase.addItem(etablissementId(), referentielId, item));
    }

    @PostMapping("/{referentielId}/items/{itemId}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    public Referentiel desactiverItem(@PathVariable UUID referentielId, @PathVariable UUID itemId) {
        return referentielUseCase.desactiverItem(etablissementId(), referentielId, itemId);
    }
}
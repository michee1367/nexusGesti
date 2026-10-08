package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.model.Etablissement;
import com.nexusgesti.tenant.domain.model.StatutEtablissement;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    private final EtablissementUseCase etablissementUseCase;

    public TenantController(EtablissementUseCase etablissementUseCase) {
        this.etablissementUseCase = etablissementUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Etablissement> creer(@RequestBody Etablissement etablissement) {
        UUID id = TenantContext.require().asUuid();
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.creer(id, etablissement));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Etablissement get(@PathVariable UUID id) {
        return etablissementUseCase.get(id);
    }

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasRole('ADMIN')")
    public Etablissement changerStatut(@PathVariable UUID id,
                                       @RequestBody Map<String, String> corps) {
        StatutEtablissement cible = StatutEtablissement.valueOf(corps.get("statut"));
        return etablissementUseCase.changerStatut(id, cible, corps.get("motif"));
    }
}
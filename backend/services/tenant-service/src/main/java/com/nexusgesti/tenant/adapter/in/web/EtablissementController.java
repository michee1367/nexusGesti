package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.port.in.EtablissementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/etablissements")
public class EtablissementController {

    private final EtablissementUseCase etablissementUseCase;

    public EtablissementController(EtablissementUseCase etablissementUseCase) {
        this.etablissementUseCase = etablissementUseCase;
    }

    private UUID etablissementId() {
        return TenantContext.require().asUuid();
    }

    // ---------- SITES ----------

    @PostMapping("/sites")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Site> addSite(@RequestBody Site site) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addSite(etablissementId(), site));
    }

    @GetMapping("/sites")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Site> listSites() {
        return etablissementUseCase.listSites(etablissementId());
    }

    @GetMapping("/sites/{siteId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Site getSite(@PathVariable UUID siteId) {
        return etablissementUseCase.getSite(etablissementId(), siteId);
    }

    @PutMapping("/sites/{siteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Site updateSite(@PathVariable UUID siteId, @RequestBody Site site) {
        return etablissementUseCase.updateSite(etablissementId(), siteId, site);
    }

    @DeleteMapping("/sites/{siteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSite(@PathVariable UUID siteId) {
        etablissementUseCase.deleteSite(etablissementId(), siteId);
        return ResponseEntity.noContent().build();
    }

    // ---------- FILIERES ----------

    @PostMapping("/filieres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Filiere> addFiliere(@RequestBody Filiere filiere) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addFiliere(etablissementId(), filiere));
    }

    @GetMapping("/filieres")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Filiere> listFilieres() {
        return etablissementUseCase.listFilieres(etablissementId());
    }

    @GetMapping("/filieres/{filiereId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Filiere getFiliere(@PathVariable UUID filiereId) {
        return etablissementUseCase.getFiliere(etablissementId(), filiereId);
    }

    @PutMapping("/filieres/{filiereId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Filiere updateFiliere(@PathVariable UUID filiereId, @RequestBody Filiere filiere) {
        return etablissementUseCase.updateFiliere(etablissementId(), filiereId, filiere);
    }

    @DeleteMapping("/filieres/{filiereId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFiliere(@PathVariable UUID filiereId) {
        etablissementUseCase.deleteFiliere(etablissementId(), filiereId);
        return ResponseEntity.noContent().build();
    }

    // ---------- PROGRAMMES ----------

    @PostMapping("/filieres/{filiereId}/programmes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Programme> addProgramme(@PathVariable UUID filiereId, @RequestBody Programme programme) {
        programme.setFiliereId(filiereId);
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addProgramme(etablissementId(), programme));
    }

    @GetMapping("/programmes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Programme> listProgrammes() {
        return etablissementUseCase.listProgrammes(etablissementId());
    }

    @GetMapping("/programmes/{programmeId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Programme getProgramme(@PathVariable UUID programmeId) {
        return etablissementUseCase.getProgramme(etablissementId(), programmeId);
    }

    @PutMapping("/programmes/{programmeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Programme updateProgramme(@PathVariable UUID programmeId, @RequestBody Programme programme) {
        return etablissementUseCase.updateProgramme(etablissementId(), programmeId, programme);
    }

    @DeleteMapping("/programmes/{programmeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProgramme(@PathVariable UUID programmeId) {
        etablissementUseCase.deleteProgramme(etablissementId(), programmeId);
        return ResponseEntity.noContent().build();
    }

    // ---------- CYCLES ----------

    @PostMapping("/cycles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cycle> addCycle(@RequestBody Cycle cycle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addCycle(etablissementId(), cycle));
    }

    @GetMapping("/cycles")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Cycle> listCycles() {
        return etablissementUseCase.listCycles(etablissementId());
    }

    @GetMapping("/cycles/{cycleId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Cycle getCycle(@PathVariable UUID cycleId) {
        return etablissementUseCase.getCycle(etablissementId(), cycleId);
    }

    @PutMapping("/cycles/{cycleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Cycle updateCycle(@PathVariable UUID cycleId, @RequestBody Cycle cycle) {
        return etablissementUseCase.updateCycle(etablissementId(), cycleId, cycle);
    }

    @DeleteMapping("/cycles/{cycleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCycle(@PathVariable UUID cycleId) {
        etablissementUseCase.deleteCycle(etablissementId(), cycleId);
        return ResponseEntity.noContent().build();
    }

    // ---------- PARAMETRES ----------

    @PostMapping("/parametres")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parametre> addParametre(@RequestBody Parametre parametre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addParametre(etablissementId(), parametre));
    }

    @GetMapping("/parametres")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Parametre> listParametres() {
        return etablissementUseCase.listParametres(etablissementId());
    }

    @GetMapping("/parametres/{parametreId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Parametre getParametre(@PathVariable UUID parametreId) {
        return etablissementUseCase.getParametre(etablissementId(), parametreId);
    }

    @PutMapping("/parametres/{parametreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Parametre updateParametre(@PathVariable UUID parametreId, @RequestBody Parametre parametre) {
        return etablissementUseCase.updateParametre(etablissementId(), parametreId, parametre);
    }

    @DeleteMapping("/parametres/{parametreId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParametre(@PathVariable UUID parametreId) {
        etablissementUseCase.deleteParametre(etablissementId(), parametreId);
        return ResponseEntity.noContent().build();
    }

    // ---------- GRILLES PRESET ----------

    @PostMapping("/grille-presets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrillePreset> addPreset(@RequestBody GrillePreset preset) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etablissementUseCase.addPreset(etablissementId(), preset));
    }

    @GetMapping("/grille-presets")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<GrillePreset> listPresets() {
        return etablissementUseCase.listPresets(etablissementId());
    }

    @GetMapping("/grille-presets/{presetId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public GrillePreset getPreset(@PathVariable UUID presetId) {
        return etablissementUseCase.getPreset(etablissementId(), presetId);
    }

    @PostMapping("/grille-presets/{presetId}/dupliquer")
    @PreAuthorize("hasRole('ADMIN')")
    public GrillePreset dupliquerPreset(@PathVariable UUID presetId, @RequestBody Map<String, String> corps) {
        return etablissementUseCase.dupliquerPreset(etablissementId(), presetId, corps.get("libelle"));
    }

    @DeleteMapping("/grille-presets/{presetId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePreset(@PathVariable UUID presetId) {
        etablissementUseCase.deletePreset(etablissementId(), presetId);
        return ResponseEntity.noContent().build();
    }
}
package com.nexusgesti.tenant.adapter.in.web;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.tenant.domain.model.*;
import com.nexusgesti.tenant.domain.port.in.GrilleUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grilles")
public class GrilleController {

    private final GrilleUseCase grilleUseCase;

    public GrilleController(GrilleUseCase grilleUseCase) {
        this.grilleUseCase = grilleUseCase;
    }

    private UUID etablissementId() {
        return TenantContext.require().asUuid();
    }

    // ---------- CREATION PAR TYPE ----------

    @PostMapping("/notation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleNotation> creerNotation(@RequestBody GrilleNotation grille) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((GrilleNotation) grilleUseCase.creer(etablissementId(), "notation", grille));
    }

    @PostMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleStock> creerStock(@RequestBody GrilleStock grille) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((GrilleStock) grilleUseCase.creer(etablissementId(), "stock", grille));
    }

    @PostMapping("/conformite")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrilleConformite> creerConformite(@RequestBody GrilleConformite grille) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body((GrilleConformite) grilleUseCase.creer(etablissementId(), "conformite", grille));
    }

    // ---------- LECTURE PAR TYPE ----------

    @GetMapping("/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<? extends Grille> list(@PathVariable String type) {
        return grilleUseCase.list(etablissementId(), type);
    }

    @GetMapping("/{type}/applicable")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Grille applicable(@PathVariable String type,
                             @RequestParam ProfilEtablissement profil,
                             @RequestParam(required = false) UUID anneeId) {
        return grilleUseCase.applicable(etablissementId(), type, profil, anneeId);
    }

    @GetMapping("/{type}/{grilleId}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Grille get(@PathVariable String type, @PathVariable UUID grilleId) {
        return grilleUseCase.get(etablissementId(), type, grilleId);
    }

    // ---------- MAJ / CYCLE DE VIE ----------

    @PutMapping("/notation/{grilleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation updateNotation(@PathVariable UUID grilleId, @RequestBody GrilleNotation grille) {
        return (GrilleNotation) grilleUseCase.update(etablissementId(), "notation", grilleId, grille);
    }

    @PutMapping("/stock/{grilleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleStock updateStock(@PathVariable UUID grilleId, @RequestBody GrilleStock grille) {
        return (GrilleStock) grilleUseCase.update(etablissementId(), "stock", grilleId, grille);
    }

    @PutMapping("/conformite/{grilleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleConformite updateConformite(@PathVariable UUID grilleId, @RequestBody GrilleConformite grille) {
        return (GrilleConformite) grilleUseCase.update(etablissementId(), "conformite", grilleId, grille);
    }

    @PostMapping("/{type}/{grilleId}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public Grille activer(@PathVariable String type, @PathVariable UUID grilleId) {
        return grilleUseCase.activer(etablissementId(), type, grilleId);
    }

    @PostMapping("/{type}/{grilleId}/figer")
    @PreAuthorize("hasRole('ADMIN')")
    public Grille figer(@PathVariable String type, @PathVariable UUID grilleId) {
        return grilleUseCase.figer(etablissementId(), type, grilleId);
    }

    @PostMapping("/{type}/{grilleId}/dupliquer")
    @PreAuthorize("hasRole('ADMIN')")
    public Grille dupliquer(@PathVariable String type, @PathVariable UUID grilleId,
                            @RequestBody Map<String, String> corps) {
        return grilleUseCase.dupliquer(etablissementId(), type, grilleId, corps.get("libelle"));
    }

    // ---------- ENFANTS NOTATION ----------

    @PostMapping("/notation/{grilleId}/baremes")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addBareme(@PathVariable UUID grilleId, @RequestBody Bareme bareme) {
        return grilleUseCase.addBareme(etablissementId(), grilleId, bareme);
    }

    @GetMapping("/notation/{grilleId}/baremes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Bareme> listBaremes(@PathVariable UUID grilleId) {
        return grilleUseCase.listBaremes(etablissementId(), grilleId);
    }

    @DeleteMapping("/notation/{grilleId}/baremes/{baremeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteBareme(@PathVariable UUID grilleId, @PathVariable UUID baremeId) {
        return grilleUseCase.deleteBareme(etablissementId(), grilleId, baremeId);
    }

    @PostMapping("/notation/{grilleId}/regles-ponderation")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addReglePonderation(@PathVariable UUID grilleId, @RequestBody ReglePonderation regle) {
        return grilleUseCase.addReglePonderation(etablissementId(), grilleId, regle);
    }

    @GetMapping("/notation/{grilleId}/regles-ponderation")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<ReglePonderation> listReglesPonderation(@PathVariable UUID grilleId) {
        return grilleUseCase.listReglesPonderation(etablissementId(), grilleId);
    }

    @DeleteMapping("/notation/{grilleId}/regles-ponderation/{regleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteReglePonderation(@PathVariable UUID grilleId, @PathVariable UUID regleId) {
        return grilleUseCase.deleteReglePonderation(etablissementId(), grilleId, regleId);
    }

    @PutMapping("/notation/{grilleId}/config-cascade")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation setConfigCascade(@PathVariable UUID grilleId, @RequestBody ConfigCascade config) {
        return grilleUseCase.setConfigCascade(etablissementId(), grilleId, config);
    }

    @GetMapping("/notation/{grilleId}/config-cascade")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ConfigCascade getConfigCascade(@PathVariable UUID grilleId) {
        return grilleUseCase.getConfigCascade(etablissementId(), grilleId);
    }

    @PostMapping("/notation/{grilleId}/seuils-decision")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addSeuilDecision(@PathVariable UUID grilleId, @RequestBody SeuilDecision seuil) {
        return grilleUseCase.addSeuilDecision(etablissementId(), grilleId, seuil);
    }

    @DeleteMapping("/notation/{grilleId}/seuils-decision/{seuilId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteSeuilDecision(@PathVariable UUID grilleId, @PathVariable UUID seuilId) {
        return grilleUseCase.deleteSeuilDecision(etablissementId(), grilleId, seuilId);
    }

    @PostMapping("/notation/{grilleId}/regles-assiduite")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addRegleAssiduite(@PathVariable UUID grilleId, @RequestBody RegleAssiduite regle) {
        return grilleUseCase.addRegleAssiduite(etablissementId(), grilleId, regle);
    }

    @DeleteMapping("/notation/{grilleId}/regles-assiduite/{regleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteRegleAssiduite(@PathVariable UUID grilleId, @PathVariable UUID regleId) {
        return grilleUseCase.deleteRegleAssiduite(etablissementId(), grilleId, regleId);
    }

    @PostMapping("/notation/{grilleId}/mentions")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addMention(@PathVariable UUID grilleId, @RequestBody Mention mention) {
        return grilleUseCase.addMention(etablissementId(), grilleId, mention);
    }

    @DeleteMapping("/notation/{grilleId}/mentions/{mentionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteMention(@PathVariable UUID grilleId, @PathVariable UUID mentionId) {
        return grilleUseCase.deleteMention(etablissementId(), grilleId, mentionId);
    }

    @PostMapping("/notation/{grilleId}/appreciations")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation addAppreciation(@PathVariable UUID grilleId, @RequestBody RegleAppreciation appreciation) {
        return grilleUseCase.addAppreciation(etablissementId(), grilleId, appreciation);
    }

    @DeleteMapping("/notation/{grilleId}/appreciations/{appreciationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public GrilleNotation deleteAppreciation(@PathVariable UUID grilleId, @PathVariable UUID appreciationId) {
        return grilleUseCase.deleteAppreciation(etablissementId(), grilleId, appreciationId);
    }

    // ---------- ENFANTS STOCK ----------

    @PostMapping("/stock/{grilleId}/alertes")
    @PreAuthorize("hasRole('ADMIN')")
    public AlerteStock addAlerte(@PathVariable UUID grilleId, @RequestBody AlerteStock alerte) {
        return grilleUseCase.addAlerte(etablissementId(), grilleId, alerte);
    }

    @GetMapping("/stock/{grilleId}/alertes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<AlerteStock> listAlertes(@PathVariable UUID grilleId) {
        return grilleUseCase.listAlertes(etablissementId(), grilleId);
    }

    @PostMapping("/stock/{grilleId}/alertes/{alerteId}/acquitter")
    @PreAuthorize("hasRole('ADMIN')")
    public AlerteStock acquitterAlerte(@PathVariable UUID grilleId, @PathVariable UUID alerteId,
                                       @RequestBody Map<String, UUID> corps) {
        return grilleUseCase.acquitterAlerte(etablissementId(), grilleId, alerteId, corps.get("acquitteur"));
    }
}
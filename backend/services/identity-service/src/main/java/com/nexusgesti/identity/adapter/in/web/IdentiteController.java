package com.nexusgesti.identity.adapter.in.web;

import com.nexusgesti.identity.domain.model.Permission;
import com.nexusgesti.identity.domain.model.Role;
import com.nexusgesti.identity.domain.port.in.RoleUseCase;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identites")
public class IdentiteController {

    private final RoleUseCase roleUseCase;
    private final UtilisateurUseCase utilisateurUseCase;

    public IdentiteController(RoleUseCase roleUseCase, UtilisateurUseCase utilisateurUseCase) {
        this.roleUseCase = roleUseCase;
        this.utilisateurUseCase = utilisateurUseCase;
    }

    @GetMapping("/annuaire")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<com.nexusgesti.identity.domain.model.Utilisateur> annuaire(
            @RequestParam(name = "q", required = false) String query) {
        return utilisateurUseCase.rechercher(query);
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Role> listerRoles() {
        return roleUseCase.listerRoles();
    }

    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> creerRole(@RequestBody RoleRequest request) {
        Role role = new Role();
        role.setCode(request.code());
        role.setLibelle(request.libelle());
        role.setCategorie(request.categorie());
        role.setSysteme(request.systeme() != null && request.systeme());
        role.setHierarchique(request.hierarchique());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleUseCase.creerRole(role));
    }

    @PutMapping("/roles/{code}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public Role definirPermissions(@PathVariable String code, @RequestBody PermissionsRequest request) {
        return roleUseCase.definirPermissions(code, request.permissionIds());
    }

    @GetMapping("/permissions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Permission> listerPermissions() {
        return roleUseCase.listerPermissions();
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Permission> creerPermission(@RequestBody PermissionRequest request) {
        Permission permission = new Permission();
        permission.setCode(request.code());
        permission.setLibelle(request.libelle());
        permission.setService(request.service());
        permission.setRessource(request.ressource());
        permission.setAction(request.action());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleUseCase.creerPermission(permission));
    }

    @GetMapping("/permissions/verifier")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Map<String, Boolean> verifier(@RequestParam String role,
                                         @RequestParam String verbe,
                                         @RequestParam String ressource) {
        return Map.of("autorise", roleUseCase.verifierPermission(role, verbe, ressource));
    }

    public record RoleRequest(
            @NotBlank String code,
            String libelle,
            com.nexusgesti.identity.domain.model.CategorieRole categorie,
            Boolean systeme,
            Integer hierarchique
    ) {}

    public record PermissionsRequest(List<UUID> permissionIds) {}

    public record PermissionRequest(
            @NotBlank String code,
            String libelle,
            String service,
            String ressource,
            String action
    ) {}
}

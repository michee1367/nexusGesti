package com.nexusgesti.identity.application.service;

import com.nexusgesti.identity.domain.IdentityException;
import com.nexusgesti.identity.domain.model.Permission;
import com.nexusgesti.identity.domain.model.Role;
import com.nexusgesti.identity.domain.model.RolePermission;
import com.nexusgesti.identity.domain.port.in.RoleUseCase;
import com.nexusgesti.identity.domain.port.out.IdentityProviderPort;
import com.nexusgesti.identity.domain.port.out.PermissionRepository;
import com.nexusgesti.identity.domain.port.out.RolePermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RoleService implements RoleUseCase {

    private final IdentityProviderPort identityProvider;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public RoleService(IdentityProviderPort identityProvider,
                       PermissionRepository permissionRepository,
                       RolePermissionRepository rolePermissionRepository) {
        this.identityProvider = identityProvider;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public Role creerRole(Role role) {
        if (role.getCode() == null || role.getCode().isBlank()) {
            throw IdentityException.conflit("CODE_REQUIS", "Le code du role est obligatoire");
        }
        if (identityProvider.findRealmRole(role.getCode()).isPresent()) {
            throw IdentityException.conflit("ROLE_EXISTANT", "Role deja existant : " + role.getCode());
        }
        Map<String, String> attributes = new java.util.LinkedHashMap<>();
        if (role.getCategorie() != null) attributes.put("categorie", role.getCategorie().name());
        attributes.put("systeme", String.valueOf(role.isSysteme()));
        if (role.getHierarchique() != null) attributes.put("hierarchique", String.valueOf(role.getHierarchique()));
        identityProvider.createRealmRole(new IdentityProviderPort.RealmRole(
                role.getCode(), role.getLibelle(), attributes));
        return identityProvider.findRealmRole(role.getCode()).map(this::toRole).orElse(role);
    }

    @Override
    public List<Role> listerRoles() {
        return identityProvider.listRealmRoles().stream()
                .map(this::toRole)
                .map(this::withPermissions)
                .toList();
    }

    @Override
    @Transactional
    public Role definirPermissions(String roleCode, List<UUID> permissionIds) {
        if (identityProvider.findRealmRole(roleCode).isEmpty()) {
            throw IdentityException.notFound("Role", null);
        }
        rolePermissionRepository.deleteByRoleCode(roleCode);
        if (permissionIds != null) {
            for (UUID permissionId : permissionIds) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> IdentityException.notFound("Permission", permissionId));
                RolePermission rp = new RolePermission();
                rp.setId(UUID.randomUUID());
                rp.setRoleCode(roleCode);
                rp.setPermission(permission);
                rp.setGrantedAt(Instant.now());
                rolePermissionRepository.save(rp);
            }
        }
        Role role = identityProvider.findRealmRole(roleCode).map(this::toRole)
                .orElseThrow(() -> IdentityException.notFound("Role", null));
        return withPermissions(role);
    }

    @Override
    public Permission creerPermission(Permission permission) {
        if (permission.getCode() == null || permission.getCode().isBlank()) {
            throw IdentityException.conflit("CODE_REQUIS", "Le code de la permission est obligatoire");
        }
        permissionRepository.findByCode(permission.getCode()).ifPresent(p -> {
            throw IdentityException.conflit("PERMISSION_EXISTANTE",
                    "Permission deja existante : " + permission.getCode());
        });
        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> listerPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public boolean verifierPermission(String roleCode, String verbe, String ressource) {
        LocalDate today = LocalDate.now();
        return rolePermissionRepository.findByRoleCode(roleCode).stream()
                .filter(rp -> !rp.estExpiree(today))
                .map(RolePermission::getPermission)
                .filter(java.util.Objects::nonNull)
                .anyMatch(p -> p.correspondA(verbe, ressource));
    }

    private Role withPermissions(Role role) {
        List<Permission> permissions = rolePermissionRepository.findByRoleCode(role.getCode()).stream()
                .map(RolePermission::getPermission)
                .filter(java.util.Objects::nonNull)
                .toList();
        role.setPermissions(new ArrayList<>(permissions));
        return role;
    }

    private Role toRole(IdentityProviderPort.RealmRole realmRole) {
        Role role = new Role();
        role.setCode(realmRole.name());
        role.setLibelle(realmRole.description() != null ? realmRole.description() : realmRole.name());
        Map<String, String> attrs = realmRole.attributes();
        if (attrs != null) {
            String categorie = attrs.get("categorie");
            if (categorie != null) {
                try {
                    role.setCategorie(com.nexusgesti.identity.domain.model.CategorieRole.valueOf(categorie));
                } catch (IllegalArgumentException ignored) {
                    // categorie non reconnue : laissee nulle
                }
            }
            role.setSysteme(Boolean.parseBoolean(attrs.get("systeme")));
            String hierarchique = attrs.get("hierarchique");
            if (hierarchique != null) {
                try {
                    role.setHierarchique(Integer.valueOf(hierarchique));
                } catch (NumberFormatException ignored) {
                    // hierarchique invalide : laissee nulle
                }
            }
        }
        return role;
    }
}

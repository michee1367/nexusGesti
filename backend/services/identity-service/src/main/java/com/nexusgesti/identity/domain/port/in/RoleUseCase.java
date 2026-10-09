package com.nexusgesti.identity.domain.port.in;

import com.nexusgesti.identity.domain.model.Permission;
import com.nexusgesti.identity.domain.model.Role;

import java.util.List;
import java.util.UUID;

public interface RoleUseCase {

    Role creerRole(Role role);

    List<Role> listerRoles();

    Role definirPermissions(String roleCode, List<UUID> permissionIds);

    Permission creerPermission(Permission permission);

    List<Permission> listerPermissions();

    boolean verifierPermission(String roleCode, String verbe, String ressource);
}

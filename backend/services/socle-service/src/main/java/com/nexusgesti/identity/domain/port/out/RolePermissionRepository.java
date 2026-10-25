package com.nexusgesti.identity.domain.port.out;

import com.nexusgesti.identity.domain.model.RolePermission;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    List<RolePermission> findByRoleCode(String roleCode);

    void deleteByRoleCode(String roleCode);

    boolean existsByRoleCodeAndPermissionId(String roleCode, UUID permissionId);
}

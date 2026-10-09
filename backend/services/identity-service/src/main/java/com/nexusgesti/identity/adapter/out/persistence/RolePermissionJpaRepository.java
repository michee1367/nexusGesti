package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.identity.adapter.out.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionEntity, UUID> {

    List<RolePermissionEntity> findByRoleCode(String roleCode);

    void deleteByRoleCode(String roleCode);

    boolean existsByRoleCodeAndPermissionId(String roleCode, UUID permissionId);
}

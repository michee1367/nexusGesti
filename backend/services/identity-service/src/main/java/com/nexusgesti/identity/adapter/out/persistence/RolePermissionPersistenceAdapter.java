package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.adapter.out.persistence.entity.RolePermissionEntity;
import com.nexusgesti.identity.domain.model.Permission;
import com.nexusgesti.identity.domain.model.RolePermission;
import com.nexusgesti.identity.domain.port.out.RolePermissionRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class RolePermissionPersistenceAdapter implements RolePermissionRepository {

    private final RolePermissionJpaRepository jpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    public RolePermissionPersistenceAdapter(RolePermissionJpaRepository jpaRepository,
                                            PermissionJpaRepository permissionJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.permissionJpaRepository = permissionJpaRepository;
    }

    @Override
    public RolePermission save(RolePermission rolePermission) {
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setId(rolePermission.getId() == null ? UUID.randomUUID() : rolePermission.getId());
        entity.setRoleCode(rolePermission.getRoleCode());
        entity.setPermissionId(rolePermission.getPermission().getIdPermission());
        entity.setGrantedAt(rolePermission.getGrantedAt() == null ? Instant.now() : rolePermission.getGrantedAt());
        entity.setGrantedBy(rolePermission.getGrantedBy());
        entity.setExpireLe(rolePermission.getExpireLe());
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<RolePermission> findByRoleCode(String roleCode) {
        return jpaRepository.findByRoleCode(roleCode).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteByRoleCode(String roleCode) {
        jpaRepository.deleteByRoleCode(roleCode);
    }

    @Override
    public boolean existsByRoleCodeAndPermissionId(String roleCode, UUID permissionId) {
        return jpaRepository.existsByRoleCodeAndPermissionId(roleCode, permissionId);
    }

    private RolePermission toDomain(RolePermissionEntity e) {
        RolePermission rp = new RolePermission();
        rp.setId(e.getId());
        rp.setRoleCode(e.getRoleCode());
        rp.setGrantedAt(e.getGrantedAt());
        rp.setGrantedBy(e.getGrantedBy());
        rp.setExpireLe(e.getExpireLe());
        permissionJpaRepository.findById(e.getPermissionId()).ifPresent(p -> {
            Permission permission = new Permission();
            permission.setIdPermission(p.getId());
            permission.setCode(p.getCode());
            permission.setLibelle(p.getLibelle());
            permission.setService(p.getService());
            permission.setRessource(p.getRessource());
            permission.setAction(p.getAction());
            rp.setPermission(permission);
        });
        return rp;
    }
}

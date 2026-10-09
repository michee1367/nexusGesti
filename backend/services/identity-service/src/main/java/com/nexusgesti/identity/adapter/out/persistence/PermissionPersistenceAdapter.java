package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.adapter.out.persistence.entity.PermissionEntity;
import com.nexusgesti.identity.domain.model.Permission;
import com.nexusgesti.identity.domain.port.out.PermissionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PermissionPersistenceAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpaRepository;

    public PermissionPersistenceAdapter(PermissionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = toEntity(permission);
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return jpaRepository.findAllByOrderByCodeAsc().stream().map(this::toDomain).toList();
    }

    private PermissionEntity toEntity(Permission p) {
        PermissionEntity e = new PermissionEntity();
        e.setId(p.getIdPermission() == null ? UUID.randomUUID() : p.getIdPermission());
        e.setCode(p.getCode());
        e.setLibelle(p.getLibelle());
        e.setService(p.getService());
        e.setRessource(p.getRessource());
        e.setAction(p.getAction());
        return e;
    }

    private Permission toDomain(PermissionEntity e) {
        Permission p = new Permission();
        p.setIdPermission(e.getId());
        p.setCode(e.getCode());
        p.setLibelle(e.getLibelle());
        p.setService(e.getService());
        p.setRessource(e.getRessource());
        p.setAction(e.getAction());
        return p;
    }
}

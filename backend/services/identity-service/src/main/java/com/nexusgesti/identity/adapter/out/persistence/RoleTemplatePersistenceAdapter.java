package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.adapter.out.persistence.entity.RoleTemplateEntity;
import com.nexusgesti.identity.domain.model.RoleTemplate;
import com.nexusgesti.identity.domain.port.out.RoleTemplateRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleTemplatePersistenceAdapter implements RoleTemplateRepository {

    private final RoleTemplateJpaRepository jpaRepository;

    public RoleTemplatePersistenceAdapter(RoleTemplateJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RoleTemplate save(RoleTemplate roleTemplate) {
        RoleTemplateEntity entity = new RoleTemplateEntity();
        entity.setId(roleTemplate.getId() == null ? UUID.randomUUID() : roleTemplate.getId());
        entity.setCode(roleTemplate.getCode());
        entity.setLibelle(roleTemplate.getLibelle());
        entity.setProfil(roleTemplate.getProfil());
        entity.setPermissions(new ArrayList<>(roleTemplate.getPermissions()));
        entity.setRoles(new ArrayList<>(roleTemplate.getRoles()));
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<RoleTemplate> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<RoleTemplate> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<RoleTemplate> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    private RoleTemplate toDomain(RoleTemplateEntity e) {
        RoleTemplate t = new RoleTemplate();
        t.setId(e.getId());
        t.setCode(e.getCode());
        t.setLibelle(e.getLibelle());
        t.setProfil(e.getProfil());
        t.setPermissions(e.getPermissions() == null ? List.of() : e.getPermissions());
        t.setRoles(e.getRoles() == null ? List.of() : e.getRoles());
        return t;
    }
}

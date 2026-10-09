package com.nexusgesti.identity.domain.port.out;

import com.nexusgesti.identity.domain.model.RoleTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleTemplateRepository {

    RoleTemplate save(RoleTemplate roleTemplate);

    Optional<RoleTemplate> findById(UUID id);

    Optional<RoleTemplate> findByCode(String code);

    List<RoleTemplate> findAll();

    void delete(UUID id);
}

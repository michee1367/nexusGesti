package com.nexusgesti.identity.domain.port.out;

import com.nexusgesti.identity.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {

    Permission save(Permission permission);

    Optional<Permission> findById(UUID id);

    Optional<Permission> findByCode(String code);

    List<Permission> findAll();
}

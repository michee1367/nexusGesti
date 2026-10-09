package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.identity.adapter.out.persistence.entity.RoleTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleTemplateJpaRepository extends JpaRepository<RoleTemplateEntity, UUID> {

    Optional<RoleTemplateEntity> findByCode(String code);
}

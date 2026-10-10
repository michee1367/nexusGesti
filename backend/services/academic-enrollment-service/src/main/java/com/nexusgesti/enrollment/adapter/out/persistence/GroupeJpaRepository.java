package com.nexusgesti.enrollment.adapter.out.persistence;

import com.nexusgesti.enrollment.adapter.out.persistence.entity.GroupeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupeJpaRepository extends JpaRepository<GroupeEntity, UUID> {
}

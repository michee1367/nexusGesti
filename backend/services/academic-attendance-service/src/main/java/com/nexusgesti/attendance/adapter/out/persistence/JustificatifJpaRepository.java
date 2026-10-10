package com.nexusgesti.attendance.adapter.out.persistence;

import com.nexusgesti.attendance.adapter.out.persistence.entity.JustificatifEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JustificatifJpaRepository extends JpaRepository<JustificatifEntity, UUID> {
}

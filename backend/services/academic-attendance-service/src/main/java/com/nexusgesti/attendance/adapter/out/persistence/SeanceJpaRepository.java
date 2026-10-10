package com.nexusgesti.attendance.adapter.out.persistence;

import com.nexusgesti.attendance.adapter.out.persistence.entity.SeanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SeanceJpaRepository extends JpaRepository<SeanceEntity, UUID> {

    List<SeanceEntity> findByDate(LocalDate date);
}

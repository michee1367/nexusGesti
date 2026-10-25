package com.nexusgesti.attendance.adapter.out.persistence;

import com.nexusgesti.attendance.adapter.out.persistence.entity.PresenceEleveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PresenceEleveJpaRepository extends JpaRepository<PresenceEleveEntity, UUID> {

    List<PresenceEleveEntity> findByFeuilleId(UUID feuilleId);

    void deleteByFeuilleId(UUID feuilleId);

    @Query("""
            select p.eleveId as eleveId, s.anneeId as anneeId, s.periodeId as periodeId,
                   p.statut as statut, p.typeAbsence as typeAbsence,
                   p.minutesRetard as minutesRetard, s.volumeHoraireReel as volumeHoraire
            from PresenceEleveEntity p, FeuillePresenceEntity f, SeanceEntity s
            where p.feuilleId = f.id and f.seanceId = s.id
              and p.eleveId = :eleveId
              and f.statut in ('VALIDEE', 'CLOTUREE')
            """)
    List<SuiviProjection> findLignesSuivi(@Param("eleveId") UUID eleveId);
}

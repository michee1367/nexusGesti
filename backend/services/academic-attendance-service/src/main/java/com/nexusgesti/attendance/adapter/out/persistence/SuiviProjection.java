package com.nexusgesti.attendance.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

public interface SuiviProjection {

    UUID getEleveId();
    UUID getAnneeId();
    UUID getPeriodeId();
    String getStatut();
    String getTypeAbsence();
    Integer getMinutesRetard();
    BigDecimal getVolumeHoraire();
}

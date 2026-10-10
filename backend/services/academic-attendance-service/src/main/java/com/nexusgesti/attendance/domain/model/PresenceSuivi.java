package com.nexusgesti.attendance.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record PresenceSuivi(
        UUID eleveId,
        UUID anneeId,
        UUID periodeId,
        StatutPresence statut,
        TypeAbsence typeAbsence,
        Integer minutesRetard,
        BigDecimal volumeHoraire) {
}

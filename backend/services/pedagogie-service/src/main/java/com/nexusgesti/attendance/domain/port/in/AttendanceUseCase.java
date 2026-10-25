package com.nexusgesti.attendance.domain.port.in;

import com.nexusgesti.attendance.domain.model.FeuillePresence;
import com.nexusgesti.attendance.domain.model.Justificatif;
import com.nexusgesti.attendance.domain.model.Seance;
import com.nexusgesti.attendance.domain.model.StatutPresence;
import com.nexusgesti.attendance.domain.model.SuiviAssiduite;
import com.nexusgesti.attendance.domain.model.TypeAbsence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AttendanceUseCase {

    List<Seance> listSeances(LocalDate date);
    Seance getSeance(UUID id);
    Seance creerSeance(Seance seance);
    Seance updateSeance(UUID id, Seance seance);
    Seance demarrerSeance(UUID id);
    Seance terminerSeance(UUID id);
    Seance reporterSeance(UUID id);
    Seance annulerSeance(UUID id, String motif);

    List<FeuillePresence> listFeuilles();
    FeuillePresence getFeuille(UUID id);
    FeuillePresence creerFeuille(FeuillePresence feuille);
    FeuillePresence pointer(UUID feuilleId, UUID eleveId, StatutPresence statut, UUID par);
    FeuillePresence marquerAbsence(UUID feuilleId, UUID eleveId, TypeAbsence type, UUID par);
    FeuillePresence marquerRetard(UUID feuilleId, UUID eleveId, int minutes, UUID par);
    FeuillePresence faireAppelGlobal(UUID feuilleId, StatutPresence statut, UUID par);
    FeuillePresence soumettreFeuille(UUID id);
    FeuillePresence validerFeuille(UUID id, UUID par);
    FeuillePresence cloturerFeuille(UUID id);

    List<Justificatif> listJustificatifs();
    Justificatif getJustificatif(UUID id);
    Justificatif deposerJustificatif(Justificatif justificatif);
    Justificatif accepterJustificatif(UUID id, UUID par);
    Justificatif rejeterJustificatif(UUID id, String motif, UUID par);

    SuiviAssiduite getSuivi(UUID eleveId, UUID anneeId, UUID periodeId);
    BigDecimal getTauxClasse(UUID classeId);
    List<FeuillePresence> getRegistre(UUID classeId, LocalDate date);
}

package com.nexusgesti.attendance.domain.port.out;

import com.nexusgesti.attendance.domain.model.FeuillePresence;
import com.nexusgesti.attendance.domain.model.Justificatif;
import com.nexusgesti.attendance.domain.model.PresenceSuivi;
import com.nexusgesti.attendance.domain.model.Seance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository {

    Seance saveSeance(Seance seance);
    Optional<Seance> findSeance(UUID id);
    List<Seance> listSeances();
    List<Seance> listSeancesDate(LocalDate date);

    FeuillePresence saveFeuille(FeuillePresence feuille);
    Optional<FeuillePresence> findFeuille(UUID id);
    List<FeuillePresence> listFeuilles();
    List<FeuillePresence> listFeuillesClasse(UUID classeId);
    List<FeuillePresence> listFeuillesDate(LocalDate date);

    Justificatif saveJustificatif(Justificatif justificatif);
    Optional<Justificatif> findJustificatif(UUID id);
    List<Justificatif> listJustificatifs();

    List<PresenceSuivi> listPresencesValideesEleve(UUID eleveId);
}

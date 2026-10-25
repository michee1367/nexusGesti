package com.nexusgesti.attendance.application.service;

import com.nexusgesti.attendance.domain.AttendanceException;
import com.nexusgesti.attendance.domain.model.*;
import com.nexusgesti.attendance.domain.port.out.AttendanceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AttendanceServiceTest {

    private final AttendanceRepository repository = mock(AttendanceRepository.class);
    private final AttendanceService service = new AttendanceService(repository);

    @Test
    void getSuiviAgregeLesLignes() {
        UUID eleve = UUID.randomUUID();
        UUID annee = UUID.randomUUID();
        UUID periode = UUID.randomUUID();
        when(repository.listPresencesValideesEleve(eleve)).thenReturn(List.of(
                ligne(eleve, annee, periode, StatutPresence.PRESENT, null, null, "2"),
                ligne(eleve, annee, periode, StatutPresence.ABSENT_EXCUSE, TypeAbsence.MEDICALE, null, "2"),
                ligne(eleve, annee, periode, StatutPresence.ABSENT_NON_EXCUSE, TypeAbsence.INJUSTIFIEE, null, "2"),
                ligne(eleve, annee, periode, StatutPresence.RETARD, null, 10, "2")));

        SuiviAssiduite suivi = service.getSuivi(eleve, annee, periode);

        assertEquals(4, suivi.getNbSeancesTenues());
        assertEquals(new BigDecimal("8"), suivi.getNbHeuresPrevues());
        assertEquals(new BigDecimal("4"), suivi.getNbHeuresAbsentes());
        assertEquals(1, suivi.getNbAbsencesJustifiees());
        assertEquals(1, suivi.getNbAbsencesInjustifiees());
        assertEquals(1, suivi.getNbRetards());
        assertEquals(10, suivi.getMinutesRetardTotal());
        assertEquals(new BigDecimal("50.00"), suivi.getTauxPresence());
    }

    @Test
    void getSuiviFiltreParAnnee() {
        UUID eleve = UUID.randomUUID();
        UUID anneeA = UUID.randomUUID();
        UUID anneeB = UUID.randomUUID();
        when(repository.listPresencesValideesEleve(eleve)).thenReturn(List.of(
                ligne(eleve, anneeA, null, StatutPresence.PRESENT, null, null, "2"),
                ligne(eleve, anneeB, null, StatutPresence.ABSENT_NON_EXCUSE, null, null, "2")));

        SuiviAssiduite suivi = service.getSuivi(eleve, anneeA, null);
        assertEquals(1, suivi.getNbSeancesTenues());
    }

    @Test
    void accepterJustificatifRequalifieLesAbsences() {
        UUID eleve = UUID.randomUUID();
        UUID justificatifId = UUID.randomUUID();
        LocalDate date = LocalDate.now();

        Justificatif justificatif = new Justificatif();
        justificatif.setIdJustificatif(justificatifId);
        justificatif.setEleveId(eleve);
        justificatif.setDateReference(date);
        justificatif.deposer();

        FeuillePresence feuille = new FeuillePresence().ouvrir();
        feuille.pointer(eleve, StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID());

        when(repository.findJustificatif(justificatifId)).thenReturn(Optional.of(justificatif));
        when(repository.listFeuillesDate(date)).thenReturn(List.of(feuille));

        service.accepterJustificatif(justificatifId, UUID.randomUUID());

        assertEquals(StatutPresence.ABSENT_EXCUSE,
                feuille.trouver(eleve).orElseThrow().getStatut());
        verify(repository).saveFeuille(feuille);
        verify(repository).saveJustificatif(justificatif);
    }

    @Test
    void getSeanceInconnueLeve404() {
        UUID id = UUID.randomUUID();
        when(repository.findSeance(id)).thenReturn(Optional.empty());
        AttendanceException ex = assertThrows(AttendanceException.class, () -> service.getSeance(id));
        assertEquals("NOT_FOUND", ex.getCode());
    }

    @Test
    void creerSeancePositionneStatutParDefaut() {
        when(repository.saveSeance(any())).thenAnswer(inv -> inv.getArgument(0));
        Seance resultat = service.creerSeance(new Seance());
        assertEquals(StatutSeance.PLANIFIEE, resultat.getStatut());
    }

    private PresenceSuivi ligne(UUID eleve, UUID annee, UUID periode, StatutPresence statut,
                                TypeAbsence type, Integer minutes, String volume) {
        return new PresenceSuivi(eleve, annee, periode, statut, type, minutes, new BigDecimal(volume));
    }
}

package com.nexusgesti.structure.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmploiDuTempsTest {

    private PlageEmploi plage(UUID creneauId, UUID salleId, UUID enseignantId) {
        PlageEmploi p = new PlageEmploi();
        p.setIdPlage(UUID.randomUUID());
        p.setCoursId(UUID.randomUUID());
        p.setCreneauId(creneauId);
        p.setSalleId(salleId);
        p.setEnseignantId(enseignantId);
        return p;
    }

    @Test
    void detecteSalleDoublee() {
        UUID creneau = UUID.randomUUID();
        UUID salle = UUID.randomUUID();
        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.getPlages().add(plage(creneau, salle, UUID.randomUUID()));
        emploi.getPlages().add(plage(creneau, salle, UUID.randomUUID()));

        List<ConflitEmploi> conflits = emploi.detecterConflits();

        assertTrue(conflits.stream().anyMatch(c -> c.getType() == TypeConflit.SALL_DOUBLEE));
    }

    @Test
    void detecteEnseignantDouble() {
        UUID creneau = UUID.randomUUID();
        UUID enseignant = UUID.randomUUID();
        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.getPlages().add(plage(creneau, UUID.randomUUID(), enseignant));
        emploi.getPlages().add(plage(creneau, UUID.randomUUID(), enseignant));

        List<ConflitEmploi> conflits = emploi.detecterConflits();

        assertTrue(conflits.stream().anyMatch(c -> c.getType() == TypeConflit.ENSEIGNANT_DOUBLE));
    }

    @Test
    void aucuneConflitSurCreneauxDifferents() {
        EmploiDuTemps emploi = new EmploiDuTemps();
        emploi.getPlages().add(plage(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));
        emploi.getPlages().add(plage(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

        assertTrue(emploi.detecterConflits().isEmpty());
    }

    @Test
    void conflitSalleEstBloquant() {
        assertTrue(new ConflitEmploi(TypeConflit.SALL_DOUBLEE, UUID.randomUUID(), UUID.randomUUID()).estBloquant());
        assertFalse(new ConflitEmploi(TypeConflit.HORS_CYCLE, UUID.randomUUID(), UUID.randomUUID()).estBloquant());
    }

    @Test
    void publicationActiveEtPublie() {
        EmploiDuTemps emploi = new EmploiDuTemps().publier();
        assertTrue(emploi.isActive());
        assertTrue(emploi.isPublie());
    }

    @Test
    void creneauDonneDureeEnMinutes() {
        Creneau c = new Creneau();
        c.setHeureDebut(LocalTime.of(8, 0));
        c.setHeureFin(LocalTime.of(9, 30));
        assertEquals(90, c.getDureeMinutes());
    }
}

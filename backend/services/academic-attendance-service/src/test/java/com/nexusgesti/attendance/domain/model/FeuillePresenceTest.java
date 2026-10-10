package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeuillePresenceTest {

    private FeuillePresence feuille() {
        return new FeuillePresence().ouvrir();
    }

    @Test
    void ouverturePlaceEnBrouillon() {
        FeuillePresence f = feuille();
        assertEquals(StatutFeuille.BROUILLON, f.getStatut());
    }

    @Test
    void pointageCreeLigneEtCalculeEffectif() {
        FeuillePresence f = feuille();
        f.pointer(UUID.randomUUID(), StatutPresence.PRESENT, UUID.randomUUID());
        f.pointer(UUID.randomUUID(), StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID());
        assertEquals(2, f.getEffectifTheorique());
        assertEquals(1, f.getEffectifPresent());
        assertEquals(1, f.getNbAbsents());
    }

    @Test
    void appelGlobalAppliqueATous() {
        FeuillePresence f = feuille();
        f.pointer(UUID.randomUUID(), StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID());
        f.pointer(UUID.randomUUID(), StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID());
        f.faireAppelGlobal(StatutPresence.PRESENT, UUID.randomUUID());
        assertEquals(2, f.getEffectifPresent());
    }

    @Test
    void appelGlobalSansStatutRefuse() {
        FeuillePresence f = feuille();
        AttendanceException ex = assertThrows(AttendanceException.class,
                () -> f.faireAppelGlobal(null, UUID.randomUUID()));
        assertEquals("INVALIDE", ex.getCode());
    }

    @Test
    void cycleSoumettreValiderCloturer() {
        FeuillePresence f = feuille();
        f.soumettre();
        assertEquals(StatutFeuille.SOUMISE, f.getStatut());
        f.valider(UUID.randomUUID());
        assertEquals(StatutFeuille.VALIDEE, f.getStatut());
        f.cloturer();
        assertEquals(StatutFeuille.CLOTUREE, f.getStatut());
    }

    @Test
    void validerSansSoumissionRefuse() {
        FeuillePresence f = feuille();
        assertThrows(AttendanceException.class, () -> f.valider(UUID.randomUUID()));
    }

    @Test
    void cloturerSansValidationRefuse() {
        FeuillePresence f = feuille();
        assertThrows(AttendanceException.class, f::cloturer);
    }

    @Test
    void modificationApresValidationMarquee() {
        FeuillePresence f = feuille();
        UUID eleve = UUID.randomUUID();
        f.pointer(eleve, StatutPresence.PRESENT, UUID.randomUUID());
        f.soumettre();
        f.valider(UUID.randomUUID());
        f.pointer(eleve, StatutPresence.RETARD, UUID.randomUUID());
        assertTrue(f.trouver(eleve).orElseThrow().isModifieeApresValidation());
    }

    @Test
    void modificationApresClotureRefusee() {
        FeuillePresence f = feuille();
        UUID eleve = UUID.randomUUID();
        f.pointer(eleve, StatutPresence.PRESENT, UUID.randomUUID());
        f.soumettre();
        f.valider(UUID.randomUUID());
        f.cloturer();
        AttendanceException ex = assertThrows(AttendanceException.class,
                () -> f.pointer(eleve, StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID()));
        assertEquals("ETAT_INVALIDE", ex.getCode());
    }

    @Test
    void tauxPresenceCalcule() {
        FeuillePresence f = feuille();
        f.pointer(UUID.randomUUID(), StatutPresence.PRESENT, UUID.randomUUID());
        f.pointer(UUID.randomUUID(), StatutPresence.PRESENT, UUID.randomUUID());
        f.pointer(UUID.randomUUID(), StatutPresence.ABSENT_NON_EXCUSE, UUID.randomUUID());
        f.pointer(UUID.randomUUID(), StatutPresence.RETARD, UUID.randomUUID());
        assertEquals(new BigDecimal("75.00"), f.getTauxPresence());
    }
}

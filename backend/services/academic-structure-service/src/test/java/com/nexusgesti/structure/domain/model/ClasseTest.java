package com.nexusgesti.structure.domain.model;

import com.nexusgesti.structure.domain.StructureException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClasseTest {

    private Classe classe(StatutClasse statut, Integer capacite, int effectif) {
        Classe c = new Classe();
        c.setCode("6A");
        c.setLibelle("Sixieme A");
        c.setStatut(statut);
        c.setCapacite(capacite);
        c.setEffectifReel(effectif);
        return c;
    }

    @Test
    void ouvertureDepuisOuverte() {
        Classe c = classe(StatutClasse.OUVERTE, 30, 0).ouvrir();
        assertEquals(StatutClasse.EN_COURS, c.getStatut());
    }

    @Test
    void ouvertureDepuisEnCoursRefusee() {
        Classe c = classe(StatutClasse.EN_COURS, 30, 0);
        StructureException ex = assertThrows(StructureException.class, c::ouvrir);
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void clotureDepuisEnCours() {
        Classe c = classe(StatutClasse.EN_COURS, 30, 10).cloturer();
        assertEquals(StatutClasse.CLOTUREE, c.getStatut());
    }

    @Test
    void clotureDepuisClotureeRefusee() {
        Classe c = classe(StatutClasse.CLOTUREE, 30, 0);
        assertThrows(StructureException.class, c::cloturer);
    }

    @Test
    void tauxRemplissage() {
        Classe c = classe(StatutClasse.EN_COURS, 40, 10);
        assertEquals(new BigDecimal("0.2500"), c.getTauxRemplissage());
    }

    @Test
    void tauxRemplissageSansCapacite() {
        Classe c = classe(StatutClasse.EN_COURS, null, 10);
        assertEquals(BigDecimal.ZERO, c.getTauxRemplissage());
        assertFalse(c.estPlein());
    }

    @Test
    void estPlein() {
        assertTrue(classe(StatutClasse.EN_COURS, 20, 20).estPlein());
        assertFalse(classe(StatutClasse.EN_COURS, 20, 19).estPlein());
    }
}

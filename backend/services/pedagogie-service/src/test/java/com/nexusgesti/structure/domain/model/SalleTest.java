package com.nexusgesti.structure.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalleTest {

    private Salle salle(TypeSalle type, int capacite, boolean disponible) {
        Salle s = new Salle();
        s.setCode("S1");
        s.setType(type);
        s.setCapacite(capacite);
        s.setDisponible(disponible);
        return s;
    }

    @Test
    void compatibleSiTypeEtCapacite() {
        assertTrue(salle(TypeSalle.COURS, 40, true).estCompatible(TypeSalle.COURS, 35));
    }

    @Test
    void compatibleSansTypeExige() {
        assertTrue(salle(TypeSalle.COURS, 40, true).estCompatible(null, 35));
    }

    @Test
    void incompatibleSiTypeDifferent() {
        assertFalse(salle(TypeSalle.LABORATOIRE, 40, true).estCompatible(TypeSalle.COURS, 10));
    }

    @Test
    void incompatibleSiEffectifTropGrand() {
        assertFalse(salle(TypeSalle.COURS, 20, true).estCompatible(TypeSalle.COURS, 25));
    }

    @Test
    void incompatibleSiIndisponible() {
        assertFalse(salle(TypeSalle.COURS, 40, false).estCompatible(TypeSalle.COURS, 10));
    }
}

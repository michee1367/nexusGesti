package com.nexusgesti.structure.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegimeHoraireTest {

    @Test
    void volumeHebdomadaire() {
        RegimeHoraire r = new RegimeHoraire();
        r.setJours(List.of(JourSemaine.LUNDI, JourSemaine.MARDI, JourSemaine.MERCREDI,
                JourSemaine.JEUDI, JourSemaine.VENDREDI));
        r.setCreneauxParJour(6);
        r.setDureeCreneau(60);
        assertEquals(1800, r.getVolumeHebdomadaire());
    }

    @Test
    void volumeNulSansJour() {
        RegimeHoraire r = new RegimeHoraire();
        r.setCreneauxParJour(6);
        r.setDureeCreneau(60);
        assertEquals(0, r.getVolumeHebdomadaire());
    }
}

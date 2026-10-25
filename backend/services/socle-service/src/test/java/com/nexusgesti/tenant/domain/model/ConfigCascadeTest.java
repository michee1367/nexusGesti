package com.nexusgesti.tenant.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ConfigCascadeTest {

    private ConfigCascade config(StrategieAggregation strategie) {
        ConfigCascade c = new ConfigCascade();
        c.setStrategie(strategie);
        c.setNiveauxActifs(List.of(NiveauAcalcul.UE, NiveauAcalcul.COURS_ECUE));
        c.setArrondi(TypeArrondi.DECIMALE);
        c.setPrecision(2);
        return c;
    }

    @Test
    void moyennePondereeArrondie() {
        ConfigCascade c = config(StrategieAggregation.MOYENNE_PONDEREE);
        BigDecimal r = c.agreger(NiveauAcalcul.UE, List.of(
                NotePonderee.of(new BigDecimal("12"), new BigDecimal("2")),
                NotePonderee.of(new BigDecimal("8"), new BigDecimal("6"))));
        assertEquals(new BigDecimal("9.00"), r);
    }

    @Test
    void niveauInactifRetourneNull() {
        ConfigCascade c = config(StrategieAggregation.MOYENNE_NON_PONDEREE);
        assertNull(c.agreger(NiveauAcalcul.PERIODE, List.of(
                NotePonderee.of(new BigDecimal("12"), null))));
    }

    @Test
    void meilleureNote() {
        ConfigCascade c = config(StrategieAggregation.MEILLEURE_NOTE);
        BigDecimal r = c.agreger(NiveauAcalcul.COURS_ECUE, List.of(
                NotePonderee.of(new BigDecimal("7"), null),
                NotePonderee.of(new BigDecimal("15"), null)));
        assertEquals(new BigDecimal("15.00"), r);
    }

    @Test
    void moyenneSimpleNonPonderee() {
        ConfigCascade c = config(StrategieAggregation.MOYENNE_NON_PONDEREE);
        BigDecimal r = c.agreger(NiveauAcalcul.COURS_ECUE, List.of(
                NotePonderee.of(new BigDecimal("10"), null),
                NotePonderee.of(new BigDecimal("14"), null)));
        assertEquals(new BigDecimal("12.00"), r);
    }

    @Test
    void derniereNote() {
        ConfigCascade c = config(StrategieAggregation.DERNIERE_NOTE);
        BigDecimal r = c.agreger(NiveauAcalcul.COURS_ECUE, List.of(
                NotePonderee.of(new BigDecimal("7"), null),
                NotePonderee.of(new BigDecimal("18"), null)));
        assertEquals(new BigDecimal("18.00"), r);
    }
}
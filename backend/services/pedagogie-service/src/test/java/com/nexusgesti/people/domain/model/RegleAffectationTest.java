package com.nexusgesti.people.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegleAffectationTest {

    private final RegleAffectation regle = new RegleAffectation();

    private AffectationEnseignant affectation(UUID classeId, boolean titulaire) {
        AffectationEnseignant a = new AffectationEnseignant();
        a.setClasseId(classeId);
        a.setTitulaire(titulaire);
        return a;
    }

    @Test
    void primaireExigeUneSeuleAffectationTitulaire() {
        UUID classe = UUID.randomUUID();
        ResultatValidation ok = regle.valider(RegleAffectation.PRIMAIRE, classe,
                List.of(affectation(classe, true)));
        assertTrue(ok.estValide());

        ResultatValidation ko = regle.valider(RegleAffectation.PRIMAIRE, classe,
                List.of(affectation(classe, true), affectation(classe, false)));
        assertFalse(ko.estValide());
        assertTrue(ko.getViolations().contains("PRIMAIRE_EXIGE_UNE_SEULE_AFFECTATION"));
    }

    @Test
    void secondaireExigeExactementUnTitulaire() {
        UUID classe = UUID.randomUUID();
        ResultatValidation ok = regle.valider(RegleAffectation.SECONDAIRE, classe,
                List.of(affectation(classe, true), affectation(classe, false)));
        assertTrue(ok.estValide());

        ResultatValidation ko = regle.valider(RegleAffectation.SECONDAIRE, classe,
                List.of(affectation(classe, false), affectation(classe, false)));
        assertFalse(ko.estValide());
        assertTrue(ko.getViolations().contains("SECONDAIRE_EXIGE_EXACTEMENT_UN_TITULAIRE"));
    }

    @Test
    void universitaireSansTitulaireDeClasse() {
        UUID classe = UUID.randomUUID();
        ResultatValidation ok = regle.valider(RegleAffectation.UNIVERSITAIRE, classe,
                List.of(affectation(classe, false)));
        assertTrue(ok.estValide());

        ResultatValidation ko = regle.valider(RegleAffectation.UNIVERSITAIRE, classe,
                List.of(affectation(classe, true)));
        assertFalse(ko.estValide());
    }

    @Test
    void profilInconnuEstSignale() {
        ResultatValidation ko = regle.valider("MATERNEL", UUID.randomUUID(), List.of());
        assertFalse(ko.estValide());
        assertTrue(ko.getViolations().contains("PROFIL_INCONNU"));
    }

    @Test
    void nombreDeTitulairesRequis() {
        assertEquals(1, regle.getNombreTitulairesRequis(RegleAffectation.PRIMAIRE));
        assertEquals(1, regle.getNombreTitulairesRequis(RegleAffectation.SECONDAIRE));
        assertEquals(0, regle.getNombreTitulairesRequis(RegleAffectation.UNIVERSITAIRE));
    }
}

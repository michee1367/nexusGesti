package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BulletinTest {

    private Bulletin bulletin() {
        Bulletin b = new Bulletin();
        b.setIdBulletin(java.util.UUID.randomUUID());
        b.setStatut(StatutBulletin.BROUILLON);
        b.setVersion(1);
        return b;
    }

    private LigneBulletin ligne(String code, String moyenne, String coefficient) {
        LigneBulletin l = new LigneBulletin("MATIERE", code, code, new BigDecimal(coefficient));
        l.setMoyennePeriode(new BigDecimal(moyenne));
        return l;
    }

    @Test
    void calculerPonderedeEtStatutCalcule() {
        Bulletin b = bulletin();
        b.ajouterLigne(ligne("MATH", "10", "1"));
        b.ajouterLigne(ligne("PHY", "20", "3"));
        b.ajouterLigne(ligne("ABS", "5", "1"));
        b.getLignes().get(2).setAbsent(true);
        b.calculer();
        assertEquals(new BigDecimal("17.50"), b.getMoyenneGenerale());
        assertEquals(StatutBulletin.CALCULE, b.getStatut());
        assertEquals(new BigDecimal("0.2500"), b.getLignes().get(0).getPoidsNormalise());
    }

    @Test
    void publierExigeUnCalculPrealable() {
        Bulletin b = bulletin();
        assertThrows(AssessmentException.class, b::publier);
        b.ajouterLigne(ligne("MATH", "12", "1"));
        b.calculer().publier();
        assertEquals(StatutBulletin.PUBLIE, b.getStatut());
    }

    @Test
    void cycleGenerationEtSignature() {
        Bulletin b = bulletin();
        b.ajouterLigne(ligne("MATH", "12", "1"));
        b.calculer().publier().genererDocuments().signer(null);
        assertEquals(StatutBulletin.SIGNE, b.getStatut());
    }

    @Test
    void rectificationExigeMotifEtIncrementeVersion() {
        Bulletin b = bulletin();
        b.ajouterLigne(ligne("MATH", "12", "1"));
        b.calculer().publier();
        assertThrows(AssessmentException.class, () -> b.rectifier(" "));
        b.rectifier("erreur de saisie");
        assertEquals(StatutBulletin.RECTIFIE, b.getStatut());
        assertEquals(2, b.getVersion());
    }

    @Test
    void ligneNonModifiableApresPublication() {
        Bulletin b = bulletin();
        b.ajouterLigne(ligne("MATH", "12", "1"));
        b.calculer().publier();
        assertThrows(AssessmentException.class, () -> b.ajouterLigne(ligne("PHY", "10", "1")));
    }

    @Test
    void syntheseAbsencesAlimenteLeBulletin() {
        Bulletin b = bulletin();
        SyntheseAbsences s = new SyntheseAbsences();
        s.setSeancesJustifiees(2);
        s.setSeancesInjustifiees(1);
        s.setRetardJours(3);
        s.setRetardHeures(4);
        s.setRegleApplicable("REG-ASSID");
        s.setRespecteRegle(false);
        b.definirSyntheseAbsences(s);
        assertEquals(2, b.getNbAbsencesJustifiees());
        assertEquals(1, b.getNbAbsencesInjustifiees());
        assertEquals(7, b.getNbRetards());
        assertTrue(b.getSyntheseAbsences().estEnAlerte());
    }
}

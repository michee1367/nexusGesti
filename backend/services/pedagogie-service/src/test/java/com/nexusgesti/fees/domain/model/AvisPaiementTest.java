package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AvisPaiementTest {

    @Test
    void ajouterLigneRecalculeLesTotaux() {
        AvisPaiement avis = new AvisPaiement();
        avis.setIdAvis(UUID.randomUUID());
        avis.ajouterLigne(ligne(100, 10, 1));
        assertEquals(new BigDecimal("100.00"), avis.getMontantBrut());
        assertEquals(new BigDecimal("90.00"), avis.getMontantNet());
        assertEquals(new BigDecimal("90.00"), avis.getSoldeDu());
    }

    @Test
    void emettrePositionneDateEtStatut() {
        AvisPaiement avis = new AvisPaiement();
        avis.ajouterLigne(ligne(100, 0, 1));
        avis.valider();
        avis.emettre();
        assertEquals(StatutAvis.EMIS, avis.getStatut());
        assertNotNull(avis.getDateEmission());
    }

    @Test
    void reglementTotalSoldeLAvis() {
        AvisPaiement avis = new AvisPaiement();
        avis.setIdAvis(UUID.randomUUID());
        avis.setStatut(StatutAvis.EMIS);
        avis.ajouterLigne(ligne(100, 0, 1));
        avis.enregistrerReglement(new BigDecimal("100.00"));
        assertTrue(avis.estSolde());
        assertEquals(StatutAvis.REGLE, avis.getStatut());
    }

    @Test
    void avisSoldeNonAnnulable() {
        AvisPaiement avis = new AvisPaiement();
        avis.setStatut(StatutAvis.EMIS);
        avis.ajouterLigne(ligne(100, 0, 1));
        avis.enregistrerReglement(new BigDecimal("100.00"));
        assertThrows(FeesException.class, () -> avis.annuler("annulation"));
    }

    @Test
    void appliquerRemiseReduitLeSolde() {
        AvisPaiement avis = new AvisPaiement();
        avis.setStatut(StatutAvis.EMIS);
        avis.ajouterLigne(ligne(100, 0, 1));
        avis.appliquerRemise(UUID.randomUUID(), new BigDecimal("20.00"));
        assertEquals(new BigDecimal("20.00"), avis.getMontantReduction());
        assertEquals(new BigDecimal("80.00"), avis.getMontantNet());
        assertEquals(new BigDecimal("80.00"), avis.getSoldeDu());
    }

    @Test
    void retardeLaDateGlobale() {
        AvisPaiement avis = new AvisPaiement();
        avis.setStatut(StatutAvis.EMIS);
        avis.setDateEcheanceGlobale(LocalDate.of(2026, 1, 1));
        avis.ajouterLigne(ligne(100, 0, 1));
        assertTrue(avis.estEnRetard(LocalDate.of(2026, 2, 1)));
        assertEquals(31, avis.getJoursRetard(LocalDate.of(2026, 2, 1)));
    }

    private LigneFrais ligne(int brut, int reduction, int nbEcheances) {
        LigneFrais ligne = new LigneFrais();
        ligne.setTypeFrais(TypeFrais.SCOLARITE);
        ligne.setMontantBrut(new BigDecimal(brut + ".00"));
        ligne.setMontantReduction(new BigDecimal(reduction + ".00"));
        ligne.setNbEcheances(nbEcheances);
        ligne.calculerNet();
        ligne.genererEcheances();
        return ligne;
    }
}
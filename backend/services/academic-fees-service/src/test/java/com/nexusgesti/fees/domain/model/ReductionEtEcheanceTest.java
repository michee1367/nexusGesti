package com.nexusgesti.fees.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReductionEtEcheanceTest {

    @Test
    void reductionPourcentageAppliqueLeTaux() {
        Reduction reduction = new Reduction();
        reduction.setType(TypeReduction.POURCENTAGE);
        reduction.setValeur(new BigDecimal("10"));
        BigDecimal montant = reduction.appliquer(new BigDecimal("200.00"), new ContexteReduction());
        assertEquals(new BigDecimal("20.00"), montant);
    }

    @Test
    void reductionMontantFixeBornParLeBrut() {
        Reduction reduction = new Reduction();
        reduction.setType(TypeReduction.MONTANT_FIXE);
        reduction.setValeur(new BigDecimal("500"));
        BigDecimal montant = reduction.appliquer(new BigDecimal("200.00"), new ContexteReduction());
        assertEquals(new BigDecimal("200.00"), montant);
    }

    @Test
    void reductionTarifReduitSoustraitLeTarif() {
        Reduction reduction = new Reduction();
        reduction.setType(TypeReduction.TARIF_REDUIT);
        reduction.setValeur(new BigDecimal("80.00"));
        BigDecimal montant = reduction.appliquer(new BigDecimal("100.00"), new ContexteReduction());
        assertEquals(new BigDecimal("20.00"), montant);
    }

    @Test
    void conditionNonSatisfaiteAnnuleLaRemise() {
        Reduction reduction = new Reduction();
        reduction.setType(TypeReduction.POURCENTAGE);
        reduction.setValeur(new BigDecimal("10"));
        ConditionReduction condition = new ConditionReduction();
        condition.setCritere("niveau");
        condition.setOperateur("=");
        condition.setValeur("UNIVERSITAIRE");
        reduction.setConditions(java.util.List.of(condition));
        ContexteReduction contexte = new ContexteReduction();
        contexte.setNiveau("SECONDAIRE");
        assertEquals(BigDecimal.ZERO, reduction.appliquer(new BigDecimal("200.00"), contexte));
    }

    @Test
    void echeanceRegleeEnEcheances() {
        LigneFrais ligne = new LigneFrais();
        ligne.setId(java.util.UUID.randomUUID());
        ligne.setMontantNet(new BigDecimal("100.00"));
        ligne.setNbEcheances(2);
        ligne.genererEcheances();
        assertEquals(2, ligne.getEcheances().size());
        assertEquals(new BigDecimal("50.00"), ligne.getEcheances().get(0).getMontant());
        assertEquals(new BigDecimal("50.00"), ligne.getEcheances().get(1).getMontant());

        Echeance echeance = ligne.getEcheances().get(0);
        echeance.enregistrerReglement(new BigDecimal("50.00"), LocalDate.now());
        assertEquals(StatutEcheance.REGLEE, echeance.getStatut());
        assertEquals(0, echeance.getSolde().signum());
    }

    @Test
    void echeanceRegleeNonReportable() {
        Echeance echeance = new Echeance();
        echeance.setMontant(new BigDecimal("50.00"));
        echeance.enregistrerReglement(new BigDecimal("50.00"), LocalDate.now());
        assertThrows(com.nexusgesti.fees.domain.FeesException.class,
                () -> echeance.reporter(LocalDate.now().plusDays(30)));
    }
}
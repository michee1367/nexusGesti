package com.nexusgesti.boulangerie.production.domain.model;

import com.nexusgesti.boulangerie.production.domain.ProductionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FourneeTest {

    private Fournee fournee() {
        Fournee fournee = new Fournee();
        fournee.setNumero("FOU-00001");
        fournee.setRecetteCode("REC-BAG");
        fournee.setQuantitePrevue(BigDecimal.valueOf(100));
        return fournee;
    }

    @Test
    void cycleNominal() {
        Fournee fournee = fournee();
        fournee.lancer();
        assertEquals(StatutFournee.EN_COURS, fournee.getStatut());
        assertNotNull(fournee.getDateDebut());
        fournee.enregistrerRendu(BigDecimal.valueOf(95));
        fournee.cuire();
        assertEquals(StatutFournee.CUITE, fournee.getStatut());
        fournee.valider();
        assertEquals(StatutFournee.VALIDEE, fournee.getStatut());
        assertNotNull(fournee.getDateFin());
    }

    @Test
    void cuireSansLancerEchoue() {
        assertThrows(ProductionException.class, () -> fournee().cuire());
    }

    @Test
    void rendementEgalRealiseSurPrevu() {
        Fournee fournee = fournee();
        fournee.lancer();
        fournee.enregistrerRendu(BigDecimal.valueOf(90));
        assertEquals(0, fournee.getRendement().compareTo(new BigDecimal("0.9000")));
    }

    @Test
    void ecartMatiereSommeTheoriqueMoinsReelle() {
        Fournee fournee = fournee();
        fournee.lancer();
        LigneConsommation ligne = new LigneConsommation();
        ligne.setArticleCode("FARINE");
        ligne.setQuantiteTheorique(BigDecimal.TEN);
        ligne.setQuantiteReelle(BigDecimal.valueOf(9));
        fournee.declarerConsommation(ligne);
        assertEquals(0, fournee.getEcartMatiere().compareTo(BigDecimal.ONE));
    }

    @Test
    void annulerUneFourneeValideeEchoue() {
        Fournee fournee = fournee();
        fournee.lancer();
        fournee.enregistrerRendu(BigDecimal.ONE);
        fournee.cuire();
        fournee.valider();
        assertThrows(ProductionException.class, () -> fournee.annuler("erreur"));
    }

    @Test
    void perteEstValoriseeAuPrixMoyen() {
        PerteProduction perte = new PerteProduction();
        perte.setArticleCode("FARINE");
        perte.setQuantite(BigDecimal.valueOf(2));
        perte.setTypePerte(TypePerte.CASSE);
        perte.valoriser(new BigDecimal("1.50"));
        assertEquals(0, perte.getValorisation().compareTo(new BigDecimal("3.00")));
    }

    @Test
    void declarerPerteRattacheLaFournee() {
        Fournee fournee = fournee();
        PerteProduction perte = new PerteProduction();
        perte.setQuantite(BigDecimal.ONE);
        perte.setTypePerte(TypePerte.PATE_PERDUE);
        fournee.declarerPerte(perte);
        assertEquals(fournee.getIdFournee(), perte.getFourneeId());
        assertEquals(1, fournee.getPertes().size());
    }
}

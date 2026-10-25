package com.nexusgesti.boulangerie.produit.domain.model;

import com.nexusgesti.boulangerie.produit.domain.ProduitException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    @Test
    void prixTTCAppliqueLeTauxDeTVA() {
        Produit produit = new Produit();
        produit.setPrixVenteHT(new BigDecimal("100.00"));
        produit.setTauxTva(new BigDecimal("16"));
        assertEquals(new BigDecimal("116.00"), produit.getPrixTTC());
    }

    @Test
    void prixTTCAZeroTVA() {
        Produit produit = new Produit();
        produit.setPrixVenteHT(new BigDecimal("100.00"));
        produit.setTauxTva(BigDecimal.ZERO);
        assertEquals(new BigDecimal("100.00"), produit.getPrixTTC());
    }

    @Test
    void contientAllergeneReconnaitLesAllergenesListes() {
        Produit produit = new Produit();
        produit.setAllergenes(List.of(Allergene.GLUTEN, Allergene.LACTOSE));
        assertTrue(produit.contientAllergene(Allergene.GLUTEN));
        assertFalse(produit.contientAllergene(Allergene.OEUF));
    }

    @Test
    void activerDepuisInactifEstPermis() {
        Produit produit = new Produit();
        produit.setActif(false);
        produit.activer();
        assertTrue(produit.isActif());
    }

    @Test
    void activerDepuisActifEstRefuse() {
        Produit produit = new Produit();
        produit.setActif(true);
        assertThrows(ProduitException.class, produit::activer);
    }

    @Test
    void desactiverDepuisActifEstPermis() {
        Produit produit = new Produit();
        produit.setActif(true);
        produit.desactiver();
        assertFalse(produit.isActif());
    }

    @Test
    void desactiverDepuisInactifEstRefuse() {
        Produit produit = new Produit();
        produit.setActif(false);
        assertThrows(ProduitException.class, produit::desactiver);
    }

    @Test
    void estFabricableRefleteLeFlagMaintenuParLeService() {
        Produit produit = new Produit();
        assertFalse(produit.estFabricable());
        produit.setFabricable(true);
        assertTrue(produit.estFabricable());
    }

    @Test
    void setAllergenesCopieDefensivement() {
        Produit produit = new Produit();
        List<Allergene> source = new ArrayList<>(List.of(Allergene.GLUTEN));
        produit.setAllergenes(source);
        source.add(Allergene.OEUF);
        assertEquals(List.of(Allergene.GLUTEN), produit.getAllergenes());
        produit.getAllergenes().add(Allergene.LACTOSE);
        assertEquals(2, produit.getAllergenes().size());
    }
}

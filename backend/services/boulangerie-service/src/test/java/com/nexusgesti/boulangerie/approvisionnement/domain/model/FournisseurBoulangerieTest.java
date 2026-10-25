package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FournisseurBoulangerieTest {

    private FournisseurBoulangerie fournisseur() {
        FournisseurBoulangerie fournisseur = new FournisseurBoulangerie();
        fournisseur.setCode("FRN-MOULIN");
        fournisseur.setRaisonSociale("Moulin du Fleuve");
        fournisseur.setDelaiLivraisonJours(2);
        return fournisseur;
    }

    @Test
    void fiableQuandActifEtDelaiRenseigne() {
        assertTrue(fournisseur().estFiable());
        assertTrue(fournisseur().isActif());
        assertEquals(2, fournisseur().getDelai());
    }

    @Test
    void pasFiableQuandDesactive() {
        FournisseurBoulangerie fournisseur = fournisseur();
        fournisseur.desactiver();

        assertFalse(fournisseur.estFiable());
        assertFalse(fournisseur.isActif());

        fournisseur.reactiver();
        assertTrue(fournisseur.isActif());
    }

    @Test
    void verifierModifiableSansCodeEstRefuse() {
        FournisseurBoulangerie fournisseur = fournisseur();
        fournisseur.setCode(null);

        assertThrows(ApprovisionnementException.class, fournisseur::verifierModifiable);
    }

    @Test
    void verifierModifiableSansRaisonSocialeEstRefuse() {
        FournisseurBoulangerie fournisseur = fournisseur();
        fournisseur.setRaisonSociale(" ");

        assertThrows(ApprovisionnementException.class, fournisseur::verifierModifiable);
    }

    @Test
    void delaiNegatifEstRefuse() {
        FournisseurBoulangerie fournisseur = fournisseur();
        fournisseur.setDelaiLivraisonJours(-1);

        assertThrows(ApprovisionnementException.class, fournisseur::verifierModifiable);
    }

    @Test
    void verifierModifiableValideNeLeveRien() {
        assertDoesNotThrow(() -> fournisseur().verifierModifiable());
    }
}

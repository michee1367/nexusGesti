package com.nexusgesti.people.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DossierEleveTest {

    private FichierJoint piece(CategoriePiece categorie) {
        FichierJoint f = new FichierJoint();
        f.setCategorie(categorie);
        f.setNom("fichier");
        f.setUri("s3://bucket/fichier");
        return f;
    }

    @Test
    void dossierVideEstIncomplet() {
        DossierEleve d = new DossierEleve();
        assertFalse(d.estComplet());
        assertEquals(2, d.getPiecesManquantes().size());
    }

    @Test
    void ajoutPhotoPuisActeRendLeDossierComplet() {
        DossierEleve d = new DossierEleve();
        d.ajouterPiece(piece(CategoriePiece.PHOTO_IDENTITE));
        assertEquals(StatutDossier.INCOMPLET, d.getStatut());
        d.ajouterPiece(piece(CategoriePiece.ACTE_NAISSANCE));
        assertTrue(d.estComplet());
        assertEquals(StatutDossier.COMPLET, d.getStatut());
    }

    @Test
    void pieceNonObligatoireNaugmentePasLaCompletion() {
        DossierEleve d = new DossierEleve();
        d.ajouterPiece(piece(CategoriePiece.BULLETIN_ANTERIEUR));
        assertFalse(d.estComplet());
    }

    @Test
    void archivageFigeLeStatut() {
        DossierEleve d = new DossierEleve();
        d.ajouterPiece(piece(CategoriePiece.PHOTO_IDENTITE));
        d.archiver();
        assertEquals(StatutDossier.ARCHIVE, d.getStatut());
        d.ajouterPiece(piece(CategoriePiece.ACTE_NAISSANCE));
        assertEquals(StatutDossier.ARCHIVE, d.getStatut());
    }
}

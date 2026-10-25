package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CommandeAchatTest {

    private CommandeAchat commande() {
        CommandeAchat commande = new CommandeAchat();
        commande.setIdCommande(UUID.randomUUID());
        commande.setFournisseurTiersCode("FRN-1");
        commande.setDate(LocalDate.of(2026, 10, 20));
        LigneCommandeAchat ligne = new LigneCommandeAchat();
        ligne.setArticleCode("FARINE");
        ligne.setDesignation("Farine de ble");
        ligne.setQuantiteCommandee(BigDecimal.valueOf(10));
        ligne.setPrixUnitaire(new BigDecimal("2.50"));
        ligne.setTauxTva(new BigDecimal("16"));
        commande.ajouterLigne(ligne);
        return commande;
    }

    @Test
    void validerCalculeLesMontantsEtPasseEnValidee() {
        CommandeAchat commande = commande();

        commande.valider();

        assertEquals(StatutCommandeAchat.VALIDEE, commande.getStatut());
        assertEquals(0, commande.getMontantHT().compareTo(new BigDecimal("25.00")));
        assertEquals(0, commande.getMontantTVA().compareTo(new BigDecimal("4.00")));
        assertEquals(0, commande.getMontantTTC().compareTo(new BigDecimal("29.00")));
    }

    @Test
    void validerSansLigneEstRefuse() {
        CommandeAchat commande = new CommandeAchat();
        commande.setFournisseurTiersCode("FRN-1");
        commande.setDate(LocalDate.now());

        assertThrows(ApprovisionnementException.class, commande::valider);
    }

    @Test
    void validerDepuisNonBrouillonEstRefusee() {
        CommandeAchat commande = commande();
        commande.valider();

        assertThrows(ApprovisionnementException.class, commande::valider);
    }

    @Test
    void envoiExigeCommandeValidee() {
        CommandeAchat commande = commande();

        assertThrows(ApprovisionnementException.class, commande::envoyer);

        commande.valider();
        commande.envoyer();
        assertEquals(StatutCommandeAchat.ENVOYEE, commande.getStatut());
        assertTrue(commande.estRecevable());
    }

    @Test
    void receptionExigeStatutEnvoyeOuPartiel() {
        CommandeAchat commande = commande();
        commande.valider();

        assertThrows(ApprovisionnementException.class, commande::marquerPartielle);
        assertThrows(ApprovisionnementException.class, commande::marquerReceptionnee);
    }

    @Test
    void resteARecevoirSommeLesEcarts() {
        CommandeAchat commande = commande();
        commande.valider();
        commande.envoyer();
        commande.getLignes().get(0).setQuantiteRecue(BigDecimal.valueOf(4));

        assertEquals(0, commande.getResteARecevoir().compareTo(BigDecimal.valueOf(6)));
    }

    @Test
    void annulationInterditeApresReception() {
        CommandeAchat commande = commande();
        commande.valider();
        commande.envoyer();
        commande.marquerReceptionnee();

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                () -> commande.annuler("plus besoin"));
        assertEquals("TRANSITION_INVALIDE", ex.getCode());
    }

    @Test
    void annulationExigeUnMotif() {
        CommandeAchat commande = commande();

        assertThrows(ApprovisionnementException.class, () -> commande.annuler(" "));
    }

    @Test
    void annulationDepuisBrouillonEstAcceptee() {
        CommandeAchat commande = commande();

        commande.annuler("doublon");

        assertEquals(StatutCommandeAchat.ANNULEE, commande.getStatut());
        assertEquals("doublon", commande.getMotifAnnulation());
        assertFalse(commande.estRecevable());
    }

    @Test
    void trouverLigneParCodeInconnuLeveNotFound() {
        CommandeAchat commande = commande();

        ApprovisionnementException ex = assertThrows(ApprovisionnementException.class,
                () -> commande.trouverLigne("SEL"));
        assertEquals("NOT_FOUND", ex.getCode());
    }
}

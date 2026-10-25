package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VenteTest {

    @Test
    void ajouterLigneCalculeMontantsEtValide() {
        Vente vente = new Vente();
        vente.setIdVente(UUID.randomUUID());
        vente.setSessionId(UUID.randomUUID());
        vente.setCleIdempotence("k1");

        LigneVente ligne = new LigneVente("PAIN", "Pain", new BigDecimal("2"),
                new BigDecimal("1.00"), new BigDecimal("0"), BigDecimal.ZERO, null);
        vente.ajouterLigne(ligne);
        vente.calculerMontants();
        assertEquals(new BigDecimal("2.00"), vente.getMontantHT());
        assertEquals(new BigDecimal("2.00"), vente.getMontantTTC());

        PaiementVente paiement = new PaiementVente(ModePaiementCaisse.ESPECES,
                vente.getMontantTTC(), new BigDecimal("5.00"), null);
        vente.ajouterPaiement(paiement);
        vente.valider();
        assertEquals(StatutVente.VALIDEE, vente.getStatut());
        assertEquals(new BigDecimal("3.00"), paiement.getRendu());
    }

    @Test
    void venteSansLigneNonValidee() {
        Vente vente = new Vente();
        vente.setSessionId(UUID.randomUUID());
        vente.setCleIdempotence("k2");
        assertThrows(VenteException.class, vente::verifierCreation);
    }

    @Test
    void annulationRefuserApresValidation() {
        Vente vente = new Vente();
        vente.setIdVente(UUID.randomUUID());
        vente.setSessionId(UUID.randomUUID());
        vente.setCleIdempotence("k3");
        LigneVente ligne = new LigneVente("CROISSANT", "Croissant", BigDecimal.ONE,
                new BigDecimal("1.50"), new BigDecimal("0"), null, null);
        vente.ajouterLigne(ligne);
        PaiementVente paiement = new PaiementVente(ModePaiementCaisse.ESPECES,
                vente.getMontantTTC(), vente.getMontantTTC(), null);
        vente.ajouterPaiement(paiement);
        vente.valider();
        vente.annuler("Erreur saisie");
        assertEquals(StatutVente.ANNULEE, vente.getStatut());
        vente.rembourser();
        assertEquals(StatutVente.REMBOURSEE, vente.getStatut());
    }
}

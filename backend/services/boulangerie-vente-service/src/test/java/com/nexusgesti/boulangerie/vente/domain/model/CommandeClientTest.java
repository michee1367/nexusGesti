package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CommandeClientTest {

    @Test
    void commandeAcompteEtRetrait() {
        CommandeClient commande = new CommandeClient("CLI-001", LocalDate.now(), LocalDate.now().plusDays(1));
        commande.ajouterLigne(new LigneCommandeClient("GATEAU", "Gateau", new BigDecimal("1"),
                new BigDecimal("20.00"), null));
        commande.confirmer();
        commande.encaisserAcompte(new BigDecimal("10.00"), ModePaiementCaisse.ESPECES, null);
        assertEquals(new BigDecimal("10.00"), commande.getSoldeDu());
        commande.demarrerProduction();
        commande.marquerPrete();
        assertThrows(VenteException.class, commande::retirer);
        commande.encaisserAcompte(new BigDecimal("10.00"), ModePaiementCaisse.ESPECES, null);
        commande.retirer();
        assertEquals(StatutCommandeClient.LIVREE, commande.getStatut());
    }

    @Test
    void annulationObligeMotif() {
        CommandeClient commande = new CommandeClient("CLI-002", LocalDate.now(), LocalDate.now());
        commande.ajouterLigne(new LigneCommandeClient("BAGUETTE", "Baguette", BigDecimal.ONE,
                new BigDecimal("0.80"), null));
        commande.confirmer();
        assertThrows(VenteException.class, () -> commande.annuler(null));
    }
}

package com.nexusgesti.boulangerie.vente.domain.port.out;

import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;

public interface VenteEventPublisher {

    void publierVenteEncaissee(Vente vente);
    void publierVenteAnnulee(Vente vente, String motif);
    void publierSessionCloturee(SessionCaisse session);
    void publierCommandeCreee(CommandeClient commande);
    void publierCommandeConfirmee(CommandeClient commande);
    void publierCommandeAnnulee(CommandeClient commande, String motif);
    void publierMouvementStock(MouvementStockIntention intention);
}

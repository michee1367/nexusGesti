package com.nexusgesti.boulangerie.approvisionnement.domain.port.out;

import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.MouvementStockIntention;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;

import java.util.List;

public interface ApprovisionnementEventPublisher {

    void publierCommandeValidee(CommandeAchat commande);
    void publierCommandeAnnulee(CommandeAchat commande, String motif);
    void publierReceptionAcceptee(Reception reception);
    void publierReceptionRefusee(Reception reception, String motif);
    void publierMouvementStock(MouvementStockIntention intention);
    void publierBesoinsSousSeuil(List<BesoinApprovisionnement> besoins);
}

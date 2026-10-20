package com.nexusgesti.boulangerie.approvisionnement.domain.port.out;

import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovisionnementRepository {

    FournisseurBoulangerie saveFournisseur(FournisseurBoulangerie fournisseur);
    Optional<FournisseurBoulangerie> findFournisseur(UUID id);
    List<FournisseurBoulangerie> listFournisseurs();

    CommandeAchat saveCommande(CommandeAchat commande);
    Optional<CommandeAchat> findCommande(UUID id);
    List<CommandeAchat> listCommandes();

    Reception saveReception(Reception reception);
    Optional<Reception> findReception(UUID id);
    List<Reception> listReceptions();
    List<Reception> findReceptionsByCommande(UUID commandeId);

    int countCommandes();
    int countReceptions();
}

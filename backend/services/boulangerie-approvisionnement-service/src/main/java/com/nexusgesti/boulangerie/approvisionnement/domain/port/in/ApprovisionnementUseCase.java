package com.nexusgesti.boulangerie.approvisionnement.domain.port.in;

import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Rapprochement;

import java.util.List;
import java.util.UUID;

public interface ApprovisionnementUseCase {

    FournisseurBoulangerie creerFournisseur(FournisseurBoulangerie fournisseur);
    FournisseurBoulangerie getFournisseur(UUID id);
    List<FournisseurBoulangerie> listFournisseurs();
    FournisseurBoulangerie modifierFournisseur(UUID id, FournisseurBoulangerie fournisseur);
    FournisseurBoulangerie changerEtatFournisseur(UUID id, boolean actif);
    EvaluationFournisseur evaluerFournisseur(UUID id);

    List<BesoinApprovisionnement> calculerBesoins(List<SaisieBesoin> saisies);
    List<BesoinApprovisionnement> listerSousSeuil(List<SaisieBesoin> saisies);

    CommandeAchat creerCommande(CommandeAchat commande);
    CommandeAchat getCommande(UUID id);
    List<CommandeAchat> listCommandes();
    CommandeAchat validerCommande(UUID id);
    CommandeAchat envoyerCommande(UUID id);
    CommandeAchat annulerCommande(UUID id, String motif);

    Reception creerReception(Reception reception);
    Reception getReception(UUID id);
    List<Reception> listReceptions();
    Reception controlerReception(UUID id);
    Reception accepterReception(UUID id);
    Reception refuserReception(UUID id, String motif);

    Rapprochement rapprocherCommande(UUID commandeId);

    record SaisieBesoin(String articleCode,
                        String libelle,
                        java.math.BigDecimal quantiteDisponible,
                        java.math.BigDecimal seuil,
                        java.math.BigDecimal consommationPrevue,
                        Integer delaiReapproJours) {
    }

    record EvaluationFournisseur(UUID idFournisseur,
                                 String code,
                                 boolean actif,
                                 boolean fiable,
                                 int delaiLivraisonJours,
                                 int nbCommandes,
                                 int nbReceptionsHonorees,
                                 int nbReceptionsRefusees) {
    }
}

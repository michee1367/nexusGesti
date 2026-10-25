package com.nexusgesti.boulangerie.vente.domain.port.in;

import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneCommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.ModePaiementCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface VenteUseCase {

    SessionCaisse ouvrirSession(LocalDate date, BigDecimal fondInitial, UUID ouvertePar);
    SessionCaisse getSession(UUID id);
    List<SessionCaisse> listSessions();
    SessionCaisse cloturerSession(UUID id, BigDecimal montantCompte);

    Vente creerVente(Vente vente, String idempotencyKey);
    Vente getVente(UUID id);
    List<Vente> listVentes(UUID sessionId);
    Vente encaisserVente(UUID id, EncaissementRequest request);
    Vente annulerVente(UUID id, String motif);
    Vente rembourserVente(UUID id);

    CommandeClient creerCommande(CommandeClient commande);
    CommandeClient getCommande(UUID id);
    List<CommandeClient> listCommandes();
    CommandeClient ajouterLigneCommande(UUID id, LigneCommandeClient ligne);
    CommandeClient confirmerCommande(UUID id);
    CommandeClient encaisserAcompte(UUID id, AcompteRequest request);
    CommandeClient demarrerProduction(UUID id);
    CommandeClient marquerPrete(UUID id);
    CommandeClient retirerCommande(UUID id);
    CommandeClient annulerCommande(UUID id, String motif);

    record EncaissementRequest(ModePaiementCaisse mode,
                               BigDecimal montant,
                               BigDecimal montantRecu,
                               String referenceExterne) {
    }

    record AcompteRequest(BigDecimal montant,
                          ModePaiementCaisse mode,
                          String reference) {
    }
}

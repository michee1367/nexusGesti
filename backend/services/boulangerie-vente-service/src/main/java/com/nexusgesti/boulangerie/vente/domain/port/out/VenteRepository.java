package com.nexusgesti.boulangerie.vente.domain.port.out;

import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VenteRepository {

    SessionCaisse saveSession(SessionCaisse session);
    Optional<SessionCaisse> findSession(UUID id);
    Optional<SessionCaisse> findSessionOuverte();
    List<SessionCaisse> listSessions();

    Vente saveVente(Vente vente);
    Optional<Vente> findVente(UUID id);
    List<Vente> listVentes(UUID sessionId);
    int countVentesBySession(UUID sessionId);

    CommandeClient saveCommande(CommandeClient commande);
    Optional<CommandeClient> findCommande(UUID id);
    List<CommandeClient> listCommandes();
    int countCommandes();
}

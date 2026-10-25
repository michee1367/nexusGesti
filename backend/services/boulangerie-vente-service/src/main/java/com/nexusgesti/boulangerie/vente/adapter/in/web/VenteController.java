package com.nexusgesti.boulangerie.vente.adapter.in.web;

import com.nexusgesti.boulangerie.vente.domain.model.CommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.LigneCommandeClient;
import com.nexusgesti.boulangerie.vente.domain.model.SessionCaisse;
import com.nexusgesti.boulangerie.vente.domain.model.Vente;
import com.nexusgesti.boulangerie.vente.domain.port.in.VenteUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class VenteController {

    private final VenteUseCase venteUseCase;

    public VenteController(VenteUseCase venteUseCase) {
        this.venteUseCase = venteUseCase;
    }

    // ---------- CAISSES ----------
    @GetMapping("/caisses")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<SessionCaisse> listSessions() {
        return venteUseCase.listSessions();
    }

    @PostMapping("/caisses/ouvrir")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionCaisse> ouvrirSession(@RequestBody OuvrirSessionRequest request) {
        SessionCaisse session = venteUseCase.ouvrirSession(
                request.date() == null ? LocalDate.now() : request.date(),
                request.fondInitial(),
                request.ouvertePar());
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @GetMapping("/caisses/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public SessionCaisse getSession(@PathVariable UUID id) {
        return venteUseCase.getSession(id);
    }

    @PostMapping("/caisses/{id}/cloturer")
    @PreAuthorize("hasRole('ADMIN')")
    public SessionCaisse cloturerSession(@PathVariable UUID id, @RequestBody CloturerSessionRequest request) {
        return venteUseCase.cloturerSession(id, request.montantCompte());
    }

    // ---------- VENTES ----------
    @GetMapping("/ventes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Vente> listVentes(@RequestParam(required = false) UUID sessionId) {
        return venteUseCase.listVentes(sessionId);
    }

    @PostMapping("/ventes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Vente> creerVente(@RequestHeader("Idempotency-Key") String idempotencyKey,
                                            @RequestBody Vente vente) {
        Vente created = venteUseCase.creerVente(vente, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/ventes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Vente getVente(@PathVariable UUID id) {
        return venteUseCase.getVente(id);
    }

    @PostMapping("/ventes/{id}/encaisser")
    @PreAuthorize("hasRole('ADMIN')")
    public Vente encaisserVente(@PathVariable UUID id, @RequestBody VenteUseCase.EncaissementRequest request) {
        return venteUseCase.encaisserVente(id, request);
    }

    @PostMapping("/ventes/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public Vente annulerVente(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return venteUseCase.annulerVente(id, request.motif());
    }

    @PostMapping("/ventes/{id}/rembourser")
    @PreAuthorize("hasRole('ADMIN')")
    public Vente rembourserVente(@PathVariable UUID id) {
        return venteUseCase.rembourserVente(id);
    }

    // ---------- COMMANDES CLIENTS ----------
    @GetMapping("/commandes-clients")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CommandeClient> listCommandes() {
        return venteUseCase.listCommandes();
    }

    @PostMapping("/commandes-clients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandeClient> creerCommande(@RequestBody CommandeClient commande) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venteUseCase.creerCommande(commande));
    }

    @GetMapping("/commandes-clients/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public CommandeClient getCommande(@PathVariable UUID id) {
        return venteUseCase.getCommande(id);
    }

    @PostMapping("/commandes-clients/{id}/lignes")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient ajouterLigne(@PathVariable UUID id, @RequestBody LigneCommandeClient ligne) {
        return venteUseCase.ajouterLigneCommande(id, ligne);
    }

    @PostMapping("/commandes-clients/{id}/confirmer")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient confirmerCommande(@PathVariable UUID id) {
        return venteUseCase.confirmerCommande(id);
    }

    @PostMapping("/commandes-clients/{id}/acompte")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient encaisserAcompte(@PathVariable UUID id, @RequestBody VenteUseCase.AcompteRequest request) {
        return venteUseCase.encaisserAcompte(id, request);
    }

    @PostMapping("/commandes-clients/{id}/demarrer-production")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient demarrerProduction(@PathVariable UUID id) {
        return venteUseCase.demarrerProduction(id);
    }

    @PostMapping("/commandes-clients/{id}/pretes")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient marquerPrete(@PathVariable UUID id) {
        return venteUseCase.marquerPrete(id);
    }

    @PostMapping("/commandes-clients/{id}/retirer")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient retirerCommande(@PathVariable UUID id) {
        return venteUseCase.retirerCommande(id);
    }

    @PostMapping("/commandes-clients/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeClient annulerCommande(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return venteUseCase.annulerCommande(id, request.motif());
    }

    public record OuvrirSessionRequest(LocalDate date, BigDecimal fondInitial, UUID ouvertePar) {
    }

    public record CloturerSessionRequest(BigDecimal montantCompte) {
    }

    public record MotifRequest(String motif) {
    }
}

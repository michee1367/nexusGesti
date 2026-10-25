package com.nexusgesti.boulangerie.approvisionnement.adapter.in.web;

import com.nexusgesti.boulangerie.approvisionnement.domain.model.BesoinApprovisionnement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.CommandeAchat;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.FournisseurBoulangerie;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Rapprochement;
import com.nexusgesti.boulangerie.approvisionnement.domain.model.Reception;
import com.nexusgesti.boulangerie.approvisionnement.domain.port.in.ApprovisionnementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ApprovisionnementController {

    private final ApprovisionnementUseCase approvisionnementUseCase;

    public ApprovisionnementController(ApprovisionnementUseCase approvisionnementUseCase) {
        this.approvisionnementUseCase = approvisionnementUseCase;
    }

    // ---------- FOURNISSEURS ----------

    @GetMapping("/fournisseurs")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<FournisseurBoulangerie> listFournisseurs() {
        return approvisionnementUseCase.listFournisseurs();
    }

    @PostMapping("/fournisseurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FournisseurBoulangerie> creerFournisseur(
            @RequestBody FournisseurBoulangerie fournisseur) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(approvisionnementUseCase.creerFournisseur(fournisseur));
    }

    @GetMapping("/fournisseurs/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public FournisseurBoulangerie getFournisseur(@PathVariable UUID id) {
        return approvisionnementUseCase.getFournisseur(id);
    }

    @PutMapping("/fournisseurs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FournisseurBoulangerie modifierFournisseur(@PathVariable UUID id,
                                                      @RequestBody FournisseurBoulangerie fournisseur) {
        return approvisionnementUseCase.modifierFournisseur(id, fournisseur);
    }

    @PostMapping("/fournisseurs/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public FournisseurBoulangerie activerFournisseur(@PathVariable UUID id) {
        return approvisionnementUseCase.changerEtatFournisseur(id, true);
    }

    @PostMapping("/fournisseurs/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    public FournisseurBoulangerie desactiverFournisseur(@PathVariable UUID id) {
        return approvisionnementUseCase.changerEtatFournisseur(id, false);
    }

    @GetMapping("/fournisseurs/{id}/evaluation")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ApprovisionnementUseCase.EvaluationFournisseur evaluerFournisseur(@PathVariable UUID id) {
        return approvisionnementUseCase.evaluerFournisseur(id);
    }

    // ---------- BESOINS ----------

    @PostMapping("/besoins/calcule")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<BesoinApprovisionnement> calculerBesoins(
            @RequestBody List<ApprovisionnementUseCase.SaisieBesoin> saisies) {
        return approvisionnementUseCase.calculerBesoins(saisies);
    }

    @PostMapping("/besoins/sous-seuil")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<BesoinApprovisionnement> listerSousSeuil(
            @RequestBody List<ApprovisionnementUseCase.SaisieBesoin> saisies) {
        return approvisionnementUseCase.listerSousSeuil(saisies);
    }

    // ---------- COMMANDES ----------

    @GetMapping("/commandes")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<CommandeAchat> listCommandes() {
        return approvisionnementUseCase.listCommandes();
    }

    @PostMapping("/commandes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommandeAchat> creerCommande(@RequestBody CommandeAchat commande) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(approvisionnementUseCase.creerCommande(commande));
    }

    @GetMapping("/commandes/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public CommandeAchat getCommande(@PathVariable UUID id) {
        return approvisionnementUseCase.getCommande(id);
    }

    @PostMapping("/commandes/{id}/valider")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeAchat validerCommande(@PathVariable UUID id) {
        return approvisionnementUseCase.validerCommande(id);
    }

    @PostMapping("/commandes/{id}/envoyer")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeAchat envoyerCommande(@PathVariable UUID id) {
        return approvisionnementUseCase.envoyerCommande(id);
    }

    @PostMapping("/commandes/{id}/annuler")
    @PreAuthorize("hasRole('ADMIN')")
    public CommandeAchat annulerCommande(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return approvisionnementUseCase.annulerCommande(id, request.motif());
    }

    @GetMapping("/commandes/{id}/rapprochement")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Rapprochement rapprocherCommande(@PathVariable UUID id) {
        return approvisionnementUseCase.rapprocherCommande(id);
    }

    // ---------- RECEPTIONS ----------

    @GetMapping("/receptions")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Reception> listReceptions() {
        return approvisionnementUseCase.listReceptions();
    }

    @PostMapping("/receptions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Reception> creerReception(@RequestBody Reception reception) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(approvisionnementUseCase.creerReception(reception));
    }

    @GetMapping("/receptions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Reception getReception(@PathVariable UUID id) {
        return approvisionnementUseCase.getReception(id);
    }

    @PostMapping("/receptions/{id}/controle")
    @PreAuthorize("hasRole('ADMIN')")
    public Reception controlerReception(@PathVariable UUID id) {
        return approvisionnementUseCase.controlerReception(id);
    }

    @PostMapping("/receptions/{id}/accepter")
    @PreAuthorize("hasRole('ADMIN')")
    public Reception accepterReception(@PathVariable UUID id) {
        return approvisionnementUseCase.accepterReception(id);
    }

    @PostMapping("/receptions/{id}/refuser")
    @PreAuthorize("hasRole('ADMIN')")
    public Reception refuserReception(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return approvisionnementUseCase.refuserReception(id, request.motif());
    }

    public record MotifRequest(String motif) {
    }
}

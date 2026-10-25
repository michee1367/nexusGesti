package com.nexusgesti.identity.adapter.in.web;

import com.nexusgesti.identity.domain.model.TypeCompte;
import com.nexusgesti.identity.domain.model.Utilisateur;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/utilisateurs")
public class UtilisateurController {

    private final UtilisateurUseCase utilisateurUseCase;

    public UtilisateurController(UtilisateurUseCase utilisateurUseCase) {
        this.utilisateurUseCase = utilisateurUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utilisateur> creer(@RequestBody CreerUtilisateurRequest request) {
        Utilisateur cree = utilisateurUseCase.creer(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurUseCase.get(cree.getIdUtilisateur()));
    }

    @PostMapping("/invitations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utilisateur> inviter(@RequestBody InviterUtilisateurRequest request) {
        Utilisateur cree = utilisateurUseCase.inviter(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurUseCase.get(cree.getIdUtilisateur()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Utilisateur get(@PathVariable UUID id) {
        return utilisateurUseCase.get(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public List<Utilisateur> rechercher(@RequestParam(name = "q", required = false) String query) {
        return utilisateurUseCase.rechercher(query);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur modifier(@PathVariable UUID id, @RequestBody ModifierUtilisateurRequest request) {
        return utilisateurUseCase.modifier(id, request.toCommand());
    }

    @PostMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur activer(@PathVariable UUID id) {
        return utilisateurUseCase.activer(id);
    }

    @PostMapping("/{id}/desactiver")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur desactiver(@PathVariable UUID id, @RequestBody MotifRequest request) {
        return utilisateurUseCase.desactiver(id, request.motif());
    }

    @PostMapping("/{id}/suspendre")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur suspendre(@PathVariable UUID id, @RequestBody SuspensionRequest request) {
        return utilisateurUseCase.suspendre(id, request.jusquau());
    }

    @PostMapping("/{id}/reinitialiser-mot-de-passe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reinitialiserMotDePasse(@PathVariable UUID id,
                                                        @RequestBody MotDePasseRequest request) {
        utilisateurUseCase.reinitialiserMotDePasse(id, request.motDePasse());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur attribuerRole(@PathVariable UUID id, @RequestBody RoleRequest request) {
        return utilisateurUseCase.attribuerRole(id, request.roleCode());
    }

    @DeleteMapping("/{id}/roles/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur retirerRole(@PathVariable UUID id, @PathVariable String code) {
        return utilisateurUseCase.retirerRole(id, code);
    }

    @PutMapping("/{id}/preferences")
    @PreAuthorize("hasRole('ADMIN')")
    public Utilisateur definirPreferences(@PathVariable UUID id, @RequestBody Map<String, String> preferences) {
        return utilisateurUseCase.definirPreferences(id, preferences);
    }

    public record CreerUtilisateurRequest(
            @NotBlank @Email String email,
            String telephone,
            String nom,
            String postNom,
            String prenom,
            String sexe,
            LocalDate dateNaissance,
            String nationalite,
            String motDePasse,
            List<String> roles,
            TypeCompte typeCompte
    ) {
        UtilisateurUseCase.CreerUtilisateurCommand toCommand() {
            return new UtilisateurUseCase.CreerUtilisateurCommand(
                    email, telephone, nom, postNom, prenom, sexe, dateNaissance, nationalite,
                    motDePasse, roles, typeCompte);
        }
    }

    public record InviterUtilisateurRequest(
            @NotBlank @Email String email,
            String nom,
            String prenom,
            List<String> roles
    ) {
        UtilisateurUseCase.InviterUtilisateurCommand toCommand() {
            return new UtilisateurUseCase.InviterUtilisateurCommand(email, nom, prenom, roles);
        }
    }

    public record ModifierUtilisateurRequest(
            String telephone,
            String nom,
            String postNom,
            String prenom,
            String sexe,
            LocalDate dateNaissance,
            String nationalite,
            String photoUrl,
            String languePreferee
    ) {
        UtilisateurUseCase.ModifierUtilisateurCommand toCommand() {
            return new UtilisateurUseCase.ModifierUtilisateurCommand(
                    telephone, nom, postNom, prenom, sexe, dateNaissance, nationalite, photoUrl, languePreferee);
        }
    }

    public record MotifRequest(String motif) {}

    public record SuspensionRequest(LocalDate jusquau) {}

    public record MotDePasseRequest(String motDePasse) {}

    public record RoleRequest(String roleCode) {}
}

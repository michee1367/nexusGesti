package com.nexusgesti.identity.domain.port.in;

import com.nexusgesti.identity.domain.model.TypeCompte;
import com.nexusgesti.identity.domain.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UtilisateurUseCase {

    Utilisateur creer(CreerUtilisateurCommand command);

    Utilisateur inviter(InviterUtilisateurCommand command);

    Utilisateur get(UUID id);

    List<Utilisateur> rechercher(String query);

    Utilisateur modifier(UUID id, ModifierUtilisateurCommand command);

    Utilisateur activer(UUID id);

    Utilisateur desactiver(UUID id, String motif);

    Utilisateur suspendre(UUID id, LocalDate jusquau);

    void reinitialiserMotDePasse(UUID id, String nouveauMotDePasse);

    Utilisateur attribuerRole(UUID id, String roleCode);

    Utilisateur retirerRole(UUID id, String roleCode);

    Utilisateur definirPreferences(UUID id, Map<String, String> preferences);

    record CreerUtilisateurCommand(
            String email,
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
    ) {}

    record InviterUtilisateurCommand(
            String email,
            String nom,
            String prenom,
            List<String> roles
    ) {}

    record ModifierUtilisateurCommand(
            String telephone,
            String nom,
            String postNom,
            String prenom,
            String sexe,
            LocalDate dateNaissance,
            String nationalite,
            String photoUrl,
            String languePreferee
    ) {}
}

package com.nexusgesti.identity.application.service;

import com.nexusgesti.identity.domain.IdentityException;
import com.nexusgesti.identity.domain.model.Preference;
import com.nexusgesti.identity.domain.model.Role;
import com.nexusgesti.identity.domain.model.StatutUtilisateur;
import com.nexusgesti.identity.domain.model.TypeCompte;
import com.nexusgesti.identity.domain.model.Utilisateur;
import com.nexusgesti.identity.domain.port.in.UtilisateurUseCase;
import com.nexusgesti.identity.domain.port.out.IdentityProviderPort;
import com.nexusgesti.identity.domain.port.out.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UtilisateurService implements UtilisateurUseCase {

    private final IdentityProviderPort identityProvider;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(IdentityProviderPort identityProvider,
                              UtilisateurRepository utilisateurRepository) {
        this.identityProvider = identityProvider;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    @Transactional
    public Utilisateur creer(CreerUtilisateurCommand command) {
        if (utilisateurRepository.existsByEmail(command.email())) {
            throw IdentityException.utilisateurDejaExistant(command.email());
        }
        boolean invitable = command.motDePasse() == null || command.motDePasse().isBlank();
        UUID keycloakId = identityProvider.createUser(new IdentityProviderPort.CreateUser(
                command.email(),
                command.email(),
                command.prenom(),
                command.nom(),
                true,
                command.motDePasse(),
                invitable,
                Map.of()
        ));

        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(UUID.randomUUID());
        u.setKeycloakUserId(keycloakId);
        u.setIdentifiant(command.email());
        u.setEmail(command.email());
        u.setTelephone(command.telephone());
        u.setNom(command.nom());
        u.setPostNom(command.postNom());
        u.setPrenom(command.prenom());
        u.setSexe(command.sexe());
        u.setDateNaissance(command.dateNaissance());
        u.setNationalite(command.nationalite());
        u.setStatut(invitable ? StatutUtilisateur.INVITE : StatutUtilisateur.ACTIF);
        u.setTypeCompte(command.typeCompte() == null ? TypeCompte.KEYCLOAK : command.typeCompte());
        u.setLanguePreferee("fr");

        if (command.roles() != null) {
            for (String role : command.roles()) {
                identityProvider.assignRealmRole(keycloakId, role);
            }
        }
        Utilisateur saved = utilisateurRepository.save(u);
        return enrich(saved);
    }

    @Override
    @Transactional
    public Utilisateur inviter(InviterUtilisateurCommand command) {
        if (utilisateurRepository.existsByEmail(command.email())) {
            throw IdentityException.utilisateurDejaExistant(command.email());
        }
        UUID keycloakId = identityProvider.createUser(new IdentityProviderPort.CreateUser(
                command.email(),
                command.email(),
                command.prenom(),
                command.nom(),
                true,
                null,
                true,
                Map.of()
        ));

        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(UUID.randomUUID());
        u.setKeycloakUserId(keycloakId);
        u.setIdentifiant(command.email());
        u.setEmail(command.email());
        u.setNom(command.nom());
        u.setPrenom(command.prenom());
        u.setStatut(StatutUtilisateur.INVITE);
        u.setTypeCompte(TypeCompte.KEYCLOAK);
        u.setLanguePreferee("fr");

        if (command.roles() != null) {
            for (String role : command.roles()) {
                identityProvider.assignRealmRole(keycloakId, role);
            }
        }
        Utilisateur saved = utilisateurRepository.save(u);
        return enrich(saved);
    }

    @Override
    public Utilisateur get(UUID id) {
        return enrich(require(id));
    }

    @Override
    public List<Utilisateur> rechercher(String query) {
        List<Utilisateur> proxyResults = utilisateurRepository.search(query);
        if (!proxyResults.isEmpty()) {
            return proxyResults.stream().map(this::enrich).toList();
        }
        return identityProvider.searchUserIds(query).stream()
                .map(kcId -> utilisateurRepository.findByKeycloakUserId(UUID.fromString(kcId)))
                .flatMap(java.util.Optional::stream)
                .map(this::enrich)
                .toList();
    }

    @Override
    @Transactional
    public Utilisateur modifier(UUID id, ModifierUtilisateurCommand command) {
        Utilisateur u = require(id);
        identityProvider.updateUser(u.getKeycloakUserId(), new IdentityProviderPort.UpdateUser(
                null,
                command.prenom(),
                command.nom(),
                Map.of()
        ));
        u.setTelephone(command.telephone());
        u.setNom(command.nom());
        u.setPostNom(command.postNom());
        u.setPrenom(command.prenom());
        u.setSexe(command.sexe());
        u.setDateNaissance(command.dateNaissance());
        u.setNationalite(command.nationalite());
        u.setPhotoUrl(command.photoUrl());
        if (command.languePreferee() != null) {
            u.setLanguePreferee(command.languePreferee());
        }
        return enrich(utilisateurRepository.save(u));
    }

    @Override
    @Transactional
    public Utilisateur activer(UUID id) {
        Utilisateur u = require(id);
        u.activer();
        identityProvider.setEnabled(u.getKeycloakUserId(), true);
        identityProvider.requirePasswordChange(u.getKeycloakUserId(), false);
        return enrich(utilisateurRepository.save(u));
    }

    @Override
    @Transactional
    public Utilisateur desactiver(UUID id, String motif) {
        Utilisateur u = require(id);
        u.desactiver(motif);
        identityProvider.setEnabled(u.getKeycloakUserId(), false);
        Utilisateur saved = utilisateurRepository.save(u);
        utilisateurRepository.saveMotifDesactivation(id, u.getMotifsDesactivation().getLast());
        return enrich(saved);
    }

    @Override
    @Transactional
    public Utilisateur suspendre(UUID id, LocalDate jusquau) {
        Utilisateur u = require(id);
        u.suspendre(jusquau);
        identityProvider.setEnabled(u.getKeycloakUserId(), false);
        return enrich(utilisateurRepository.save(u));
    }

    @Override
    public void reinitialiserMotDePasse(UUID id, String nouveauMotDePasse) {
        Utilisateur u = require(id);
        identityProvider.resetPassword(u.getKeycloakUserId(), nouveauMotDePasse, true);
        identityProvider.requirePasswordChange(u.getKeycloakUserId(), true);
    }

    @Override
    @Transactional
    public Utilisateur attribuerRole(UUID id, String roleCode) {
        Utilisateur u = require(id);
        identityProvider.assignRealmRole(u.getKeycloakUserId(), roleCode);
        return enrich(u);
    }

    @Override
    @Transactional
    public Utilisateur retirerRole(UUID id, String roleCode) {
        Utilisateur u = require(id);
        identityProvider.removeRealmRole(u.getKeycloakUserId(), roleCode);
        return enrich(u);
    }

    @Override
    public Utilisateur definirPreferences(UUID id, Map<String, String> preferences) {
        Utilisateur u = require(id);
        identityProvider.setAttributes(u.getKeycloakUserId(), preferences);
        return enrich(u);
    }

    private Utilisateur require(UUID id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> IdentityException.notFound("Utilisateur", id));
    }

    private Utilisateur enrich(Utilisateur u) {
        UUID keycloakId = u.getKeycloakUserId();
        if (keycloakId == null) {
            return u;
        }
        List<Role> roles = identityProvider.listUserRealmRoles(keycloakId).stream()
                .map(code -> {
                    Role role = new Role();
                    role.setCode(code);
                    role.setLibelle(code);
                    return role;
                }).toList();
        u.getRoles().clear();
        u.getRoles().addAll(roles);

        u.getPreferences().clear();
        identityProvider.getAttributes(keycloakId).forEach((k, v) -> u.getPreferences().add(new Preference(k, v)));

        java.time.Instant last = identityProvider.lastLogin(keycloakId);
        if (last != null) {
            u.setDerniereConnexion(last);
        }
        return u;
    }
}

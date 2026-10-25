package com.nexusgesti.identity.adapter.out.persistence;

import com.nexusgesti.common.tenancy.TenantContext;
import com.nexusgesti.identity.adapter.out.persistence.entity.MotifDesactivationEntity;
import com.nexusgesti.identity.adapter.out.persistence.entity.UtilisateurEntity;
import com.nexusgesti.identity.domain.model.Adresse;
import com.nexusgesti.identity.domain.model.MotifDesactivation;
import com.nexusgesti.identity.domain.model.StatutUtilisateur;
import com.nexusgesti.identity.domain.model.TypeCompte;
import com.nexusgesti.identity.domain.model.Utilisateur;
import com.nexusgesti.identity.domain.port.out.UtilisateurRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UtilisateurPersistenceAdapter implements UtilisateurRepository {

    private final UtilisateurJpaRepository utilisateurJpaRepository;
    private final MotifDesactivationJpaRepository motifJpaRepository;

    public UtilisateurPersistenceAdapter(UtilisateurJpaRepository utilisateurJpaRepository,
                                         MotifDesactivationJpaRepository motifJpaRepository) {
        this.utilisateurJpaRepository = utilisateurJpaRepository;
        this.motifJpaRepository = motifJpaRepository;
    }

    @Override
    public Utilisateur save(Utilisateur utilisateur) {
        UtilisateurEntity entity = toEntity(utilisateur);
        entity.setTenantId(TenantContext.require().asUuid());
        return toDomain(utilisateurJpaRepository.save(entity));
    }

    @Override
    public Optional<Utilisateur> findById(UUID id) {
        return utilisateurJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Utilisateur> findByKeycloakUserId(UUID keycloakUserId) {
        return utilisateurJpaRepository.findByKeycloakUserId(keycloakUserId).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return utilisateurJpaRepository.existsByEmail(email);
    }

    @Override
    public List<Utilisateur> search(String query) {
        String q = query == null ? "" : query;
        return utilisateurJpaRepository
                .findByEmailContainingIgnoreCaseOrNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(q, q, q)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public MotifDesactivation saveMotifDesactivation(UUID utilisateurId, MotifDesactivation motif) {
        MotifDesactivationEntity entity = new MotifDesactivationEntity();
        entity.setId(motif.getId() == null ? UUID.randomUUID() : motif.getId());
        entity.setUtilisateurId(utilisateurId);
        entity.setDate(motif.getDate() == null ? java.time.Instant.now() : motif.getDate());
        entity.setMotif(motif.getMotif());
        entity.setDecideur(motif.getDecideur());
        entity.setTenantId(TenantContext.require().asUuid());
        MotifDesactivationEntity saved = motifJpaRepository.save(entity);
        return new MotifDesactivation(saved.getMotif(), saved.getDecideur());
    }

    @Override
    public List<MotifDesactivation> findMotifsDesactivation(UUID utilisateurId) {
        return motifJpaRepository.findByUtilisateurIdOrderByDateDesc(utilisateurId).stream()
                .map(e -> {
                    MotifDesactivation m = new MotifDesactivation();
                    m.setId(e.getId());
                    m.setDate(e.getDate());
                    m.setMotif(e.getMotif());
                    m.setDecideur(e.getDecideur());
                    return m;
                }).toList();
    }

    private UtilisateurEntity toEntity(Utilisateur u) {
        UtilisateurEntity entity = new UtilisateurEntity();
        entity.setId(u.getIdUtilisateur());
        entity.setKeycloakUserId(u.getKeycloakUserId());
        entity.setEmail(u.getEmail());
        entity.setTelephone(u.getTelephone());
        entity.setNom(u.getNom());
        entity.setPostNom(u.getPostNom());
        entity.setPrenom(u.getPrenom());
        entity.setSexe(u.getSexe());
        entity.setDateNaissance(u.getDateNaissance());
        entity.setNationalite(u.getNationalite());
        entity.setPhotoUrl(u.getPhotoUrl());
        entity.setStatut(u.getStatut() == null ? null : u.getStatut().name());
        entity.setTypeCompte(u.getTypeCompte() == null ? null : u.getTypeCompte().name());
        entity.setLanguePreferee(u.getLanguePreferee());
        entity.setDerniereConnexion(u.getDerniereConnexion());
        entity.setDelombok(u.isDelombok());
        Adresse a = u.getAdresse();
        if (a != null) {
            entity.setAdrRue(a.getRue());
            entity.setAdrQuartier(a.getQuartier());
            entity.setAdrCommune(a.getCommune());
            entity.setAdrVille(a.getVille());
            entity.setAdrProvince(a.getProvince());
            entity.setAdrPays(a.getPays());
        }
        return entity;
    }

    private Utilisateur toDomain(UtilisateurEntity entity) {
        Utilisateur u = new Utilisateur();
        u.setIdUtilisateur(entity.getId());
        u.setKeycloakUserId(entity.getKeycloakUserId());
        u.setEmail(entity.getEmail());
        u.setIdentifiant(entity.getEmail());
        u.setTelephone(entity.getTelephone());
        u.setNom(entity.getNom());
        u.setPostNom(entity.getPostNom());
        u.setPrenom(entity.getPrenom());
        u.setSexe(entity.getSexe());
        u.setDateNaissance(entity.getDateNaissance());
        u.setNationalite(entity.getNationalite());
        u.setPhotoUrl(entity.getPhotoUrl());
        u.setStatut(entity.getStatut() == null ? null : StatutUtilisateur.valueOf(entity.getStatut()));
        u.setTypeCompte(entity.getTypeCompte() == null ? null : TypeCompte.valueOf(entity.getTypeCompte()));
        u.setLanguePreferee(entity.getLanguePreferee());
        u.setDerniereConnexion(entity.getDerniereConnexion());
        u.setDelombok(entity.isDelombok());
        Adresse a = new Adresse();
        a.setRue(entity.getAdrRue());
        a.setQuartier(entity.getAdrQuartier());
        a.setCommune(entity.getAdrCommune());
        a.setVille(entity.getAdrVille());
        a.setProvince(entity.getAdrProvince());
        a.setPays(entity.getAdrPays());
        u.setAdresse(a);
        return u;
    }
}

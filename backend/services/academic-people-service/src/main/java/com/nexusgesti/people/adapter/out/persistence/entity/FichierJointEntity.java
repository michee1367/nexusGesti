package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fichier_joint", schema = "academic_people")
public class FichierJointEntity {

    @Id
    private UUID id;

    @Column(name = "dossier_id", nullable = false)
    private UUID dossierId;

    @Column(nullable = false, length = 40)
    private String categorie;

    @Column(nullable = false, length = 160)
    private String nom;

    @Column(name = "type_mime", length = 120)
    private String typeMime;

    private Long taille;

    @Column(length = 500)
    private String uri;

    @Column(name = "date_depot", nullable = false)
    private Instant dateDepot;

    @Column(name = "depose_par")
    private UUID deposePar;

    @Column(name = "document_officiel", nullable = false)
    private boolean documentOfficiel;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDossierId() { return dossierId; }
    public void setDossierId(UUID dossierId) { this.dossierId = dossierId; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTypeMime() { return typeMime; }
    public void setTypeMime(String typeMime) { this.typeMime = typeMime; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public Instant getDateDepot() { return dateDepot; }
    public void setDateDepot(Instant dateDepot) { this.dateDepot = dateDepot; }
    public UUID getDeposePar() { return deposePar; }
    public void setDeposePar(UUID deposePar) { this.deposePar = deposePar; }
    public boolean isDocumentOfficiel() { return documentOfficiel; }
    public void setDocumentOfficiel(boolean documentOfficiel) { this.documentOfficiel = documentOfficiel; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

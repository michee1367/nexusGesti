package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "parametre", schema = "tenant")
public class ParametreEntity {

    @Id
    private UUID id;

    @Column(name = "etablissement_id", nullable = false)
    private UUID etablissementId;

    @Column(nullable = false, length = 80)
    private String cle;

    @Column(length = 500)
    private String valeur;

    @Column(name = "type_valeur", nullable = false, length = 20)
    private String typeValeur;

    @Column(length = 60)
    private String categorie;

    @Column(length = 255)
    private String description;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEtablissementId() { return etablissementId; }
    public void setEtablissementId(UUID etablissementId) { this.etablissementId = etablissementId; }
    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public String getTypeValeur() { return typeValeur; }
    public void setTypeValeur(String typeValeur) { this.typeValeur = typeValeur; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
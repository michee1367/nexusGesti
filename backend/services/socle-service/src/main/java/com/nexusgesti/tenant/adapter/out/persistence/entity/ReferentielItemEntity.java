package com.nexusgesti.tenant.adapter.out.persistence.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "referentiel_item", schema = "socle")
public class ReferentielItemEntity {

    @Id
    private UUID id;

    @Column(name = "referentiel_id", nullable = false)
    private UUID referentielId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(length = 255)
    private String valeur;

    @JdbcTypeCode(SqlTypes.JSON)
    private String metaDonnees;

    @Column(nullable = false)
    private int ordre;

    @Column(nullable = false)
    private boolean actif;

    @Column(name = "comptabilise_presence")
    private Boolean comptabilisePresence;

    @Column(name = "bloque_saisie")
    private Boolean bloqueSaisie;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReferentielId() { return referentielId; }
    public void setReferentielId(UUID referentielId) { this.referentielId = referentielId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public String getMetaDonnees() { return metaDonnees; }
    public void setMetaDonnees(String metaDonnees) { this.metaDonnees = metaDonnees; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Boolean getComptabilisePresence() { return comptabilisePresence; }
    public void setComptabilisePresence(Boolean comptabilisePresence) { this.comptabilisePresence = comptabilisePresence; }
    public Boolean getBloqueSaisie() { return bloqueSaisie; }
    public void setBloqueSaisie(Boolean bloqueSaisie) { this.bloqueSaisie = bloqueSaisie; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
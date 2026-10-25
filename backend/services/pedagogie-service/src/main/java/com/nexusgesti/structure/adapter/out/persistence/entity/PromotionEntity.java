package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "promotion", schema = "pedagogie")
public class PromotionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "programme_id")
    private UUID programmeId;

    @Column(name = "semestre_id")
    private UUID semestreId;

    @Column(name = "annee_id")
    private UUID anneeId;

    private Integer capacite;

    @Column(name = "effectif_reel", nullable = false)
    private int effectifReel;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public int getEffectifReel() { return effectifReel; }
    public void setEffectifReel(int effectifReel) { this.effectifReel = effectifReel; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

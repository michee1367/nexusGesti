package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ue", schema = "pedagogie")
public class UEEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(name = "semestre_id")
    private UUID semestreId;

    private BigDecimal credits;

    private BigDecimal coefficient;

    @Column(name = "volume_horaire")
    private BigDecimal volumeHoraire;

    @Column(nullable = false)
    private boolean obligatoire;

    @Column(name = "niveau_acalcul", length = 60)
    private String niveauAcalcul;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public BigDecimal getCredits() { return credits; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public BigDecimal getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(BigDecimal volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
    public String getNiveauAcalcul() { return niveauAcalcul; }
    public void setNiveauAcalcul(String niveauAcalcul) { this.niveauAcalcul = niveauAcalcul; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

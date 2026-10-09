package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "niveau", schema = "academic_structure")
public class NiveauEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int rang;

    @Column(nullable = false, length = 20)
    private String cycle;

    @Column(nullable = false)
    private int ordre;

    @Column(name = "niveau_superieur_id")
    private UUID niveauSuperieurId;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public String getCycle() { return cycle; }
    public void setCycle(String cycle) { this.cycle = cycle; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public UUID getNiveauSuperieurId() { return niveauSuperieurId; }
    public void setNiveauSuperieurId(UUID niveauSuperieurId) { this.niveauSuperieurId = niveauSuperieurId; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

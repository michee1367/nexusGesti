package com.nexusgesti.structure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "semestre", schema = "academic_structure")
public class SemestreEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 120)
    private String libelle;

    @Column(nullable = false)
    private int rang;

    @Column(name = "periode_id")
    private UUID periodeId;

    @Column(name = "total_credits_attendu")
    private BigDecimal totalCreditsAttendu;

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
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public BigDecimal getTotalCreditsAttendu() { return totalCreditsAttendu; }
    public void setTotalCreditsAttendu(BigDecimal totalCreditsAttendu) { this.totalCreditsAttendu = totalCreditsAttendu; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

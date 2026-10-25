package com.nexusgesti.boulangerie.vente.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session_caisse", schema = "boulangerie")
public class SessionCaisseEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "fond_initial", nullable = false)
    private BigDecimal fondInitial;

    @Column(name = "total_encaisse", nullable = false)
    private BigDecimal totalEncaisse;

    private BigDecimal ecart;

    @Column(name = "ouverte_par")
    private UUID ouvertePar;

    @Column(nullable = false)
    private Boolean cloturee;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public BigDecimal getFondInitial() { return fondInitial; }
    public void setFondInitial(BigDecimal fondInitial) { this.fondInitial = fondInitial; }
    public BigDecimal getTotalEncaisse() { return totalEncaisse; }
    public void setTotalEncaisse(BigDecimal totalEncaisse) { this.totalEncaisse = totalEncaisse; }
    public BigDecimal getEcart() { return ecart; }
    public void setEcart(BigDecimal ecart) { this.ecart = ecart; }
    public UUID getOuvertePar() { return ouvertePar; }
    public void setOuvertePar(UUID ouvertePar) { this.ouvertePar = ouvertePar; }
    public Boolean getCloturee() { return cloturee; }
    public void setCloturee(Boolean cloturee) { this.cloturee = cloturee; }
    public LocalDateTime getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDateTime dateCloture) { this.dateCloture = dateCloture; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

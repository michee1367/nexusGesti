package com.nexusgesti.people.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "piece_identite_enseignant", schema = "pedagogie")
public class PieceIdentiteEnseignantEntity {

    @Id
    private UUID id;

    @Column(name = "enseignant_id", nullable = false)
    private UUID enseignantId;

    @Column(nullable = false, length = 60)
    private String type;

    @Column(nullable = false, length = 80)
    private String numero;

    @Column(name = "date_delivrance")
    private LocalDate dateDelivrance;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Column(length = 120)
    private String pays;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }
    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

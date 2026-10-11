package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "signature", schema = "academic_exam")
public class SignatureEntity {

    @Id
    private UUID id;

    @Column(name = "proces_verbal_id", nullable = false)
    private UUID procesVerbalId;

    private UUID signataire;

    private String role;

    @Column(name = "date_signature")
    private Instant dateSignature;

    private String empreinte;

    @Column(nullable = false)
    private boolean validee;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProcesVerbalId() { return procesVerbalId; }
    public void setProcesVerbalId(UUID procesVerbalId) { this.procesVerbalId = procesVerbalId; }
    public UUID getSignataire() { return signataire; }
    public void setSignataire(UUID signataire) { this.signataire = signataire; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Instant getDateSignature() { return dateSignature; }
    public void setDateSignature(Instant dateSignature) { this.dateSignature = dateSignature; }
    public String getEmpreinte() { return empreinte; }
    public void setEmpreinte(String empreinte) { this.empreinte = empreinte; }
    public boolean isValidee() { return validee; }
    public void setValidee(boolean validee) { this.validee = validee; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
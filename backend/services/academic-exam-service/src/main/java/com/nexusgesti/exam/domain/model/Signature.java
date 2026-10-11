package com.nexusgesti.exam.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Signature {

    private UUID id;
    private UUID procesVerbalId;
    private UUID signataire;
    private String role;
    private Instant dateSignature;
    private String empreinte;
    private boolean validee;

    public boolean verifier(String document) {
        if (document == null || empreinte == null) {
            return false;
        }
        return empreinte.equals(Integer.toHexString((document + "|" + signataire + "|" + role).hashCode()));
    }

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
}

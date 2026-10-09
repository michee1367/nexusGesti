package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class PieceIdentiteEnseignant {

    private UUID id;
    private UUID enseignantId;
    private String type;
    private String numero;
    private LocalDate dateDelivrance;
    private LocalDate dateExpiration;
    private String pays;

    public boolean estExpiree(LocalDate date) {
        return dateExpiration != null && dateExpiration.isBefore(date);
    }

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
}

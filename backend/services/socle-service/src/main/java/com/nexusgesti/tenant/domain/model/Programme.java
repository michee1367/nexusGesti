package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Programme {

    private UUID idProgramme;
    private UUID filiereId;
    private String code;
    private String libelle;
    private List<String> parcours = new ArrayList<>();
    private BigDecimal totalCredits;
    private boolean active;

    public UUID getIdProgramme() { return idProgramme; }
    public void setIdProgramme(UUID idProgramme) { this.idProgramme = idProgramme; }
    public UUID getFiliereId() { return filiereId; }
    public void setFiliereId(UUID filiereId) { this.filiereId = filiereId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public List<String> getParcours() { return parcours; }
    public void setParcours(List<String> parcours) { this.parcours = parcours == null ? new ArrayList<>() : parcours; }
    public BigDecimal getTotalCredits() { return totalCredits; }
    public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
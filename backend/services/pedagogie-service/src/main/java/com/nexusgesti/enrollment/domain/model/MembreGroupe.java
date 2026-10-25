package com.nexusgesti.enrollment.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class MembreGroupe {

    private UUID idMembre;
    private UUID groupeId;
    private UUID eleveId;
    private LocalDate dateAffectation;
    private UUID affectePar;
    private Integer rang;

    public UUID getIdMembre() { return idMembre; }
    public void setIdMembre(UUID idMembre) { this.idMembre = idMembre; }
    public UUID getGroupeId() { return groupeId; }
    public void setGroupeId(UUID groupeId) { this.groupeId = groupeId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public LocalDate getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(LocalDate dateAffectation) { this.dateAffectation = dateAffectation; }
    public UUID getAffectePar() { return affectePar; }
    public void setAffectePar(UUID affectePar) { this.affectePar = affectePar; }
    public Integer getRang() { return rang; }
    public void setRang(Integer rang) { this.rang = rang; }
}

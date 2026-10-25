package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class SanctionDisciplinaire {

    private UUID id;
    private UUID eleveId;
    private LocalDate dateSanction;
    private TypeSanction type;
    private String motif;
    private Integer dureeJours;
    private LocalDate dateFin;
    private UUID decidePar;
    private String instance;

    public boolean estActive(LocalDate date) {
        if (dateFin == null) {
            return !date.isBefore(dateSanction);
        }
        return !date.isBefore(dateSanction) && !date.isAfter(dateFin);
    }

    public Integer getPeriodeRestant(LocalDate date) {
        if (dateFin == null || dateFin.isBefore(date)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(date, dateFin);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public LocalDate getDateSanction() { return dateSanction; }
    public void setDateSanction(LocalDate dateSanction) { this.dateSanction = dateSanction; }
    public TypeSanction getType() { return type; }
    public void setType(TypeSanction type) { this.type = type; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public Integer getDureeJours() { return dureeJours; }
    public void setDureeJours(Integer dureeJours) { this.dureeJours = dureeJours; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public UUID getDecidePar() { return decidePar; }
    public void setDecidePar(UUID decidePar) { this.decidePar = decidePar; }
    public String getInstance() { return instance; }
    public void setInstance(String instance) { this.instance = instance; }
}

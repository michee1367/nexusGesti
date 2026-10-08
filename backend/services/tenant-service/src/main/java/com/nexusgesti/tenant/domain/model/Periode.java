package com.nexusgesti.tenant.domain.model;

import com.nexusgesti.tenant.domain.TenantException;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class Periode {

    private UUID idPeriode;
    private UUID anneeId;
    private String code;
    private String libelle;
    private int rang;
    private Date dateDebut;
    private Date dateFin;
    private TypePeriode type;
    private boolean cloturee;
    private Date dateCloture;

    public Periode ouvrir() {
        this.cloturee = false;
        this.dateCloture = null;
        return this;
    }

    public Periode cloturer() {
        if (cloturee) {
            throw TenantException.transitionInvalide("CLOTUREE", "CLOTURER");
        }
        this.cloturee = true;
        this.dateCloture = new Date();
        return this;
    }

    public boolean estCourante(Date date) {
        if (cloturee || date == null) {
            return false;
        }
        return !date.before(dateDebut) && !date.after(dateFin);
    }

    public long getDureeJours() {
        if (dateDebut == null || dateFin == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dateDebut.toInstant(), dateFin.toInstant());
    }

    public UUID getIdPeriode() { return idPeriode; }
    public void setIdPeriode(UUID idPeriode) { this.idPeriode = idPeriode; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    public TypePeriode getType() { return type; }
    public void setType(TypePeriode type) { this.type = type; }
    public boolean isCloturee() { return cloturee; }
    public void setCloturee(boolean cloturee) { this.cloturee = cloturee; }
    public Date getDateCloture() { return dateCloture; }
    public void setDateCloture(Date dateCloture) { this.dateCloture = dateCloture; }
}
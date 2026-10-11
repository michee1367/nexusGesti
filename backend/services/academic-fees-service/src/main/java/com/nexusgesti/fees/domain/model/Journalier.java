package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Journalier {

    private UUID id;
    private LocalDate date;
    private UUID site;
    private String statut = "OUVERT";
    private BigDecimal fond = BigDecimal.ZERO;

    public boolean estOuvert() {
        return "OUVERT".equalsIgnoreCase(statut);
    }

    public Journalier cloturer() {
        if (!estOuvert()) {
            throw FeesException.transitionInvalide(statut, "CLOTURE");
        }
        this.statut = "CLOTURE";
        return this;
    }

    public void crediter(BigDecimal montant) {
        if (!estOuvert()) {
            throw FeesException.etatInvalide("Journalier clos : encaissement impossible");
        }
        if (montant != null && montant.signum() > 0) {
            this.fond = this.fond.add(montant);
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public UUID getSite() { return site; }
    public void setSite(UUID site) { this.site = site; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public BigDecimal getFond() { return fond; }
    public void setFond(BigDecimal fond) { this.fond = fond; }
}
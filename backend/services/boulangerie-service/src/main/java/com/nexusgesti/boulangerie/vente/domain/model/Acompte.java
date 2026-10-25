package com.nexusgesti.boulangerie.vente.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Acompte {

    private UUID id;
    private BigDecimal montant;
    private LocalDate date;
    private ModePaiementCaisse mode;
    private String reference;

    public Acompte() {
    }

    public Acompte(BigDecimal montant, ModePaiementCaisse mode, String reference) {
        this.id = UUID.randomUUID();
        this.montant = montant;
        this.date = LocalDate.now();
        this.mode = mode;
        this.reference = reference;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ModePaiementCaisse getMode() {
        return mode;
    }

    public void setMode(ModePaiementCaisse mode) {
        this.mode = mode;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

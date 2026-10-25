package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Remboursement {

    private UUID id;
    private UUID paiementId;
    private BigDecimal montant = BigDecimal.ZERO;
    private String motif;
    private LocalDate date;
    private MoyenPaiement moyen;
    private String referenceTransaction;

    public boolean estEffectue() {
        return date != null;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public MoyenPaiement getMoyen() { return moyen; }
    public void setMoyen(MoyenPaiement moyen) { this.moyen = moyen; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
}

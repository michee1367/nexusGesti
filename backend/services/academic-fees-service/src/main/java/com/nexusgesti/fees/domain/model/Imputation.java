package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Imputation {

    private UUID paiementId;
    private UUID echeanceId;
    private BigDecimal montant = BigDecimal.ZERO;
    private LocalDate dateImputation;
    private Integer ordre;

    public UUID getPaiementId() { return paiementId; }
    public void setPaiementId(UUID paiementId) { this.paiementId = paiementId; }
    public UUID getEcheanceId() { return echeanceId; }
    public void setEcheanceId(UUID echeanceId) { this.echeanceId = echeanceId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDate getDateImputation() { return dateImputation; }
    public void setDateImputation(LocalDate dateImputation) { this.dateImputation = dateImputation; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}

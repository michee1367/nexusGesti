package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public class PayementMobileMoney {

    private String operateur;
    private String numeroTelephone;
    private String referenceTransaction;
    private Instant dateTransaction;
    private String statutTransaction;
    private BigDecimal frais = BigDecimal.ZERO;
    private BigDecimal montantRecu = BigDecimal.ZERO;

    public boolean estReceptionne() {
        return "RECU".equalsIgnoreCase(statutTransaction)
                || "SUCCESS".equalsIgnoreCase(statutTransaction)
                || "REUSSI".equalsIgnoreCase(statutTransaction);
    }

    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }
    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }
    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) { this.referenceTransaction = referenceTransaction; }
    public Instant getDateTransaction() { return dateTransaction; }
    public void setDateTransaction(Instant dateTransaction) { this.dateTransaction = dateTransaction; }
    public String getStatutTransaction() { return statutTransaction; }
    public void setStatutTransaction(String statutTransaction) { this.statutTransaction = statutTransaction; }
    public BigDecimal getFrais() { return frais; }
    public void setFrais(BigDecimal frais) { this.frais = frais; }
    public BigDecimal getMontantRecu() { return montantRecu; }
    public void setMontantRecu(BigDecimal montantRecu) { this.montantRecu = montantRecu; }
}

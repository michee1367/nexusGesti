package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PayementVirement {

    private String banque;
    private String numeroCompte;
    private String referenceVirement;
    private LocalDate dateValeur;
    private BigDecimal commission = BigDecimal.ZERO;

    public String getBanque() { return banque; }
    public void setBanque(String banque) { this.banque = banque; }
    public String getNumeroCompte() { return numeroCompte; }
    public void setNumeroCompte(String numeroCompte) { this.numeroCompte = numeroCompte; }
    public String getReferenceVirement() { return referenceVirement; }
    public void setReferenceVirement(String referenceVirement) { this.referenceVirement = referenceVirement; }
    public LocalDate getDateValeur() { return dateValeur; }
    public void setDateValeur(LocalDate dateValeur) { this.dateValeur = dateValeur; }
    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
}

package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;

public class PrincipeEquivalence {

    private TypePrincipe type;
    private String cible;
    private BigDecimal ponderation;

    public TypePrincipe getType() { return type; }
    public void setType(TypePrincipe type) { this.type = type; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
    public BigDecimal getPonderation() { return ponderation; }
    public void setPonderation(BigDecimal ponderation) { this.ponderation = ponderation; }
}
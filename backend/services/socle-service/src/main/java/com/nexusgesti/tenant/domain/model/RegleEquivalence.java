package com.nexusgesti.tenant.domain.model;

public class RegleEquivalence {

    private String strategie;
    private Integer nombreMaxIgnorees;
    private java.math.BigDecimal moyenneMinimaleIgnoree;
    private java.util.List<PrincipeEquivalence> principes = new java.util.ArrayList<>();

    public String getStrategie() { return strategie; }
    public void setStrategie(String strategie) { this.strategie = strategie; }
    public Integer getNombreMaxIgnorees() { return nombreMaxIgnorees; }
    public void setNombreMaxIgnorees(Integer nombreMaxIgnorees) { this.nombreMaxIgnorees = nombreMaxIgnorees; }
    public java.math.BigDecimal getMoyenneMinimaleIgnoree() { return moyenneMinimaleIgnoree; }
    public void setMoyenneMinimaleIgnoree(java.math.BigDecimal moyenneMinimaleIgnoree) { this.moyenneMinimaleIgnoree = moyenneMinimaleIgnoree; }
    public java.util.List<PrincipeEquivalence> getPrincipes() { return principes; }
    public void setPrincipes(java.util.List<PrincipeEquivalence> principes) { this.principes = principes == null ? new java.util.ArrayList<>() : principes; }
}
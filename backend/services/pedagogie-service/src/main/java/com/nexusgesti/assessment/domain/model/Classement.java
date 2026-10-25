package com.nexusgesti.assessment.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Classement {

    private int rang;
    private UUID eleveId;
    private BigDecimal moyenne;

    public Classement() {
    }

    public Classement(int rang, UUID eleveId, BigDecimal moyenne) {
        this.rang = rang;
        this.eleveId = eleveId;
        this.moyenne = moyenne;
    }

    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public BigDecimal getMoyenne() { return moyenne; }
    public void setMoyenne(BigDecimal moyenne) { this.moyenne = moyenne; }
}

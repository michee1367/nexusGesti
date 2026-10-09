package com.nexusgesti.structure.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class UE {

    private UUID idUE;
    private String code;
    private String libelle;
    private UUID semestreId;
    private BigDecimal credits;
    private BigDecimal coefficient;
    private BigDecimal volumeHoraire;
    private boolean obligatoire = true;
    private String niveauAcalcul;

    public BigDecimal getCredits() {
        return credits == null ? BigDecimal.ZERO : credits;
    }

    public UUID getIdUE() { return idUE; }
    public void setIdUE(UUID idUE) { this.idUE = idUE; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public void setCredits(BigDecimal credits) { this.credits = credits; }
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    public BigDecimal getVolumeHoraire() { return volumeHoraire; }
    public void setVolumeHoraire(BigDecimal volumeHoraire) { this.volumeHoraire = volumeHoraire; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
    public String getNiveauAcalcul() { return niveauAcalcul; }
    public void setNiveauAcalcul(String niveauAcalcul) { this.niveauAcalcul = niveauAcalcul; }
}

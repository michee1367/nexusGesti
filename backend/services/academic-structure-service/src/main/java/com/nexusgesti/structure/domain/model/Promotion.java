package com.nexusgesti.structure.domain.model;

import java.util.UUID;

public class Promotion {

    private UUID idPromotion;
    private String code;
    private String libelle;
    private UUID programmeId;
    private UUID semestreId;
    private UUID anneeId;
    private Integer capacite;
    private int effectifReel;
    private StatutClasse statut;

    public UUID getIdPromotion() { return idPromotion; }
    public void setIdPromotion(UUID idPromotion) { this.idPromotion = idPromotion; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public UUID getSemestreId() { return semestreId; }
    public void setSemestreId(UUID semestreId) { this.semestreId = semestreId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public int getEffectifReel() { return effectifReel; }
    public void setEffectifReel(int effectifReel) { this.effectifReel = effectifReel; }
    public StatutClasse getStatut() { return statut; }
    public void setStatut(StatutClasse statut) { this.statut = statut; }
}

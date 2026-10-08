package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Mention {

    private UUID idMention;
    private UUID grilleId;
    private String code;
    private String libelle;
    private String symbole;
    private BigDecimal seuilMinimum;
    private BigDecimal seuilMaximum;
    private String couleur;
    private String appreciationAssociee;
    private int ordre;

    public boolean contient(BigDecimal moyenne) {
        if (moyenne == null) {
            return false;
        }
        if (seuilMinimum != null && moyenne.compareTo(seuilMinimum) < 0) {
            return false;
        }
        return seuilMaximum == null || moyenne.compareTo(seuilMaximum) <= 0;
    }

    public UUID getIdMention() { return idMention; }
    public void setIdMention(UUID idMention) { this.idMention = idMention; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getSymbole() { return symbole; }
    public void setSymbole(String symbole) { this.symbole = symbole; }
    public BigDecimal getSeuilMinimum() { return seuilMinimum; }
    public void setSeuilMinimum(BigDecimal seuilMinimum) { this.seuilMinimum = seuilMinimum; }
    public BigDecimal getSeuilMaximum() { return seuilMaximum; }
    public void setSeuilMaximum(BigDecimal seuilMaximum) { this.seuilMaximum = seuilMaximum; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public String getAppreciationAssociee() { return appreciationAssociee; }
    public void setAppreciationAssociee(String appreciationAssociee) { this.appreciationAssociee = appreciationAssociee; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
}
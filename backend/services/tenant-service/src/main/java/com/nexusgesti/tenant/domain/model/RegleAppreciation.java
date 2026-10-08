package com.nexusgesti.tenant.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class RegleAppreciation {

    private UUID idRegle;
    private UUID grilleId;
    private TypePortee portee;
    private BigDecimal moyenneMinimale;
    private BigDecimal moyenneMaximale;
    private String libelle;
    private String modeleTexte;
    private UUID mentionId;

    public UUID getIdRegle() { return idRegle; }
    public void setIdRegle(UUID idRegle) { this.idRegle = idRegle; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public TypePortee getPortee() { return portee; }
    public void setPortee(TypePortee portee) { this.portee = portee; }
    public BigDecimal getMoyenneMinimale() { return moyenneMinimale; }
    public void setMoyenneMinimale(BigDecimal moyenneMinimale) { this.moyenneMinimale = moyenneMinimale; }
    public BigDecimal getMoyenneMaximale() { return moyenneMaximale; }
    public void setMoyenneMaximale(BigDecimal moyenneMaximale) { this.moyenneMaximale = moyenneMaximale; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getModeleTexte() { return modeleTexte; }
    public void setModeleTexte(String modeleTexte) { this.modeleTexte = modeleTexte; }
    public UUID getMentionId() { return mentionId; }
    public void setMentionId(UUID mentionId) { this.mentionId = mentionId; }
}
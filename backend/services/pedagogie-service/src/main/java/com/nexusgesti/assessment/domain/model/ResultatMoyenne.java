package com.nexusgesti.assessment.domain.model;

import java.math.BigDecimal;

public class ResultatMoyenne {

    private BigDecimal moyenne;
    private BigDecimal noteMin;
    private BigDecimal noteMax;
    private int nbNotes;
    private int effectif;

    public BigDecimal getMoyenne() { return moyenne; }
    public void setMoyenne(BigDecimal moyenne) { this.moyenne = moyenne; }
    public BigDecimal getNoteMin() { return noteMin; }
    public void setNoteMin(BigDecimal noteMin) { this.noteMin = noteMin; }
    public BigDecimal getNoteMax() { return noteMax; }
    public void setNoteMax(BigDecimal noteMax) { this.noteMax = noteMax; }
    public int getNbNotes() { return nbNotes; }
    public void setNbNotes(int nbNotes) { this.nbNotes = nbNotes; }
    public int getEffectif() { return effectif; }
    public void setEffectif(int effectif) { this.effectif = effectif; }
}

package com.nexusgesti.fees.domain.model;

import java.util.UUID;

public class ContexteReduction {

    private String niveau;
    private UUID classe;
    private Integer nombreEnfantsScolaires;
    private String statutSocial;
    private boolean bourse;
    private boolean inscriptionTardive;
    private boolean eleveRedoublant;

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
    public UUID getClasse() { return classe; }
    public void setClasse(UUID classe) { this.classe = classe; }
    public Integer getNombreEnfantsScolaires() { return nombreEnfantsScolaires; }
    public void setNombreEnfantsScolaires(Integer nombreEnfantsScolaires) { this.nombreEnfantsScolaires = nombreEnfantsScolaires; }
    public String getStatutSocial() { return statutSocial; }
    public void setStatutSocial(String statutSocial) { this.statutSocial = statutSocial; }
    public boolean isBourse() { return bourse; }
    public void setBourse(boolean bourse) { this.bourse = bourse; }
    public boolean isInscriptionTardive() { return inscriptionTardive; }
    public void setInscriptionTardive(boolean inscriptionTardive) { this.inscriptionTardive = inscriptionTardive; }
    public boolean isEleveRedoublant() { return eleveRedoublant; }
    public void setEleveRedoublant(boolean eleveRedoublant) { this.eleveRedoublant = eleveRedoublant; }
}

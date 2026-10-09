package com.nexusgesti.identity.domain.model;

import java.time.Instant;

public class IdentifiantExterne {
    private String fournisseur;
    private String identifiant;
    private Instant dateLiaison;

    public String getFournisseur() { return fournisseur; }
    public void setFournisseur(String fournisseur) { this.fournisseur = fournisseur; }
    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    public Instant getDateLiaison() { return dateLiaison; }
    public void setDateLiaison(Instant dateLiaison) { this.dateLiaison = dateLiaison; }
}

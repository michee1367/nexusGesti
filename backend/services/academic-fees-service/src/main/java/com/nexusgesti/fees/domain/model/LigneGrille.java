package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class LigneGrille {

    private UUID id;
    private UUID grilleId;
    private TypeFrais typeFrais;
    private CategorieFrais categorie;
    private String libelle;
    private BigDecimal montant = BigDecimal.ZERO;
    private BigDecimal montantPromo;
    private String devise;
    private boolean obligatoire = true;
    private Periodicite periodicite = Periodicite.PONCTUELLE;
    private Integer jour;
    private Integer nbEcheances;
    private Integer ordre = 0;

    public boolean estOptionnel() {
        return !obligatoire;
    }

    public boolean estPeriodicite() {
        return periodicite != null && periodicite.estPeriodique();
    }

    public BigDecimal getMontantEffectif() {
        return montantPromo != null ? montantPromo : montant;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public TypeFrais getTypeFrais() { return typeFrais; }
    public void setTypeFrais(TypeFrais typeFrais) { this.typeFrais = typeFrais; }
    public CategorieFrais getCategorie() { return categorie; }
    public void setCategorie(CategorieFrais categorie) { this.categorie = categorie; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public BigDecimal getMontantPromo() { return montantPromo; }
    public void setMontantPromo(BigDecimal montantPromo) { this.montantPromo = montantPromo; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
    public Periodicite getPeriodicite() { return periodicite; }
    public void setPeriodicite(Periodicite periodicite) { this.periodicite = periodicite; }
    public Integer getJour() { return jour; }
    public void setJour(Integer jour) { this.jour = jour; }
    public Integer getNbEcheances() { return nbEcheances; }
    public void setNbEcheances(Integer nbEcheances) { this.nbEcheances = nbEcheances; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
}

package com.nexusgesti.boulangerie.production.domain.model;

import com.nexusgesti.boulangerie.production.domain.ProductionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Fournee {

    private UUID idFournee;
    private String numero;
    private String recetteCode;
    private Integer versionRecette;
    private String produitCode;
    private String fourCode;
    private UUID operateurId;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private BigDecimal quantitePrevue = BigDecimal.ZERO;
    private BigDecimal quantiteRealisee = BigDecimal.ZERO;
    private StatutFournee statut = StatutFournee.PLANIFIEE;
    private String motifAnnulation;
    private List<LigneConsommation> consommations = new ArrayList<>();
    private List<PerteProduction> pertes = new ArrayList<>();

    public Fournee lancer() {
        if (statut != StatutFournee.PLANIFIEE) {
            throw ProductionException.transitionInvalide(statut.name(), StatutFournee.EN_COURS.name());
        }
        this.statut = StatutFournee.EN_COURS;
        this.dateDebut = LocalDateTime.now();
        return this;
    }

    public Fournee enregistrerRendu(BigDecimal quantite) {
        if (statut != StatutFournee.EN_COURS) {
            throw ProductionException.etatInvalide("La fournee doit etre en cours pour enregistrer un rendu");
        }
        this.quantiteRealisee = quantite == null ? BigDecimal.ZERO : quantite;
        return this;
    }

    public Fournee cuire() {
        if (statut != StatutFournee.EN_COURS) {
            throw ProductionException.transitionInvalide(statut.name(), StatutFournee.CUITE.name());
        }
        this.statut = StatutFournee.CUITE;
        return this;
    }

    public Fournee valider() {
        if (statut != StatutFournee.CUITE) {
            throw ProductionException.transitionInvalide(statut.name(), StatutFournee.VALIDEE.name());
        }
        this.statut = StatutFournee.VALIDEE;
        this.dateFin = LocalDateTime.now();
        return this;
    }

    public Fournee annuler(String motif) {
        if (statut == StatutFournee.VALIDEE || statut == StatutFournee.ANNULEE) {
            throw ProductionException.transitionInvalide(statut.name(), StatutFournee.ANNULEE.name());
        }
        this.statut = StatutFournee.ANNULEE;
        this.motifAnnulation = motif;
        return this;
    }

    public void declarerConsommation(LigneConsommation ligne) {
        if (ligne == null) {
            return;
        }
        ligne.setFourneeId(idFournee);
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        this.consommations.add(ligne);
    }

    public void declarerPerte(PerteProduction perte) {
        if (perte == null) {
            return;
        }
        perte.setFourneeId(idFournee);
        if (perte.getIdPerte() == null) {
            perte.setIdPerte(UUID.randomUUID());
        }
        this.pertes.add(perte);
    }

    public BigDecimal getRendement() {
        if (quantitePrevue == null || quantitePrevue.signum() == 0) {
            return BigDecimal.ZERO;
        }
        return quantiteRealisee.divide(quantitePrevue, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal getEcartMatiere() {
        return consommations.stream()
                .map(LigneConsommation::getEcart)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getIdFournee() { return idFournee; }
    public void setIdFournee(UUID idFournee) { this.idFournee = idFournee; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getRecetteCode() { return recetteCode; }
    public void setRecetteCode(String recetteCode) { this.recetteCode = recetteCode; }
    public Integer getVersionRecette() { return versionRecette; }
    public void setVersionRecette(Integer versionRecette) { this.versionRecette = versionRecette; }
    public String getProduitCode() { return produitCode; }
    public void setProduitCode(String produitCode) { this.produitCode = produitCode; }
    public String getFourCode() { return fourCode; }
    public void setFourCode(String fourCode) { this.fourCode = fourCode; }
    public UUID getOperateurId() { return operateurId; }
    public void setOperateurId(UUID operateurId) { this.operateurId = operateurId; }
    public LocalDateTime getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDateTime dateDebut) { this.dateDebut = dateDebut; }
    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
    public BigDecimal getQuantitePrevue() { return quantitePrevue; }
    public void setQuantitePrevue(BigDecimal quantitePrevue) {
        this.quantitePrevue = quantitePrevue == null ? BigDecimal.ZERO : quantitePrevue;
    }
    public BigDecimal getQuantiteRealisee() { return quantiteRealisee; }
    public void setQuantiteRealisee(BigDecimal quantiteRealisee) {
        this.quantiteRealisee = quantiteRealisee == null ? BigDecimal.ZERO : quantiteRealisee;
    }
    public StatutFournee getStatut() { return statut; }
    public void setStatut(StatutFournee statut) { this.statut = statut == null ? StatutFournee.PLANIFIEE : statut; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public List<LigneConsommation> getConsommations() { return consommations; }
    public void setConsommations(List<LigneConsommation> consommations) {
        this.consommations = consommations == null ? new ArrayList<>() : new ArrayList<>(consommations);
    }
    public List<PerteProduction> getPertes() { return pertes; }
    public void setPertes(List<PerteProduction> pertes) {
        this.pertes = pertes == null ? new ArrayList<>() : new ArrayList<>(pertes);
    }
}

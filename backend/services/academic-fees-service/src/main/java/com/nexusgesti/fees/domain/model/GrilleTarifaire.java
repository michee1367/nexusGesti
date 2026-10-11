package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GrilleTarifaire {

    private UUID id;
    private String code;
    private String libelle;
    private UUID annee;
    private String profil;
    private StatutGrille statut = StatutGrille.BROUILLON;
    private String devise;
    private java.time.LocalDate dateActivation;
    private boolean active;
    private Integer version = 1;
    private boolean modifiable = true;

    private List<LigneGrille> lignes = new ArrayList<>();
    private List<Reduction> reductions = new ArrayList<>();

    public GrilleTarifaire activer() {
        if (estFigee()) {
            throw FeesException.transitionInvalide("FIGEE", "ACTIVER");
        }
        if (statut == StatutGrille.ARCHIVEE) {
            throw FeesException.transitionInvalide(statut.name(), StatutGrille.ACTIVE.name());
        }
        this.statut = StatutGrille.ACTIVE;
        this.active = true;
        this.dateActivation = java.time.LocalDate.now();
        return this;
    }

    public GrilleTarifaire figer() {
        if (estFigee()) {
            throw FeesException.transitionInvalide("FIGEE", "FIGER");
        }
        this.modifiable = false;
        return this;
    }

    public GrilleTarifaire archiver() {
        if (statut == StatutGrille.ARCHIVEE) {
            throw FeesException.transitionInvalide(statut.name(), StatutGrille.ARCHIVEE.name());
        }
        this.statut = StatutGrille.ARCHIVEE;
        this.active = false;
        this.modifiable = false;
        return this;
    }

    public boolean estFigee() {
        return !modifiable;
    }

    public void verifierModifiable() {
        if (estFigee()) {
            throw FeesException.etatInvalide("Grille figee : toute correction cree une nouvelle version");
        }
    }

    public BigDecimal getCoutTotal(String niveau, String categorie) {
        return getLignes(niveau).stream()
                .filter(l -> categorie == null || categorie.isBlank() || l.getCategorie().name().equalsIgnoreCase(categorie))
                .map(LigneGrille::getMontantEffectif)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<LigneGrille> getLignes(String niveau) {
        if (niveau == null || niveau.isBlank()) {
            return new ArrayList<>(lignes);
        }
        return lignes.stream()
                .filter(l -> l.getTypeFrais().name().equalsIgnoreCase(niveau))
                .toList();
    }

    public Reduction trouverReduction(UUID reductionId) {
        return reductions.stream()
                .filter(r -> reductionId.equals(r.getId()))
                .findFirst()
                .orElseThrow(() -> FeesException.notFound("Reduction", reductionId));
    }

    public void ajouterLigne(LigneGrille ligne) {
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        ligne.setGrilleId(id);
        this.lignes.add(ligne);
    }

    public void ajouterReduction(Reduction reduction) {
        if (reduction.getId() == null) {
            reduction.setId(UUID.randomUUID());
        }
        reduction.setGrilleId(id);
        this.reductions.add(reduction);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getAnnee() { return annee; }
    public void setAnnee(UUID annee) { this.annee = annee; }
    public String getProfil() { return profil; }
    public void setProfil(String profil) { this.profil = profil; }
    public StatutGrille getStatut() { return statut; }
    public void setStatut(StatutGrille statut) { this.statut = statut; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public java.time.LocalDate getDateActivation() { return dateActivation; }
    public void setDateActivation(java.time.LocalDate dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public boolean isModifiable() { return modifiable; }
    public void setModifiable(boolean modifiable) { this.modifiable = modifiable; }
    public List<LigneGrille> getLignes() { return lignes; }
    public void setLignes(List<LigneGrille> lignes) { this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes); }
    public List<Reduction> getReductions() { return reductions; }
    public void setReductions(List<Reduction> reductions) { this.reductions = reductions == null ? new ArrayList<>() : new ArrayList<>(reductions); }
}

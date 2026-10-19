package com.nexusgesti.boulangerie.production.domain.model;

import com.nexusgesti.boulangerie.production.domain.ProductionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlanProduction {

    private UUID idPlan;
    private LocalDate date;
    private String atelierCode;
    private StatutPlan statut = StatutPlan.BROUILLON;
    private List<LignePlan> lignes = new ArrayList<>();
    private List<BesoinMatiere> besoinsMatiere = new ArrayList<>();

    public PlanProduction planifier() {
        if (date == null) {
            throw ProductionException.invalide("La date du plan est obligatoire");
        }
        this.statut = StatutPlan.BROUILLON;
        return this;
    }

    public void ajouterLigne(LignePlan ligne) {
        if (ligne == null) {
            return;
        }
        ligne.setPlanId(idPlan);
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        this.lignes.add(ligne);
    }

    public void definirBesoins(List<BesoinMatiere> besoins) {
        this.besoinsMatiere = besoins == null ? new ArrayList<>() : new ArrayList<>(besoins);
    }

    public List<BesoinMatiere> calculerBesoins() {
        return new ArrayList<>(besoinsMatiere);
    }

    public boolean estEquilibre(Map<String, BigDecimal> disponibles) {
        if (besoinsMatiere.isEmpty()) {
            return true;
        }
        Map<String, BigDecimal> stock = disponibles == null ? Map.of() : disponibles;
        return besoinsMatiere.stream().allMatch(besoin -> {
            BigDecimal dispo = stock.getOrDefault(besoin.articleCode(), BigDecimal.ZERO);
            return dispo.compareTo(besoin.quantiteBrute()) >= 0;
        });
    }

    public PlanProduction valider() {
        if (statut != StatutPlan.BROUILLON) {
            throw ProductionException.transitionInvalide(statut.name(), StatutPlan.VALIDE.name());
        }
        this.statut = StatutPlan.VALIDE;
        return this;
    }

    public PlanProduction lancer() {
        if (statut != StatutPlan.VALIDE) {
            throw ProductionException.transitionInvalide(statut.name(), StatutPlan.LANCE.name());
        }
        this.statut = StatutPlan.LANCE;
        return this;
    }

    public PlanProduction cloturer() {
        if (statut != StatutPlan.LANCE) {
            throw ProductionException.transitionInvalide(statut.name(), StatutPlan.CLOTURE.name());
        }
        this.statut = StatutPlan.CLOTURE;
        return this;
    }

    public UUID getIdPlan() { return idPlan; }
    public void setIdPlan(UUID idPlan) { this.idPlan = idPlan; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getAtelierCode() { return atelierCode; }
    public void setAtelierCode(String atelierCode) { this.atelierCode = atelierCode; }
    public StatutPlan getStatut() { return statut; }
    public void setStatut(StatutPlan statut) { this.statut = statut == null ? StatutPlan.BROUILLON : statut; }
    public List<LignePlan> getLignes() { return lignes; }
    public void setLignes(List<LignePlan> lignes) {
        this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes);
    }
    public List<BesoinMatiere> getBesoinsMatiere() { return besoinsMatiere; }
    public void setBesoinsMatiere(List<BesoinMatiere> besoinsMatiere) {
        this.besoinsMatiere = besoinsMatiere == null ? new ArrayList<>() : new ArrayList<>(besoinsMatiere);
    }
}

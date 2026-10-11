package com.nexusgesti.fees.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reduction {

    private UUID id;
    private UUID grilleId;
    private String code;
    private String libelle;
    private TypeReduction type;
    private BigDecimal valeur = BigDecimal.ZERO;
    private boolean cumulable;
    private List<ConditionReduction> conditions = new ArrayList<>();

    public BigDecimal appliquer(BigDecimal montantBrut, ContexteReduction contexte) {
        if (montantBrut == null || montantBrut.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        if (!conditionsSatisfaites(contexte)) {
            return BigDecimal.ZERO;
        }
        BigDecimal reduction = switch (type) {
            case POURCENTAGE -> montantBrut.multiply(valeur).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case MONTANT_FIXE, TRANCHE -> valeur;
            case TARIF_REDUIT -> montantBrut.subtract(valeur);
        };
        if (reduction.signum() < 0) {
            return BigDecimal.ZERO;
        }
        return reduction.min(montantBrut).setScale(2, RoundingMode.HALF_UP);
    }

    public boolean conditionsSatisfaites(ContexteReduction contexte) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        return conditions.stream().allMatch(c -> c.estSatisfaite(contexte));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeReduction getType() { return type; }
    public void setType(TypeReduction type) { this.type = type; }
    public BigDecimal getValeur() { return valeur; }
    public void setValeur(BigDecimal valeur) { this.valeur = valeur; }
    public boolean isCumulable() { return cumulable; }
    public void setCumulable(boolean cumulable) { this.cumulable = cumulable; }
    public List<ConditionReduction> getConditions() { return conditions; }
    public void setConditions(List<ConditionReduction> conditions) { this.conditions = conditions == null ? new ArrayList<>() : new ArrayList<>(conditions); }
}

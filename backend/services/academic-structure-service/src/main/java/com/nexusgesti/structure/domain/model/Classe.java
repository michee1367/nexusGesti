package com.nexusgesti.structure.domain.model;

import com.nexusgesti.structure.domain.StructureException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class Classe {

    private UUID idClasse;
    private String code;
    private String libelle;
    private UUID niveauId;
    private UUID anneeId;
    private UUID siteId;
    private Integer capacite;
    private int effectifReel;
    private StatutClasse statut;
    private UUID salleAttitreeId;
    private String regimeHoraireCode;

    public Classe ouvrir() {
        if (statut != StatutClasse.OUVERTE) {
            throw StructureException.transitionInvalide(String.valueOf(statut), StatutClasse.EN_COURS.name());
        }
        this.statut = StatutClasse.EN_COURS;
        return this;
    }

    public Classe cloturer() {
        if (statut != StatutClasse.EN_COURS && statut != StatutClasse.OUVERTE) {
            throw StructureException.transitionInvalide(String.valueOf(statut), StatutClasse.CLOTUREE.name());
        }
        this.statut = StatutClasse.CLOTUREE;
        return this;
    }

    public BigDecimal getTauxRemplissage() {
        if (capacite == null || capacite == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(effectifReel)
                .divide(BigDecimal.valueOf(capacite), 4, RoundingMode.HALF_UP);
    }

    public boolean estPlein() {
        return capacite != null && effectifReel >= capacite;
    }

    public UUID getIdClasse() { return idClasse; }
    public void setIdClasse(UUID idClasse) { this.idClasse = idClasse; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public UUID getNiveauId() { return niveauId; }
    public void setNiveauId(UUID niveauId) { this.niveauId = niveauId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getSiteId() { return siteId; }
    public void setSiteId(UUID siteId) { this.siteId = siteId; }
    public Integer getCapacite() { return capacite; }
    public void setCapacite(Integer capacite) { this.capacite = capacite; }
    public int getEffectifReel() { return effectifReel; }
    public void setEffectifReel(int effectifReel) { this.effectifReel = effectifReel; }
    public StatutClasse getStatut() { return statut; }
    public void setStatut(StatutClasse statut) { this.statut = statut; }
    public UUID getSalleAttitreeId() { return salleAttitreeId; }
    public void setSalleAttitreeId(UUID salleAttitreeId) { this.salleAttitreeId = salleAttitreeId; }
    public String getRegimeHoraireCode() { return regimeHoraireCode; }
    public void setRegimeHoraireCode(String regimeHoraireCode) { this.regimeHoraireCode = regimeHoraireCode; }
}

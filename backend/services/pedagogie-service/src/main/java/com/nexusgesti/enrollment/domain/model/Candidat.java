package com.nexusgesti.enrollment.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Candidat {

    private UUID eleveId;
    private UUID programmeId;
    private BigDecimal creditsValides;
    private BigDecimal creditsRequis;
    private BigDecimal moyenneGenerale;
    private Integer age;
    private List<String> niveauxValides = new ArrayList<>();

    public boolean aRempli(String condition) {
        if (condition == null || condition.isBlank()) {
            return true;
        }
        String attendu = condition.trim();
        return niveauxValides != null && niveauxValides.stream()
                .anyMatch(n -> n != null && n.equalsIgnoreCase(attendu));
    }

    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public BigDecimal getCreditsValides() { return creditsValides; }
    public void setCreditsValides(BigDecimal creditsValides) { this.creditsValides = creditsValides; }
    public BigDecimal getCreditsRequis() { return creditsRequis; }
    public void setCreditsRequis(BigDecimal creditsRequis) { this.creditsRequis = creditsRequis; }
    public BigDecimal getMoyenneGenerale() { return moyenneGenerale; }
    public void setMoyenneGenerale(BigDecimal moyenneGenerale) { this.moyenneGenerale = moyenneGenerale; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public List<String> getNiveauxValides() { return niveauxValides; }
    public void setNiveauxValides(List<String> niveauxValides) {
        this.niveauxValides = niveauxValides == null ? new ArrayList<>() : new ArrayList<>(niveauxValides);
    }
}

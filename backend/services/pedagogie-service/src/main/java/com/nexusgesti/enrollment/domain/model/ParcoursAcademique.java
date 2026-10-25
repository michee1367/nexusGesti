package com.nexusgesti.enrollment.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ParcoursAcademique {

    private UUID eleveId;
    private UUID programmeId;
    private BigDecimal creditsRequis;
    private List<InscriptionUE> inscriptions = new ArrayList<>();

    public boolean estUeAcquise(UUID ueId) {
        return inscriptions.stream()
                .anyMatch(i -> ueId != null && ueId.equals(i.getUeId())
                        && i.getStatut() == StatutInscriptionUE.VALIDE);
    }

    public BigDecimal getCreditsAcquis() {
        return inscriptions.stream()
                .map(InscriptionUE::getCreditsAcquis)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTauxProgression() {
        if (creditsRequis == null || creditsRequis.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return getCreditsAcquis().divide(creditsRequis, 4, RoundingMode.HALF_UP);
    }

    public BigDecimal getMoyennePonderee() {
        BigDecimal sommePonderee = BigDecimal.ZERO;
        BigDecimal sommePoids = BigDecimal.ZERO;
        for (InscriptionUE i : inscriptions) {
            if (i.getStatut() != StatutInscriptionUE.VALIDE
                    || i.getMoyenneUEPrecedente() == null
                    || i.getCreditsReconnus() == null
                    || i.getCreditsReconnus().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            sommePonderee = sommePonderee.add(i.getMoyenneUEPrecedente().multiply(i.getCreditsReconnus()));
            sommePoids = sommePoids.add(i.getCreditsReconnus());
        }
        if (sommePoids.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return sommePonderee.divide(sommePoids, 2, RoundingMode.HALF_UP);
    }

    public long getNombreValidees() {
        return inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscriptionUE.VALIDE)
                .count();
    }

    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getProgrammeId() { return programmeId; }
    public void setProgrammeId(UUID programmeId) { this.programmeId = programmeId; }
    public BigDecimal getCreditsRequis() { return creditsRequis; }
    public void setCreditsRequis(BigDecimal creditsRequis) { this.creditsRequis = creditsRequis; }
    public List<InscriptionUE> getInscriptions() { return inscriptions; }
    public void setInscriptions(List<InscriptionUE> inscriptions) {
        this.inscriptions = inscriptions == null ? new ArrayList<>() : new ArrayList<>(inscriptions);
    }
}

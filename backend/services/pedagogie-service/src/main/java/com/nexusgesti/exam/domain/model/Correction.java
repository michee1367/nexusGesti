package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Correction {

    private UUID id;
    private UUID epreuveId;
    private UUID correcteur;
    private LocalDate date;
    private StatutCorrection statut = StatutCorrection.OUVERTE;
    private int nbNotesCorrigees;
    private BigDecimal moyenneBrute;
    private BigDecimal moyenneRetenue;
    private BigDecimal plusForteMoyenne;
    private BigDecimal plusFaibleMoyenne;

    public Correction ouvrir() {
        if (statut == StatutCorrection.HOMOLOGUEE) {
            throw ExamException.transitionInvalide(statut.name(), StatutCorrection.OUVERTE.name());
        }
        this.statut = StatutCorrection.OUVERTE;
        if (this.date == null) {
            this.date = LocalDate.now();
        }
        return this;
    }

    public Correction calculerStatistiques(List<NoteExamen> notes) {
        List<BigDecimal> valeurs = notes == null ? List.of() : notes.stream()
                .filter(NoteExamen::comptePourMoyenne)
                .map(NoteExamen::getValeur)
                .filter(java.util.Objects::nonNull)
                .toList();
        this.nbNotesCorrigees = valeurs.size();
        if (valeurs.isEmpty()) {
            this.moyenneBrute = null;
            this.moyenneRetenue = this.moyenneBrute;
            this.plusForteMoyenne = null;
            this.plusFaibleMoyenne = null;
            return this;
        }
        BigDecimal somme = valeurs.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        this.moyenneBrute = somme.divide(BigDecimal.valueOf(valeurs.size()), 2, RoundingMode.HALF_UP);
        this.moyenneRetenue = this.moyenneBrute;
        this.plusForteMoyenne = valeurs.stream().max(BigDecimal::compareTo).orElse(null);
        this.plusFaibleMoyenne = valeurs.stream().min(BigDecimal::compareTo).orElse(null);
        this.statut = StatutCorrection.EN_COURS;
        return this;
    }

    public Correction cloturer() {
        if (statut != StatutCorrection.EN_COURS && statut != StatutCorrection.OUVERTE) {
            throw ExamException.transitionInvalide(statut.name(), StatutCorrection.CLOTUREE.name());
        }
        this.statut = StatutCorrection.CLOTUREE;
        return this;
    }

    public boolean getEcartSignificatif() {
        if (moyenneBrute == null || moyenneRetenue == null) {
            return false;
        }
        return moyenneBrute.subtract(moyenneRetenue).abs().compareTo(new BigDecimal("2")) > 0;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEpreuveId() { return epreuveId; }
    public void setEpreuveId(UUID epreuveId) { this.epreuveId = epreuveId; }
    public UUID getCorrecteur() { return correcteur; }
    public void setCorrecteur(UUID correcteur) { this.correcteur = correcteur; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public StatutCorrection getStatut() { return statut; }
    public void setStatut(StatutCorrection statut) { this.statut = statut; }
    public int getNbNotesCorrigees() { return nbNotesCorrigees; }
    public void setNbNotesCorrigees(int nbNotesCorrigees) { this.nbNotesCorrigees = nbNotesCorrigees; }
    public BigDecimal getMoyenneBrute() { return moyenneBrute; }
    public void setMoyenneBrute(BigDecimal moyenneBrute) { this.moyenneBrute = moyenneBrute; }
    public BigDecimal getMoyenneRetenue() { return moyenneRetenue; }
    public void setMoyenneRetenue(BigDecimal moyenneRetenue) { this.moyenneRetenue = moyenneRetenue; }
    public BigDecimal getPlusForteMoyenne() { return plusForteMoyenne; }
    public void setPlusForteMoyenne(BigDecimal plusForteMoyenne) { this.plusForteMoyenne = plusForteMoyenne; }
    public BigDecimal getPlusFaibleMoyenne() { return plusFaibleMoyenne; }
    public void setPlusFaibleMoyenne(BigDecimal plusFaibleMoyenne) { this.plusFaibleMoyenne = plusFaibleMoyenne; }
}

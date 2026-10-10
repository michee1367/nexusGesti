package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Justificatif {

    private UUID idJustificatif;
    private UUID eleveId;
    private TypeAbsence type;
    private LocalDate dateReference;
    private LocalDate dateDepot;
    private String motif;
    private String fichierUri;
    private StatutJustificatif statut;
    private UUID traitePar;
    private Instant traiteLe;
    private String motifRejet;

    public Justificatif deposer() {
        if (statut == null) {
            this.statut = StatutJustificatif.EN_ATTENTE;
        }
        if (this.dateDepot == null) {
            this.dateDepot = LocalDate.now();
        }
        return this;
    }

    public Justificatif accepter(UUID par) {
        if (statut != StatutJustificatif.EN_ATTENTE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutJustificatif.ACCEPTE.name());
        }
        this.statut = StatutJustificatif.ACCEPTE;
        this.traitePar = par;
        this.traiteLe = Instant.now();
        return this;
    }

    public Justificatif rejeter(String motifRejet, UUID par) {
        if (statut != StatutJustificatif.EN_ATTENTE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutJustificatif.REJETE.name());
        }
        this.statut = StatutJustificatif.REJETE;
        this.motifRejet = motifRejet;
        this.traitePar = par;
        this.traiteLe = Instant.now();
        return this;
    }

    public UUID getIdJustificatif() { return idJustificatif; }
    public void setIdJustificatif(UUID idJustificatif) { this.idJustificatif = idJustificatif; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public TypeAbsence getType() { return type; }
    public void setType(TypeAbsence type) { this.type = type; }
    public LocalDate getDateReference() { return dateReference; }
    public void setDateReference(LocalDate dateReference) { this.dateReference = dateReference; }
    public LocalDate getDateDepot() { return dateDepot; }
    public void setDateDepot(LocalDate dateDepot) { this.dateDepot = dateDepot; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getFichierUri() { return fichierUri; }
    public void setFichierUri(String fichierUri) { this.fichierUri = fichierUri; }
    public StatutJustificatif getStatut() { return statut; }
    public void setStatut(StatutJustificatif statut) { this.statut = statut; }
    public UUID getTraitePar() { return traitePar; }
    public void setTraitePar(UUID traitePar) { this.traitePar = traitePar; }
    public Instant getTraiteLe() { return traiteLe; }
    public void setTraiteLe(Instant traiteLe) { this.traiteLe = traiteLe; }
    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }
}

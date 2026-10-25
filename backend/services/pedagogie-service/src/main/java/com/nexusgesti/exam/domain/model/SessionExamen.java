package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionExamen {

    private UUID idSession;
    private String reference;
    private String libelle;
    private TypeSession type;
    private UUID anneeId;
    private UUID periodeId;
    private UUID classeId;
    private LocalDate dateOuverture;
    private LocalDate dateCloture;
    private StatutSession statut = StatutSession.BROUILLON;
    private boolean anneeUniversitaire;
    private boolean convocationEmise;
    private UUID procesVerbalId;
    private String grilleDeliberation;
    private Integer nbCandidatsAttendus;
    private Integer nbCandidatsConvoques;
    private String motifAnnulation;
    private Instant annuleLe;

    private List<Epreuve> epreuves = new ArrayList<>();
    private List<Creneau> creneaux = new ArrayList<>();
    private List<Convocation> convocations = new ArrayList<>();

    public SessionExamen programmer() {
        if (statut != StatutSession.BROUILLON) {
            throw ExamException.transitionInvalide(statut.name(), StatutSession.PROGRAMMEE.name());
        }
        this.statut = StatutSession.PROGRAMMEE;
        return this;
    }

    public SessionExamen ouvrir() {
        if (statut != StatutSession.PROGRAMMEE) {
            throw ExamException.transitionInvalide(statut.name(), StatutSession.EN_COURS.name());
        }
        this.statut = StatutSession.EN_COURS;
        if (this.dateOuverture == null) {
            this.dateOuverture = LocalDate.now();
        }
        return this;
    }

    public SessionExamen cloturer() {
        if (statut == StatutSession.BROUILLON || statut == StatutSession.ANNULEE || statut == StatutSession.CLOTUREE) {
            throw ExamException.transitionInvalide(statut.name(), StatutSession.CLOTUREE.name());
        }
        this.statut = StatutSession.CLOTUREE;
        this.dateCloture = LocalDate.now();
        return this;
    }

    public SessionExamen annuler(String motif) {
        if (statut == StatutSession.CLOTUREE) {
            throw ExamException.transitionInvalide(statut.name(), StatutSession.ANNULEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw ExamException.invalide("Le motif d annulation est obligatoire");
        }
        this.statut = StatutSession.ANNULEE;
        this.motifAnnulation = motif;
        this.annuleLe = Instant.now();
        return this;
    }

    public boolean estAnnuelle() {
        return periodeId == null && anneeUniversitaire;
    }

    public BigDecimal getTotalPointsPrevus() {
        return epreuves.stream()
                .map(Epreuve::getPointsMax)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean estCloturee() {
        return statut == StatutSession.CLOTUREE;
    }

    public void ajouterEpreuve(Epreuve epreuve) {
        if (epreuve.getIdEpreuve() == null) {
            epreuve.setIdEpreuve(UUID.randomUUID());
        }
        epreuve.setSessionId(idSession);
        this.epreuves.add(epreuve);
    }

    public void ajouterCreneau(Creneau creneau) {
        if (creneau.getId() == null) {
            creneau.setId(UUID.randomUUID());
        }
        creneau.setSessionId(idSession);
        this.creneaux.add(creneau);
    }

    public void ajouterConvocation(Convocation convocation) {
        if (convocation.getId() == null) {
            convocation.setId(UUID.randomUUID());
        }
        convocation.setSessionId(idSession);
        this.convocations.add(convocation);
    }

    public UUID getIdSession() { return idSession; }
    public void setIdSession(UUID idSession) { this.idSession = idSession; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeSession getType() { return type; }
    public void setType(TypeSession type) { this.type = type; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public LocalDate getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDate dateOuverture) { this.dateOuverture = dateOuverture; }
    public LocalDate getDateCloture() { return dateCloture; }
    public void setDateCloture(LocalDate dateCloture) { this.dateCloture = dateCloture; }
    public StatutSession getStatut() { return statut; }
    public void setStatut(StatutSession statut) { this.statut = statut; }
    public boolean isAnneeUniversitaire() { return anneeUniversitaire; }
    public void setAnneeUniversitaire(boolean anneeUniversitaire) { this.anneeUniversitaire = anneeUniversitaire; }
    public boolean isConvocationEmise() { return convocationEmise; }
    public void setConvocationEmise(boolean convocationEmise) { this.convocationEmise = convocationEmise; }
    public UUID getProcesVerbalId() { return procesVerbalId; }
    public void setProcesVerbalId(UUID procesVerbalId) { this.procesVerbalId = procesVerbalId; }
    public String getGrilleDeliberation() { return grilleDeliberation; }
    public void setGrilleDeliberation(String grilleDeliberation) { this.grilleDeliberation = grilleDeliberation; }
    public Integer getNbCandidatsAttendus() { return nbCandidatsAttendus; }
    public void setNbCandidatsAttendus(Integer nbCandidatsAttendus) { this.nbCandidatsAttendus = nbCandidatsAttendus; }
    public Integer getNbCandidatsConvoques() { return nbCandidatsConvoques; }
    public void setNbCandidatsConvoques(Integer nbCandidatsConvoques) { this.nbCandidatsConvoques = nbCandidatsConvoques; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public Instant getAnnuleLe() { return annuleLe; }
    public void setAnnuleLe(Instant annuleLe) { this.annuleLe = annuleLe; }
    public List<Epreuve> getEpreuves() { return epreuves; }
    public void setEpreuves(List<Epreuve> epreuves) { this.epreuves = epreuves == null ? new ArrayList<>() : new ArrayList<>(epreuves); }
    public List<Creneau> getCreneaux() { return creneaux; }
    public void setCreneaux(List<Creneau> creneaux) { this.creneaux = creneaux == null ? new ArrayList<>() : new ArrayList<>(creneaux); }
    public List<Convocation> getConvocations() { return convocations; }
    public void setConvocations(List<Convocation> convocations) { this.convocations = convocations == null ? new ArrayList<>() : new ArrayList<>(convocations); }
}

package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ResultatOfficiel {

    private UUID id;
    private UUID decisionId;
    private String reference;
    private boolean publie;
    private Instant datePublication;
    private UUID generateur;
    private String urlDocument;

    public ResultatOfficiel publier() {
        if (publie) {
            throw ExamException.etatInvalide("Resultat deja publie");
        }
        this.publie = true;
        this.datePublication = Instant.now();
        return this;
    }

    public List<Decision> getClassementGeneral(List<Decision> decisions) {
        if (decisions == null) {
            return List.of();
        }
        return decisions.stream()
                .sorted(Comparator.comparing(Decision::getMoyenneGenerale,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDecisionId() { return decisionId; }
    public void setDecisionId(UUID decisionId) { this.decisionId = decisionId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public Instant getDatePublication() { return datePublication; }
    public void setDatePublication(Instant datePublication) { this.datePublication = datePublication; }
    public UUID getGenerateur() { return generateur; }
    public void setGenerateur(UUID generateur) { this.generateur = generateur; }
    public String getUrlDocument() { return urlDocument; }
    public void setUrlDocument(String urlDocument) { this.urlDocument = urlDocument; }
}

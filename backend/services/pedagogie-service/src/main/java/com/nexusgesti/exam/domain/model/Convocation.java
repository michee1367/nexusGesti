package com.nexusgesti.exam.domain.model;

import com.nexusgesti.exam.domain.ExamException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Convocation {

    private UUID id;
    private UUID sessionId;
    private UUID eleveId;
    private String numero;
    private LocalDate date;
    private StatutConvocation statut = StatutConvocation.EMISE;
    private Instant genereeLe;
    private Instant notifieeLe;
    private String canal;
    private boolean accuse;
    private String qrCode;
    private String emplacement;
    private String urlTelechargement;
    private boolean signee;

    private List<LigneConvocation> lignes = new ArrayList<>();

    public Convocation emettre() {
        if (statut == StatutConvocation.ANNULEE) {
            throw ExamException.transitionInvalide(statut.name(), StatutConvocation.EMISE.name());
        }
        this.statut = StatutConvocation.EMISE;
        if (this.genereeLe == null) {
            this.genereeLe = Instant.now();
        }
        return this;
    }

    public Convocation notifier(String canal) {
        if (statut == StatutConvocation.ANNULEE) {
            throw ExamException.transitionInvalide(statut.name(), StatutConvocation.NOTIFIEE.name());
        }
        this.statut = StatutConvocation.NOTIFIEE;
        this.canal = canal;
        this.notifieeLe = Instant.now();
        return this;
    }

    public List<LigneConvocation> getPlanningDetaille() {
        return lignes;
    }

    public void ajouterLigne(LigneConvocation ligne) {
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        ligne.setConvocationId(id);
        this.lignes.add(ligne);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public StatutConvocation getStatut() { return statut; }
    public void setStatut(StatutConvocation statut) { this.statut = statut; }
    public Instant getGenereeLe() { return genereeLe; }
    public void setGenereeLe(Instant genereeLe) { this.genereeLe = genereeLe; }
    public Instant getNotifieeLe() { return notifieeLe; }
    public void setNotifieeLe(Instant notifieeLe) { this.notifieeLe = notifieeLe; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public boolean isAccuse() { return accuse; }
    public void setAccuse(boolean accuse) { this.accuse = accuse; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public String getUrlTelechargement() { return urlTelechargement; }
    public void setUrlTelechargement(String urlTelechargement) { this.urlTelechargement = urlTelechargement; }
    public boolean isSignee() { return signee; }
    public void setSignee(boolean signee) { this.signee = signee; }
    public List<LigneConvocation> getLignes() { return lignes; }
    public void setLignes(List<LigneConvocation> lignes) { this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes); }
}

package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Reception {

    private UUID idReception;
    private String numero;
    private UUID commandeId;
    private String commandeNumero;
    private LocalDate date;
    private UUID recuPar;
    private String bonLivraison;
    private StatutReception statut = StatutReception.ATTENDUE;
    private String motif;
    private List<LigneReception> lignes = new ArrayList<>();

    public Reception controler() {
        if (statut != StatutReception.ATTENDUE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutReception.EN_CONTROLE.name());
        }
        this.statut = StatutReception.EN_CONTROLE;
        return this;
    }

    public Reception accepter() {
        if (statut != StatutReception.EN_CONTROLE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutReception.ACCEPTEE.name());
        }
        if (lignes.isEmpty()) {
            throw ApprovisionnementException.etatInvalide("Une reception doit porter au moins une ligne");
        }
        if (!estConforme()) {
            throw ApprovisionnementException.etatInvalide(
                    "Reception non conforme : elle doit etre refusee, jamais integree partiellement");
        }
        this.statut = estComplete() ? StatutReception.ACCEPTEE : StatutReception.PARTIELLE;
        return this;
    }

    public Reception refuser(String motif) {
        if (statut != StatutReception.EN_CONTROLE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutReception.REFUSEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw ApprovisionnementException.invalide("Le motif de refus est obligatoire");
        }
        this.statut = StatutReception.REFUSEE;
        this.motif = motif;
        return this;
    }

    public boolean estConforme() {
        return lignes.stream().allMatch(LigneReception::isConforme);
    }

    public boolean estComplete() {
        return !lignes.isEmpty() && lignes.stream().allMatch(LigneReception::estComplete);
    }

    public boolean estHonoree() {
        return statut == StatutReception.ACCEPTEE || statut == StatutReception.PARTIELLE;
    }

    public void ajouterLigne(LigneReception ligne) {
        if (ligne == null) {
            return;
        }
        ligne.setReceptionId(idReception);
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        this.lignes.add(ligne);
    }

    public UUID getIdReception() { return idReception; }
    public void setIdReception(UUID idReception) { this.idReception = idReception; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public UUID getCommandeId() { return commandeId; }
    public void setCommandeId(UUID commandeId) { this.commandeId = commandeId; }
    public String getCommandeNumero() { return commandeNumero; }
    public void setCommandeNumero(String commandeNumero) { this.commandeNumero = commandeNumero; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public UUID getRecuPar() { return recuPar; }
    public void setRecuPar(UUID recuPar) { this.recuPar = recuPar; }
    public String getBonLivraison() { return bonLivraison; }
    public void setBonLivraison(String bonLivraison) { this.bonLivraison = bonLivraison; }
    public StatutReception getStatut() { return statut; }
    public void setStatut(StatutReception statut) {
        this.statut = statut == null ? StatutReception.ATTENDUE : statut;
    }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public List<LigneReception> getLignes() { return lignes; }
    public void setLignes(List<LigneReception> lignes) {
        this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes);
    }
}

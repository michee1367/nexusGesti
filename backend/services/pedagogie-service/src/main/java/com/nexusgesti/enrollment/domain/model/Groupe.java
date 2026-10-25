package com.nexusgesti.enrollment.domain.model;

import com.nexusgesti.enrollment.domain.EnrollmentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Groupe {

    private UUID idGroupe;
    private String code;
    private String libelle;
    private TypeGroupe type;
    private UUID coursId;
    private UUID ueId;
    private UUID classeId;
    private UUID enseignantId;
    private UUID salleId;
    private UUID creneauId;
    private String jour;
    private int capacite;
    private int effectif;
    private StatutGroupe statut;
    private List<MembreGroupe> membres = new ArrayList<>();

    public Groupe ouvrir() {
        if (statut == StatutGroupe.CLOS || statut == StatutGroupe.ARCHIVE) {
            throw EnrollmentException.transitionInvalide(statut.name(), StatutGroupe.OUVERT.name());
        }
        this.statut = StatutGroupe.OUVERT;
        return this;
    }

    public Groupe clore() {
        this.statut = StatutGroupe.CLOS;
        return this;
    }

    public boolean estComplet() {
        return capacite > 0 && effectif >= capacite;
    }

    public int getPlacesRestantes() {
        return Math.max(0, capacite - effectif);
    }

    public Groupe ajouterMembre(MembreGroupe membre) {
        if (statut == StatutGroupe.CLOS || statut == StatutGroupe.ARCHIVE) {
            throw EnrollmentException.etatInvalide("Groupe " + statut.name() + " : affectation impossible");
        }
        if (estComplet()) {
            throw EnrollmentException.conflit("GROUPE_COMPLET", "Groupe complet : " + code);
        }
        if (membre.getIdMembre() == null) {
            membre.setIdMembre(UUID.randomUUID());
        }
        membre.setGroupeId(idGroupe);
        membres.add(membre);
        synchroniserEffectif();
        return this;
    }

    public Optional<MembreGroupe> exclureMembre(UUID membreId) {
        Optional<MembreGroupe> retire = membres.stream()
                .filter(m -> membreId.equals(m.getIdMembre()))
                .findFirst();
        retire.ifPresent(m -> membres.remove(m));
        synchroniserEffectif();
        return retire;
    }

    private void synchroniserEffectif() {
        this.effectif = membres.size();
        if (statut == StatutGroupe.OUVERT && estComplet()) {
            this.statut = StatutGroupe.COMPLET;
        } else if (statut == StatutGroupe.COMPLET && !estComplet()) {
            this.statut = StatutGroupe.OUVERT;
        }
    }

    public UUID getIdGroupe() { return idGroupe; }
    public void setIdGroupe(UUID idGroupe) { this.idGroupe = idGroupe; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeGroupe getType() { return type; }
    public void setType(TypeGroupe type) { this.type = type; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getUeId() { return ueId; }
    public void setUeId(UUID ueId) { this.ueId = ueId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public UUID getSalleId() { return salleId; }
    public void setSalleId(UUID salleId) { this.salleId = salleId; }
    public UUID getCreneauId() { return creneauId; }
    public void setCreneauId(UUID creneauId) { this.creneauId = creneauId; }
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public int getEffectif() { return effectif; }
    public void setEffectif(int effectif) { this.effectif = effectif; }
    public StatutGroupe getStatut() { return statut; }
    public void setStatut(StatutGroupe statut) { this.statut = statut; }
    public List<MembreGroupe> getMembres() { return membres; }
    public void setMembres(List<MembreGroupe> membres) {
        this.membres = membres == null ? new ArrayList<>() : new ArrayList<>(membres);
    }
}

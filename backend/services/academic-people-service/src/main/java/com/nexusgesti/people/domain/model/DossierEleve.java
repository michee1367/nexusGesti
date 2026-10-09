package com.nexusgesti.people.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DossierEleve {

    private UUID id;
    private UUID eleveId;
    private String reference;
    private LocalDate dateOuverture;
    private StatutDossier statut;
    private List<FichierJoint> pieces = new ArrayList<>();

    private boolean aPiece(CategoriePiece categorie) {
        return pieces.stream().anyMatch(p -> p.getCategorie() == categorie);
    }

    public boolean estComplet() {
        return getPiecesManquantes().isEmpty();
    }

    public List<String> getPiecesManquantes() {
        List<String> manquantes = new ArrayList<>();
        if (!aPiece(CategoriePiece.PHOTO_IDENTITE)) {
            manquantes.add(CategoriePiece.PHOTO_IDENTITE.name());
        }
        if (!aPiece(CategoriePiece.ACTE_NAISSANCE)) {
            manquantes.add(CategoriePiece.ACTE_NAISSANCE.name());
        }
        return manquantes;
    }

    public DossierEleve ajouterPiece(FichierJoint fichier) {
        if (fichier.getId() == null) {
            fichier.setId(UUID.randomUUID());
        }
        pieces.add(fichier);
        if (statut != StatutDossier.ARCHIVE) {
            statut = estComplet() ? StatutDossier.COMPLET : StatutDossier.INCOMPLET;
        }
        return this;
    }

    public DossierEleve archiver() {
        if (statut == StatutDossier.ARCHIVE) {
            return this;
        }
        this.statut = StatutDossier.ARCHIVE;
        return this;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public LocalDate getDateOuverture() { return dateOuverture; }
    public void setDateOuverture(LocalDate dateOuverture) { this.dateOuverture = dateOuverture; }
    public StatutDossier getStatut() { return statut; }
    public void setStatut(StatutDossier statut) { this.statut = statut; }
    public List<FichierJoint> getPieces() { return pieces; }
    public void setPieces(List<FichierJoint> pieces) {
        this.pieces = pieces == null ? new ArrayList<>() : new ArrayList<>(pieces);
    }
}

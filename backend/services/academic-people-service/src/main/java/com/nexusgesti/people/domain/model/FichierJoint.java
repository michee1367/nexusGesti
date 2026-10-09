package com.nexusgesti.people.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FichierJoint {

    private UUID id;
    private CategoriePiece categorie;
    private String nom;
    private String typeMime;
    private Long taille;
    private String uri;
    private Instant dateDepot;
    private UUID deposePar;
    private boolean documentOfficiel;

    public boolean estValide(List<String> typesMimeAcceptes) {
        return nom != null && !nom.isBlank()
                && uri != null && !uri.isBlank()
                && (typesMimeAcceptes == null || typesMimeAcceptes.isEmpty()
                    || typesMimeAcceptes.contains(typeMime));
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CategoriePiece getCategorie() { return categorie; }
    public void setCategorie(CategoriePiece categorie) { this.categorie = categorie; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getTypeMime() { return typeMime; }
    public void setTypeMime(String typeMime) { this.typeMime = typeMime; }
    public Long getTaille() { return taille; }
    public void setTaille(Long taille) { this.taille = taille; }
    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }
    public Instant getDateDepot() { return dateDepot; }
    public void setDateDepot(Instant dateDepot) { this.dateDepot = dateDepot; }
    public UUID getDeposePar() { return deposePar; }
    public void setDeposePar(UUID deposePar) { this.deposePar = deposePar; }
    public boolean isDocumentOfficiel() { return documentOfficiel; }
    public void setDocumentOfficiel(boolean documentOfficiel) { this.documentOfficiel = documentOfficiel; }
}

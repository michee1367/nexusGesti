package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_grille", schema = "academic_fees")
public class LigneGrilleEntity {

    @Id
    private UUID id;

    @Column(name = "grille_id", nullable = false)
    private UUID grilleId;

    @Column(name = "type_frais", nullable = false)
    private String typeFrais;

    @Column(nullable = false)
    private String categorie;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private BigDecimal montant;

    @Column(name = "montant_promo")
    private BigDecimal montantPromo;

    private String devise;

    @Column(nullable = false)
    private boolean obligatoire;

    @Column(nullable = false)
    private String periodicite;

    private Integer jour;

    @Column(name = "nb_echeances")
    private Integer nbEcheances;

    @Column(nullable = false)
    private Integer ordre;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGrilleId() { return grilleId; }
    public void setGrilleId(UUID grilleId) { this.grilleId = grilleId; }
    public String getTypeFrais() { return typeFrais; }
    public void setTypeFrais(String typeFrais) { this.typeFrais = typeFrais; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public BigDecimal getMontantPromo() { return montantPromo; }
    public void setMontantPromo(BigDecimal montantPromo) { this.montantPromo = montantPromo; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public boolean isObligatoire() { return obligatoire; }
    public void setObligatoire(boolean obligatoire) { this.obligatoire = obligatoire; }
    public String getPeriodicite() { return periodicite; }
    public void setPeriodicite(String periodicite) { this.periodicite = periodicite; }
    public Integer getJour() { return jour; }
    public void setJour(Integer jour) { this.jour = jour; }
    public Integer getNbEcheances() { return nbEcheances; }
    public void setNbEcheances(Integer nbEcheances) { this.nbEcheances = nbEcheances; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
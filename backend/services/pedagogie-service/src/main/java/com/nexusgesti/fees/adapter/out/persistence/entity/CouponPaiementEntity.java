package com.nexusgesti.fees.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "coupon_paiement", schema = "pedagogie")
public class CouponPaiementEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private Integer sequence;

    @Column(nullable = false)
    private UUID annee;

    @Column(nullable = false)
    private UUID eleve;

    private UUID paiement;

    private UUID avis;

    @Column(name = "ligne_frais")
    private UUID ligneFrais;

    @Column(name = "date_edition")
    private LocalDate dateEdition;

    @Column(nullable = false)
    private BigDecimal montant;

    private String libelle;

    private String moyen;

    @Column(nullable = false)
    private String statut;

    private String devise;

    private String mention;

    @Column(name = "url_qr")
    private String urlQr;

    @Column(name = "code_barre")
    private String codeBarre;

    private String empreinte;

    @Column(name = "remis_le")
    private Instant remisLe;

    @Column(name = "remis_a")
    private UUID remisA;

    @Column(name = "reference_signature")
    private String referenceSignature;

    @Column(nullable = false)
    private boolean annule;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public Integer getSequence() { return sequence; }
    public void setSequence(Integer sequence) { this.sequence = sequence; }
    public UUID getAnnee() { return annee; }
    public void setAnnee(UUID annee) { this.annee = annee; }
    public UUID getEleve() { return eleve; }
    public void setEleve(UUID eleve) { this.eleve = eleve; }
    public UUID getPaiement() { return paiement; }
    public void setPaiement(UUID paiement) { this.paiement = paiement; }
    public UUID getAvis() { return avis; }
    public void setAvis(UUID avis) { this.avis = avis; }
    public UUID getLigneFrais() { return ligneFrais; }
    public void setLigneFrais(UUID ligneFrais) { this.ligneFrais = ligneFrais; }
    public LocalDate getDateEdition() { return dateEdition; }
    public void setDateEdition(LocalDate dateEdition) { this.dateEdition = dateEdition; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public String getMoyen() { return moyen; }
    public void setMoyen(String moyen) { this.moyen = moyen; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public String getMention() { return mention; }
    public void setMention(String mention) { this.mention = mention; }
    public String getUrlQr() { return urlQr; }
    public void setUrlQr(String urlQr) { this.urlQr = urlQr; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public String getEmpreinte() { return empreinte; }
    public void setEmpreinte(String empreinte) { this.empreinte = empreinte; }
    public Instant getRemisLe() { return remisLe; }
    public void setRemisLe(Instant remisLe) { this.remisLe = remisLe; }
    public UUID getRemisA() { return remisA; }
    public void setRemisA(UUID remisA) { this.remisA = remisA; }
    public String getReferenceSignature() { return referenceSignature; }
    public void setReferenceSignature(String referenceSignature) { this.referenceSignature = referenceSignature; }
    public boolean isAnnule() { return annule; }
    public void setAnnule(boolean annule) { this.annule = annule; }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.UUID;

public class CouponPaiement {

    private UUID id;
    private String numero;
    private Integer sequence;
    private UUID annee;
    private UUID eleve;
    private UUID paiement;
    private UUID avis;
    private UUID ligneFrais;
    private LocalDate dateEdition;
    private BigDecimal montant = BigDecimal.ZERO;
    private String libelle;
    private MoyenPaiement moyen;
    private StatutCoupon statut = StatutCoupon.A_TIRER;
    private String devise;
    private String mention;
    private String urlQr;
    private String codeBarre;
    private String empreinte;
    private Instant remisLe;
    private UUID remisA;
    private String referenceSignature;
    private boolean annule;
    private String motifAnnulation;

    public CouponPaiement tirer() {
        if (annule) {
            throw FeesException.etatInvalide("Coupon annule : tirage impossible");
        }
        this.statut = StatutCoupon.TIRE;
        this.empreinte = calculerEmpreinte();
        return this;
    }

    public CouponPaiement remettre(UUID destinataire) {
        if (annule) {
            throw FeesException.etatInvalide("Coupon annule : remise impossible");
        }
        if (statut != StatutCoupon.TIRE && statut != StatutCoupon.REMIS) {
            throw FeesException.etatInvalide("Coupon non tire : remise impossible");
        }
        this.statut = StatutCoupon.REMIS;
        this.remisLe = Instant.now();
        this.remisA = destinataire;
        return this;
    }

    public CouponPaiement annuler(String motif) {
        if (statut == StatutCoupon.ANNULE) {
            throw FeesException.transitionInvalide(statut.name(), StatutCoupon.ANNULE.name());
        }
        if (statut == StatutCoupon.REMIS) {
            throw FeesException.etatInvalide("Coupon deja remis : annulation interdite");
        }
        this.statut = StatutCoupon.ANNULE;
        this.annule = true;
        this.motifAnnulation = motif;
        return this;
    }

    public boolean verifierEmpreinte() {
        if (empreinte == null || empreinte.isBlank()) {
            return false;
        }
        return empreinte.equalsIgnoreCase(calculerEmpreinte());
    }

    public String calculerEmpreinte() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(getTexte().getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).toUpperCase();
        } catch (NoSuchAlgorithmException ex) {
            throw FeesException.etatInvalide("SHA-256 indisponible");
        }
    }

    public String getTexte() {
        return numero + "|" + montant.toPlainString() + "|" + eleve + "|" + devise
                + "|" + moyen + "|" + libelle;
    }

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
    public MoyenPaiement getMoyen() { return moyen; }
    public void setMoyen(MoyenPaiement moyen) { this.moyen = moyen; }
    public StatutCoupon getStatut() { return statut; }
    public void setStatut(StatutCoupon statut) { this.statut = statut; }
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
}
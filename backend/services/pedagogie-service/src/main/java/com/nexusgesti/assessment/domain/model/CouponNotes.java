package com.nexusgesti.assessment.domain.model;

import com.nexusgesti.assessment.domain.AssessmentException;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.UUID;

public class CouponNotes {

    private UUID id;
    private String numero;
    private UUID bulletinId;
    private UUID eleveId;
    private UUID anneeId;
    private UUID periodeId;
    private String classe;
    private LocalDate dateEdition;
    private StatutCoupon statut;
    private BigDecimal noteMoyenne;
    private String mention;
    private String urlQr;
    private String codeBarre;
    private String empreinte;
    private Instant detacheLe;
    private UUID detachePar;
    private UUID signePar;
    private Instant signeLe;
    private String observation;
    private Instant dateRendu;

    public CouponNotes() {
        this.statut = StatutCoupon.A_TIRER;
    }

    public CouponNotes tirer() {
        if (statut != StatutCoupon.A_TIRER) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutCoupon.TIRE.name());
        }
        this.statut = StatutCoupon.TIRE;
        return this;
    }

    public CouponNotes detacher(UUID par) {
        if (statut != StatutCoupon.TIRE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutCoupon.DETACHE.name());
        }
        this.statut = StatutCoupon.DETACHE;
        this.detacheLe = Instant.now();
        this.detachePar = par;
        return this;
    }

    public CouponNotes signer(UUID par) {
        if (statut != StatutCoupon.DETACHE) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutCoupon.SIGNE_PAR_TUTEUR.name());
        }
        this.statut = StatutCoupon.SIGNE_PAR_TUTEUR;
        this.signeLe = Instant.now();
        this.signePar = par;
        return this;
    }

    public CouponNotes rendre() {
        if (statut != StatutCoupon.SIGNE_PAR_TUTEUR) {
            throw AssessmentException.transitionInvalide(String.valueOf(statut), StatutCoupon.RENDU.name());
        }
        this.statut = StatutCoupon.RENDU;
        this.dateRendu = Instant.now();
        return this;
    }

    public CouponNotes marquerPerdu() {
        if (statut == StatutCoupon.RENDU || statut == StatutCoupon.INVALIDE) {
            throw AssessmentException.etatInvalide("Un coupon " + statut + " ne peut pas etre marque perdu");
        }
        this.statut = StatutCoupon.PERDU;
        return this;
    }

    public boolean estSigne() {
        return statut == StatutCoupon.SIGNE_PAR_TUTEUR || statut == StatutCoupon.RENDU;
    }

    public boolean estRendu() {
        return statut == StatutCoupon.RENDU;
    }

    public void calculerEmpreinte() {
        String base = String.join("|",
                numero == null ? "" : numero,
                bulletinId == null ? "" : bulletinId.toString(),
                eleveId == null ? "" : eleveId.toString(),
                noteMoyenne == null ? "" : noteMoyenne.toPlainString(),
                dateEdition == null ? "" : dateEdition.toString());
        this.empreinte = sha256(base);
    }

    public boolean verifierEmpreinte() {
        if (empreinte == null) {
            return false;
        }
        String base = String.join("|",
                numero == null ? "" : numero,
                bulletinId == null ? "" : bulletinId.toString(),
                eleveId == null ? "" : eleveId.toString(),
                noteMoyenne == null ? "" : noteMoyenne.toPlainString(),
                dateEdition == null ? "" : dateEdition.toString());
        return empreinte.equals(sha256(base));
    }

    public String getTexte() {
        return "Coupon " + numero + " - " + (noteMoyenne == null ? "n/a" : noteMoyenne.toPlainString())
                + (mention == null ? "" : " (" + mention + ")") + " - " + statut;
    }

    private static String sha256(String valeur) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valeur.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw AssessmentException.etatInvalide("Algorithme d empreinte indisponible");
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public UUID getBulletinId() { return bulletinId; }
    public void setBulletinId(UUID bulletinId) { this.bulletinId = bulletinId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }
    public LocalDate getDateEdition() { return dateEdition; }
    public void setDateEdition(LocalDate dateEdition) { this.dateEdition = dateEdition; }
    public StatutCoupon getStatut() { return statut; }
    public void setStatut(StatutCoupon statut) { this.statut = statut; }
    public BigDecimal getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(BigDecimal noteMoyenne) { this.noteMoyenne = noteMoyenne; }
    public String getMention() { return mention; }
    public void setMention(String mention) { this.mention = mention; }
    public String getUrlQr() { return urlQr; }
    public void setUrlQr(String urlQr) { this.urlQr = urlQr; }
    public String getCodeBarre() { return codeBarre; }
    public void setCodeBarre(String codeBarre) { this.codeBarre = codeBarre; }
    public String getEmpreinte() { return empreinte; }
    public void setEmpreinte(String empreinte) { this.empreinte = empreinte; }
    public Instant getDetacheLe() { return detacheLe; }
    public void setDetacheLe(Instant detacheLe) { this.detacheLe = detacheLe; }
    public UUID getDetachePar() { return detachePar; }
    public void setDetachePar(UUID detachePar) { this.detachePar = detachePar; }
    public UUID getSignePar() { return signePar; }
    public void setSignePar(UUID signePar) { this.signePar = signePar; }
    public Instant getSigneLe() { return signeLe; }
    public void setSigneLe(Instant signeLe) { this.signeLe = signeLe; }
    public String getObservation() { return observation; }
    public void setObservation(String observation) { this.observation = observation; }
    public Instant getDateRendu() { return dateRendu; }
    public void setDateRendu(Instant dateRendu) { this.dateRendu = dateRendu; }
}

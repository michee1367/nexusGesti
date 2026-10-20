package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;

import java.math.BigDecimal;
import java.util.UUID;

public class LigneReception {

    private UUID id;
    private UUID receptionId;
    private String articleCode;
    private BigDecimal quantiteCommandee = BigDecimal.ZERO;
    private BigDecimal quantiteRecue = BigDecimal.ZERO;
    private BigDecimal prixAchat = BigDecimal.ZERO;
    private String lotNumero;
    private java.time.LocalDate datePeremption;
    private boolean conforme = true;
    private String motifNonConformite;

    public boolean estComplete() {
        BigDecimal commandee = quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee;
        BigDecimal recue = quantiteRecue == null ? BigDecimal.ZERO : quantiteRecue;
        return recue.compareTo(commandee) >= 0;
    }

    public void verifier() {
        if (articleCode == null || articleCode.isBlank()) {
            throw ApprovisionnementException.invalide("L article est obligatoire sur une ligne de reception");
        }
        if (quantiteRecue == null || quantiteRecue.signum() <= 0) {
            throw ApprovisionnementException.invalide("La quantite recue doit etre superieure a zero");
        }
        if (quantiteRecue.compareTo(quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee) > 0) {
            throw ApprovisionnementException.invalide(
                    "La quantite recue ne peut pas depasser la quantite commandee pour l article " + articleCode);
        }
        if (prixAchat == null || prixAchat.signum() < 0) {
            throw ApprovisionnementException.invalide("Le prix d achat ne peut pas etre negatif");
        }
        if (!conforme && (motifNonConformite == null || motifNonConformite.isBlank())) {
            throw ApprovisionnementException.invalide(
                    "Une ligne non conforme doit porter un motif de non conformite");
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReceptionId() { return receptionId; }
    public void setReceptionId(UUID receptionId) { this.receptionId = receptionId; }
    public String getArticleCode() { return articleCode; }
    public void setArticleCode(String articleCode) { this.articleCode = articleCode; }
    public BigDecimal getQuantiteCommandee() { return quantiteCommandee; }
    public void setQuantiteCommandee(BigDecimal quantiteCommandee) {
        this.quantiteCommandee = quantiteCommandee == null ? BigDecimal.ZERO : quantiteCommandee;
    }
    public BigDecimal getQuantiteRecue() { return quantiteRecue; }
    public void setQuantiteRecue(BigDecimal quantiteRecue) {
        this.quantiteRecue = quantiteRecue == null ? BigDecimal.ZERO : quantiteRecue;
    }
    public BigDecimal getPrixAchat() { return prixAchat; }
    public void setPrixAchat(BigDecimal prixAchat) {
        this.prixAchat = prixAchat == null ? BigDecimal.ZERO : prixAchat;
    }
    public String getLotNumero() { return lotNumero; }
    public void setLotNumero(String lotNumero) { this.lotNumero = lotNumero; }
    public java.time.LocalDate getDatePeremption() { return datePeremption; }
    public void setDatePeremption(java.time.LocalDate datePeremption) { this.datePeremption = datePeremption; }
    public boolean isConforme() { return conforme; }
    public void setConforme(boolean conforme) { this.conforme = conforme; }
    public String getMotifNonConformite() { return motifNonConformite; }
    public void setMotifNonConformite(String motifNonConformite) { this.motifNonConformite = motifNonConformite; }
}

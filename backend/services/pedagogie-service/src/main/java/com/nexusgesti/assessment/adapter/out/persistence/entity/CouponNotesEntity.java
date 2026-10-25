package com.nexusgesti.assessment.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "coupon_notes", schema = "pedagogie")
public class CouponNotesEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String numero;

    @Column(name = "bulletin_id", nullable = false)
    private UUID bulletinId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(name = "annee_id")
    private UUID anneeId;

    @Column(name = "periode_id")
    private UUID periodeId;

    private String classe;

    @Column(name = "date_edition", nullable = false)
    private LocalDate dateEdition;

    @Column(nullable = false)
    private String statut;

    @Column(name = "note_moyenne")
    private BigDecimal noteMoyenne;

    private String mention;

    @Column(name = "url_qr")
    private String urlQr;

    @Column(name = "code_barre")
    private String codeBarre;

    private String empreinte;

    @Column(name = "detache_le")
    private Instant detacheLe;

    @Column(name = "detache_par")
    private UUID detachePar;

    @Column(name = "signe_par")
    private UUID signePar;

    @Column(name = "signe_le")
    private Instant signeLe;

    private String observation;

    @Column(name = "date_rendu")
    private Instant dateRendu;

    @Column(name = "tenant_id")
    private UUID tenantId;

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
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
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
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

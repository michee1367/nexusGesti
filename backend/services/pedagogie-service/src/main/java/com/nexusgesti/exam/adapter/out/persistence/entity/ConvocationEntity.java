package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "convocation", schema = "pedagogie")
public class ConvocationEntity {

    @Id
    private UUID id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @Column(nullable = false)
    private String numero;

    @Column(name = "date_convocation")
    private LocalDate dateConvocation;

    @Column(nullable = false)
    private String statut;

    @Column(name = "generee_le")
    private Instant genereeLe;

    @Column(name = "notifiee_le")
    private Instant notifieeLe;

    private String canal;

    @Column(nullable = false)
    private boolean accuse;

    @Column(name = "qr_code")
    private String qrCode;

    private String emplacement;

    @Column(name = "url_telechargement")
    private String urlTelechargement;

    @Column(nullable = false)
    private boolean signee;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionId() { return sessionId; }
    public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public LocalDate getDateConvocation() { return dateConvocation; }
    public void setDateConvocation(LocalDate dateConvocation) { this.dateConvocation = dateConvocation; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public Instant getGenereeLe() { return genereeLe; }
    public void setGenereeLe(Instant genereeLe) { this.genereeLe = genereeLe; }
    public Instant getNotifieeLe() { return notifieeLe; }
    public void setNotifieeLe(Instant notifieeLe) { this.notifieeLe = notifieeLe; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    public boolean isAccuse() { return accuse; }
    public void setAccuse(boolean accuse) { this.accuse = accuse; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public String getEmplacement() { return emplacement; }
    public void setEmplacement(String emplacement) { this.emplacement = emplacement; }
    public String getUrlTelechargement() { return urlTelechargement; }
    public void setUrlTelechargement(String urlTelechargement) { this.urlTelechargement = urlTelechargement; }
    public boolean isSignee() { return signee; }
    public void setSignee(boolean signee) { this.signee = signee; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
package com.nexusgesti.exam.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "rattrapage", schema = "pedagogie")
public class RattrapageEntity {

    @Id
    private UUID id;

    @Column(name = "session_origine_id")
    private UUID sessionOrigineId;

    @Column(name = "session_rattrapage_id")
    private UUID sessionRattrapageId;

    @Column(name = "eleve_id", nullable = false)
    private UUID eleveId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "epreuves_concernees")
    private String epreuvesConcernees;

    @Column(name = "convocation_id")
    private UUID convocationId;

    @Column(name = "ne_pas_presenter", nullable = false)
    private boolean nePasPresenter;

    private String motif;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionOrigineId() { return sessionOrigineId; }
    public void setSessionOrigineId(UUID sessionOrigineId) { this.sessionOrigineId = sessionOrigineId; }
    public UUID getSessionRattrapageId() { return sessionRattrapageId; }
    public void setSessionRattrapageId(UUID sessionRattrapageId) { this.sessionRattrapageId = sessionRattrapageId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public String getEpreuvesConcernees() { return epreuvesConcernees; }
    public void setEpreuvesConcernees(String epreuvesConcernees) { this.epreuvesConcernees = epreuvesConcernees; }
    public UUID getConvocationId() { return convocationId; }
    public void setConvocationId(UUID convocationId) { this.convocationId = convocationId; }
    public boolean isNePasPresenter() { return nePasPresenter; }
    public void setNePasPresenter(boolean nePasPresenter) { this.nePasPresenter = nePasPresenter; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}
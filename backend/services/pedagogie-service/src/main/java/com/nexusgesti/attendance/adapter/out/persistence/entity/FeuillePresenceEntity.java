package com.nexusgesti.attendance.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "feuille_presence", schema = "pedagogie")
public class FeuillePresenceEntity {

    @Id
    private UUID id;

    @Column(name = "seance_id")
    private UUID seanceId;

    @Column(name = "classe_id")
    private UUID classeId;

    @Column(name = "cours_id")
    private UUID coursId;

    @Column(name = "enseignant_id")
    private UUID enseignantId;

    private LocalDate date;

    @Column(nullable = false)
    private String statut;

    private String methode;

    @Column(name = "ouverte_par")
    private UUID ouvertePar;

    @Column(name = "ouverte_le")
    private Instant ouverteLe;

    @Column(name = "soumise_le")
    private Instant soumiseLe;

    @Column(name = "validee_par")
    private UUID valideePar;

    @Column(name = "validee_le")
    private Instant valideeLe;

    @Column(name = "effectif_theorique")
    private Integer effectifTheorique;

    @Column(name = "effectif_present")
    private Integer effectifPresent;

    private String observations;

    @Column(name = "tenant_id")
    private UUID tenantId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSeanceId() { return seanceId; }
    public void setSeanceId(UUID seanceId) { this.seanceId = seanceId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public UUID getCoursId() { return coursId; }
    public void setCoursId(UUID coursId) { this.coursId = coursId; }
    public UUID getEnseignantId() { return enseignantId; }
    public void setEnseignantId(UUID enseignantId) { this.enseignantId = enseignantId; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }
    public UUID getOuvertePar() { return ouvertePar; }
    public void setOuvertePar(UUID ouvertePar) { this.ouvertePar = ouvertePar; }
    public Instant getOuverteLe() { return ouverteLe; }
    public void setOuverteLe(Instant ouverteLe) { this.ouverteLe = ouverteLe; }
    public Instant getSoumiseLe() { return soumiseLe; }
    public void setSoumiseLe(Instant soumiseLe) { this.soumiseLe = soumiseLe; }
    public UUID getValideePar() { return valideePar; }
    public void setValideePar(UUID valideePar) { this.valideePar = valideePar; }
    public Instant getValideeLe() { return valideeLe; }
    public void setValideeLe(Instant valideeLe) { this.valideeLe = valideeLe; }
    public Integer getEffectifTheorique() { return effectifTheorique; }
    public void setEffectifTheorique(Integer effectifTheorique) { this.effectifTheorique = effectifTheorique; }
    public Integer getEffectifPresent() { return effectifPresent; }
    public void setEffectifPresent(Integer effectifPresent) { this.effectifPresent = effectifPresent; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
}

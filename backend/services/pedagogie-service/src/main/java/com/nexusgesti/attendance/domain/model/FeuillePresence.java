package com.nexusgesti.attendance.domain.model;

import com.nexusgesti.attendance.domain.AttendanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FeuillePresence {

    private UUID idFeuille;
    private UUID seanceId;
    private UUID classeId;
    private UUID coursId;
    private UUID enseignantId;
    private LocalDate date;
    private StatutFeuille statut;
    private MethodeSaisie methode;
    private UUID ouvertePar;
    private Instant ouverteLe;
    private Instant soumiseLe;
    private UUID valideePar;
    private Instant valideeLe;
    private Integer effectifTheorique;
    private Integer effectifPresent;
    private String observations;
    private List<PresenceEleve> presences = new ArrayList<>();

    public FeuillePresence ouvrir() {
        if (statut != null && statut != StatutFeuille.BROUILLON) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutFeuille.BROUILLON.name());
        }
        this.statut = StatutFeuille.BROUILLON;
        this.ouverteLe = Instant.now();
        recalculer();
        return this;
    }

    public FeuillePresence pointer(UUID eleveId, StatutPresence statut, UUID par) {
        PresenceEleve ligne = ligne(eleveId);
        verifierModifiable(ligne);
        ligne.setStatut(statut);
        ligne.setSaisiePar(par);
        ligne.setSaisieLe(Instant.now());
        recalculer();
        return this;
    }

    public FeuillePresence marquerAbsence(UUID eleveId, TypeAbsence type, UUID par) {
        PresenceEleve ligne = ligne(eleveId);
        verifierModifiable(ligne);
        ligne.setStatut(StatutPresence.ABSENT_NON_EXCUSE);
        ligne.setTypeAbsence(type);
        ligne.setSaisiePar(par);
        ligne.setSaisieLe(Instant.now());
        recalculer();
        return this;
    }

    public FeuillePresence marquerRetard(UUID eleveId, int minutes, UUID par) {
        PresenceEleve ligne = ligne(eleveId);
        verifierModifiable(ligne);
        ligne.setStatut(StatutPresence.RETARD);
        ligne.setMinutesRetard(minutes);
        ligne.setSaisiePar(par);
        ligne.setSaisieLe(Instant.now());
        recalculer();
        return this;
    }

    public FeuillePresence faireAppelGlobal(StatutPresence statut, UUID par) {
        if (statut == null) {
            throw AttendanceException.invalide("statut requis pour l appel global");
        }
        presences.forEach(ligne -> {
            verifierModifiable(ligne);
            ligne.setStatut(statut);
            ligne.setSaisiePar(par);
            ligne.setSaisieLe(Instant.now());
        });
        recalculer();
        return this;
    }

    public FeuillePresence soumettre() {
        if (statut != StatutFeuille.BROUILLON && statut != StatutFeuille.CORRIGEE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutFeuille.SOUMISE.name());
        }
        this.statut = StatutFeuille.SOUMISE;
        this.soumiseLe = Instant.now();
        return this;
    }

    public FeuillePresence valider(UUID par) {
        if (statut != StatutFeuille.SOUMISE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutFeuille.VALIDEE.name());
        }
        this.statut = StatutFeuille.VALIDEE;
        this.valideePar = par;
        this.valideeLe = Instant.now();
        return this;
    }

    public FeuillePresence cloturer() {
        if (statut != StatutFeuille.VALIDEE) {
            throw AttendanceException.transitionInvalide(String.valueOf(statut), StatutFeuille.CLOTUREE.name());
        }
        this.statut = StatutFeuille.CLOTUREE;
        return this;
    }

    public BigDecimal getTauxPresence() {
        if (presences.isEmpty()) {
            return BigDecimal.ZERO;
        }
        long presents = presences.stream().filter(PresenceEleve::estPresent).count();
        return BigDecimal.valueOf(presents * 100.0)
                .divide(BigDecimal.valueOf(presences.size()), 2, RoundingMode.HALF_UP);
    }

    public int getNbAbsents() {
        return (int) presences.stream().filter(PresenceEleve::estAbsent).count();
    }

    public Optional<PresenceEleve> trouver(UUID eleveId) {
        return presences.stream().filter(p -> eleveId.equals(p.getEleveId())).findFirst();
    }

    private PresenceEleve ligne(UUID eleveId) {
        return trouver(eleveId).orElseGet(() -> {
            PresenceEleve ligne = new PresenceEleve();
            ligne.setEleveId(eleveId);
            presences.add(ligne);
            return ligne;
        });
    }

    private void verifierModifiable(PresenceEleve ligne) {
        if (statut == StatutFeuille.CLOTUREE) {
            throw AttendanceException.etatInvalide("Feuille cloturee : modification impossible");
        }
        if (statut == StatutFeuille.VALIDEE) {
            ligne.setModifieeApresValidation(true);
        }
    }

    private void recalculer() {
        this.effectifTheorique = presences.size();
        this.effectifPresent = (int) presences.stream().filter(PresenceEleve::estPresent).count();
    }

    public UUID getIdFeuille() { return idFeuille; }
    public void setIdFeuille(UUID idFeuille) { this.idFeuille = idFeuille; }
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
    public StatutFeuille getStatut() { return statut; }
    public void setStatut(StatutFeuille statut) { this.statut = statut; }
    public MethodeSaisie getMethode() { return methode; }
    public void setMethode(MethodeSaisie methode) { this.methode = methode; }
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
    public List<PresenceEleve> getPresences() { return presences; }
    public void setPresences(List<PresenceEleve> presences) {
        this.presences = presences == null ? new ArrayList<>() : new ArrayList<>(presences);
    }
}

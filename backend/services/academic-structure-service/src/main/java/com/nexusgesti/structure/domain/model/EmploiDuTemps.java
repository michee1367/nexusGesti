package com.nexusgesti.structure.domain.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmploiDuTemps {

    private UUID idEmploi;
    private UUID anneeId;
    private UUID classeId;
    private int version = 1;
    private Instant dateActivation;
    private boolean active;
    private boolean publie;
    private List<PlageEmploi> plages = new ArrayList<>();
    private List<ConflitEmploi> conflits = new ArrayList<>();

    public EmploiDuTemps publier() {
        this.active = true;
        this.publie = true;
        this.dateActivation = Instant.now();
        return this;
    }

    public EmploiDuTemps deplacer(UUID plageId, UUID creneauId, UUID salleId) {
        PlageEmploi plage = plages.stream()
                .filter(p -> p.getIdPlage().equals(plageId))
                .findFirst()
                .orElse(null);
        if (plage == null) {
            return this;
        }
        if (creneauId != null) {
            plage.setCreneauId(creneauId);
        }
        if (salleId != null) {
            plage.setSalleId(salleId);
        }
        return this;
    }

    public List<ConflitEmploi> detecterConflits() {
        List<ConflitEmploi> resultat = new ArrayList<>();
        for (int i = 0; i < plages.size(); i++) {
            for (int j = i + 1; j < plages.size(); j++) {
                PlageEmploi a = plages.get(i);
                PlageEmploi b = plages.get(j);
                if (a.getCreneauId() == null || !a.getCreneauId().equals(b.getCreneauId())) {
                    continue;
                }
                if (a.getSalleId() != null && a.getSalleId().equals(b.getSalleId())) {
                    resultat.add(new ConflitEmploi(TypeConflit.SALL_DOUBLEE, a.getIdPlage(), b.getIdPlage()));
                }
                if (a.getEnseignantId() != null && a.getEnseignantId().equals(b.getEnseignantId())) {
                    resultat.add(new ConflitEmploi(TypeConflit.ENSEIGNANT_DOUBLE, a.getIdPlage(), b.getIdPlage()));
                }
                if (a.getCoursId() != null && b.getCoursId() != null && !a.getCoursId().equals(b.getCoursId())) {
                    resultat.add(new ConflitEmploi(TypeConflit.ELEVES_DOUBLE, a.getIdPlage(), b.getIdPlage()));
                }
            }
        }
        this.conflits = resultat;
        return resultat;
    }

    public UUID getIdEmploi() { return idEmploi; }
    public void setIdEmploi(UUID idEmploi) { this.idEmploi = idEmploi; }
    public UUID getAnneeId() { return anneeId; }
    public void setAnneeId(UUID anneeId) { this.anneeId = anneeId; }
    public UUID getClasseId() { return classeId; }
    public void setClasseId(UUID classeId) { this.classeId = classeId; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public Instant getDateActivation() { return dateActivation; }
    public void setDateActivation(Instant dateActivation) { this.dateActivation = dateActivation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isPublie() { return publie; }
    public void setPublie(boolean publie) { this.publie = publie; }
    public List<PlageEmploi> getPlages() { return plages; }
    public void setPlages(List<PlageEmploi> plages) { this.plages = plages == null ? new ArrayList<>() : new ArrayList<>(plages); }
    public List<ConflitEmploi> getConflits() { return conflits; }
    public void setConflits(List<ConflitEmploi> conflits) { this.conflits = conflits == null ? new ArrayList<>() : new ArrayList<>(conflits); }
}

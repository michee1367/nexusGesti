package com.nexusgesti.structure.domain.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

public class Creneau {

    private UUID idCreneau;
    private JourSemaine jour;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private int ordre;

    public int getDureeMinutes() {
        if (heureDebut == null || heureFin == null) {
            return 0;
        }
        return (int) Duration.between(heureDebut, heureFin).toMinutes();
    }

    public UUID getIdCreneau() { return idCreneau; }
    public void setIdCreneau(UUID idCreneau) { this.idCreneau = idCreneau; }
    public JourSemaine getJour() { return jour; }
    public void setJour(JourSemaine jour) { this.jour = jour; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
}

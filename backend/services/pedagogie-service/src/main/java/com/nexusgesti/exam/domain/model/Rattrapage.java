package com.nexusgesti.exam.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Rattrapage {

    private UUID id;
    private UUID sessionOrigineId;
    private UUID sessionRattrapageId;
    private UUID eleveId;
    private List<UUID> epreuvesConcernees = new ArrayList<>();
    private UUID convocationId;
    private boolean nePasPresenter;
    private String motif;

    public Rattrapage inscrire() {
        this.nePasPresenter = false;
        return this;
    }

    public Rattrapage declareExempt(String motif) {
        this.nePasPresenter = true;
        this.motif = motif;
        return this;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSessionOrigineId() { return sessionOrigineId; }
    public void setSessionOrigineId(UUID sessionOrigineId) { this.sessionOrigineId = sessionOrigineId; }
    public UUID getSessionRattrapageId() { return sessionRattrapageId; }
    public void setSessionRattrapageId(UUID sessionRattrapageId) { this.sessionRattrapageId = sessionRattrapageId; }
    public UUID getEleveId() { return eleveId; }
    public void setEleveId(UUID eleveId) { this.eleveId = eleveId; }
    public List<UUID> getEpreuvesConcernees() { return epreuvesConcernees; }
    public void setEpreuvesConcernees(List<UUID> epreuvesConcernees) { this.epreuvesConcernees = epreuvesConcernees == null ? new ArrayList<>() : new ArrayList<>(epreuvesConcernees); }
    public UUID getConvocationId() { return convocationId; }
    public void setConvocationId(UUID convocationId) { this.convocationId = convocationId; }
    public boolean isNePasPresenter() { return nePasPresenter; }
    public void setNePasPresenter(boolean nePasPresenter) { this.nePasPresenter = nePasPresenter; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}

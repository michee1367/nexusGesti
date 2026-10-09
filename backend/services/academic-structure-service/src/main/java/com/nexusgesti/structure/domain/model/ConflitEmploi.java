package com.nexusgesti.structure.domain.model;

import java.util.UUID;

public class ConflitEmploi {

    private UUID idConflit;
    private UUID emploiId;
    private TypeConflit type;
    private UUID plageAId;
    private UUID plageBId;

    public ConflitEmploi() {
    }

    public ConflitEmploi(TypeConflit type, UUID plageAId, UUID plageBId) {
        this.idConflit = UUID.randomUUID();
        this.type = type;
        this.plageAId = plageAId;
        this.plageBId = plageBId;
    }

    public boolean estBloquant() {
        return type != TypeConflit.HORS_CYCLE;
    }

    public UUID getIdConflit() { return idConflit; }
    public void setIdConflit(UUID idConflit) { this.idConflit = idConflit; }
    public UUID getEmploiId() { return emploiId; }
    public void setEmploiId(UUID emploiId) { this.emploiId = emploiId; }
    public TypeConflit getType() { return type; }
    public void setType(TypeConflit type) { this.type = type; }
    public UUID getPlageAId() { return plageAId; }
    public void setPlageAId(UUID plageAId) { this.plageAId = plageAId; }
    public UUID getPlageBId() { return plageBId; }
    public void setPlageBId(UUID plageBId) { this.plageBId = plageBId; }
}

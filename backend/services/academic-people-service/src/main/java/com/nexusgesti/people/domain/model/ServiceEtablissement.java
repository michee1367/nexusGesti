package com.nexusgesti.people.domain.model;

import java.util.UUID;

public class ServiceEtablissement {

    private UUID id;
    private String code;
    private String libelle;
    private TypeService type;
    private UUID parentId;
    private UUID responsableId;
    private String email;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public TypeService getType() { return type; }
    public void setType(TypeService type) { this.type = type; }
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
    public UUID getResponsableId() { return responsableId; }
    public void setResponsableId(UUID responsableId) { this.responsableId = responsableId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

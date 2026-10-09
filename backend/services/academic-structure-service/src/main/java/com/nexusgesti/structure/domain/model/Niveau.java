package com.nexusgesti.structure.domain.model;

import java.util.UUID;

public class Niveau {

    private UUID idNiveau;
    private String code;
    private String libelle;
    private int rang;
    private TypeCycle cycle;
    private int ordre;
    private UUID niveauSuperieurId;

    public boolean estPremierDuCycle() {
        return niveauSuperieurId == null;
    }

    public UUID getIdNiveau() { return idNiveau; }
    public void setIdNiveau(UUID idNiveau) { this.idNiveau = idNiveau; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public TypeCycle getCycle() { return cycle; }
    public void setCycle(TypeCycle cycle) { this.cycle = cycle; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public UUID getNiveauSuperieurId() { return niveauSuperieurId; }
    public void setNiveauSuperieurId(UUID niveauSuperieurId) { this.niveauSuperieurId = niveauSuperieurId; }
}

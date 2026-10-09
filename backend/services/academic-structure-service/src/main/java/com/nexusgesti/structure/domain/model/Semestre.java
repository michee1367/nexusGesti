package com.nexusgesti.structure.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Semestre {

    private UUID idSemestre;
    private String code;
    private String libelle;
    private int rang;
    private UUID periodeId;
    private BigDecimal totalCreditsAttendu;

    public UUID getIdSemestre() { return idSemestre; }
    public void setIdSemestre(UUID idSemestre) { this.idSemestre = idSemestre; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public int getRang() { return rang; }
    public void setRang(int rang) { this.rang = rang; }
    public UUID getPeriodeId() { return periodeId; }
    public void setPeriodeId(UUID periodeId) { this.periodeId = periodeId; }
    public BigDecimal getTotalCreditsAttendu() { return totalCreditsAttendu; }
    public void setTotalCreditsAttendu(BigDecimal totalCreditsAttendu) { this.totalCreditsAttendu = totalCreditsAttendu; }
}

package com.nexusgesti.fees.domain.model;

import java.util.UUID;

public class PayementEspeces {

    private UUID caja;
    private String numeroRecepisse;
    private UUID agent;
    private boolean contresigne;

    public UUID getCaja() { return caja; }
    public void setCaja(UUID caja) { this.caja = caja; }
    public String getNumeroRecepisse() { return numeroRecepisse; }
    public void setNumeroRecepisse(String numeroRecepisse) { this.numeroRecepisse = numeroRecepisse; }
    public UUID getAgent() { return agent; }
    public void setAgent(UUID agent) { this.agent = agent; }
    public boolean isContresigne() { return contresigne; }
    public void setContresigne(boolean contresigne) { this.contresigne = contresigne; }
}

package com.nexusgesti.identity.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class JournalAccesUtilisateur {
    private UUID id;
    private UUID utilisateur;
    private String action;
    private String cible;
    private Map<String, String> details;
    private Instant horodatage;
    private String adresseIp;
    private String resultat;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUtilisateur() { return utilisateur; }
    public void setUtilisateur(UUID utilisateur) { this.utilisateur = utilisateur; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }
    public Map<String, String> getDetails() { return details; }
    public void setDetails(Map<String, String> details) { this.details = details; }
    public Instant getHorodatage() { return horodatage; }
    public void setHorodatage(Instant horodatage) { this.horodatage = horodatage; }
    public String getAdresseIp() { return adresseIp; }
    public void setAdresseIp(String adresseIp) { this.adresseIp = adresseIp; }
    public String getResultat() { return resultat; }
    public void setResultat(String resultat) { this.resultat = resultat; }
}

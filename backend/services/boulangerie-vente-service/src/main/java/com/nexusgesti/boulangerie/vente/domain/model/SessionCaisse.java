package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionCaisse {

    private UUID idSession;
    private LocalDate date;
    private BigDecimal fondInitial = BigDecimal.ZERO;
    private BigDecimal totalEncaisse = BigDecimal.ZERO;
    private BigDecimal ecart;
    private UUID ouvertePar;
    private boolean cloturee;
    private LocalDateTime dateCloture;

    public SessionCaisse() {
    }

    public SessionCaisse(LocalDate date, BigDecimal fondInitial, UUID ouvertePar) {
        this.idSession = UUID.randomUUID();
        this.date = date;
        this.fondInitial = fondInitial == null ? BigDecimal.ZERO : fondInitial;
        this.ouvertePar = ouvertePar;
    }

    public void encaisser(BigDecimal montant) {
        if (cloturee) {
            throw VenteException.etatInvalide("La session de caisse est cloturee");
        }
        if (montant == null || montant.signum() < 0) {
            throw VenteException.invalide("Montant d encaissement invalide");
        }
        this.totalEncaisse = this.totalEncaisse.add(montant);
    }

    public void cloturer(BigDecimal montantCompte) {
        if (cloturee) {
            throw VenteException.etatInvalide("La session de caisse est deja cloturee");
        }
        if (montantCompte == null) {
            throw VenteException.invalide("Le montant compte est obligatoire pour cloturer");
        }
        BigDecimal attendu = (fondInitial == null ? BigDecimal.ZERO : fondInitial)
                .add(totalEncaisse == null ? BigDecimal.ZERO : totalEncaisse);
        this.ecart = montantCompte.subtract(attendu);
        this.dateCloture = LocalDateTime.now();
        this.cloturee = true;
    }

    public boolean estOuverte() {
        return !cloturee;
    }

    public UUID getIdSession() {
        return idSession;
    }

    public void setIdSession(UUID idSession) {
        this.idSession = idSession;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getFondInitial() {
        return fondInitial;
    }

    public void setFondInitial(BigDecimal fondInitial) {
        this.fondInitial = fondInitial;
    }

    public BigDecimal getTotalEncaisse() {
        return totalEncaisse;
    }

    public void setTotalEncaisse(BigDecimal totalEncaisse) {
        this.totalEncaisse = totalEncaisse;
    }

    public BigDecimal getEcart() {
        return ecart;
    }

    public void setEcart(BigDecimal ecart) {
        this.ecart = ecart;
    }

    public UUID getOuvertePar() {
        return ouvertePar;
    }

    public void setOuvertePar(UUID ouvertePar) {
        this.ouvertePar = ouvertePar;
    }

    public boolean isCloturee() {
        return cloturee;
    }

    public void setCloturee(boolean cloturee) {
        this.cloturee = cloturee;
    }

    public LocalDateTime getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(LocalDateTime dateCloture) {
        this.dateCloture = dateCloture;
    }
}

package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Vente {

    private UUID idVente;
    private String numeroTicket;
    private LocalDateTime date;
    private UUID sessionId;
    private CanalVente canal = CanalVente.COMPTOIR;
    private String clientCode;
    private BigDecimal montantHT = BigDecimal.ZERO;
    private BigDecimal montantTVA = BigDecimal.ZERO;
    private BigDecimal montantTTC = BigDecimal.ZERO;
    private ModePaiementCaisse modePaiement = ModePaiementCaisse.ESPECES;
    private int pointsFideliteGagnes;
    private String cleIdempotence;
    private StatutVente statut = StatutVente.EN_COURS;
    private String motifAnnulation;
    private List<LigneVente> lignes = new ArrayList<>();
    private List<PaiementVente> paiements = new ArrayList<>();

    public Vente() {
    }

    public void verifierCreation() {
        if (sessionId == null) {
            throw VenteException.invalide("La session de caisse est obligatoire pour une vente");
        }
        if (cleIdempotence == null || cleIdempotence.isBlank()) {
            throw VenteException.invalide("La cle d idempotence est obligatoire pour une vente");
        }
        if (lignes.isEmpty()) {
            throw VenteException.invalide("Une vente doit porter au moins une ligne");
        }
    }

    public void ajouterLigne(LigneVente ligne) {
        verifierModifiable();
        if (ligne == null) {
            throw VenteException.invalide("Ligne de vente manquante");
        }
        ligne.verifier();
        ligne.calculerMontants();
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        lignes.add(ligne);
        calculerMontants();
    }

    public void ajouterPaiement(PaiementVente paiement) {
        verifierModifiable();
        if (paiement == null) {
            throw VenteException.invalide("Paiement manquant");
        }
        paiement.finaliser(montantTTC);
        if (paiement.getId() == null) {
            paiement.setId(UUID.randomUUID());
        }
        if (paiements.isEmpty()) {
            this.modePaiement = paiement.getMode();
        }
        paiements.add(paiement);
    }

    public void calculerMontants() {
        BigDecimal ht = BigDecimal.ZERO;
        BigDecimal tva = BigDecimal.ZERO;
        for (LigneVente ligne : lignes) {
            ht = ht.add(ligne.getMontantHT());
            tva = tva.add(ligne.getMontantTVA());
        }
        this.montantHT = ht.setScale(2, RoundingMode.HALF_UP);
        this.montantTVA = tva.setScale(2, RoundingMode.HALF_UP);
        this.montantTTC = ht.add(tva).setScale(2, RoundingMode.HALF_UP);
    }

    public void valider() {
        if (statut != StatutVente.EN_COURS) {
            throw VenteException.transitionInvalide(statut.name(), StatutVente.VALIDEE.name());
        }
        if (lignes.isEmpty()) {
            throw VenteException.etatInvalide("Une vente sans ligne ne peut pas etre validee");
        }
        if (!estReglee()) {
            throw VenteException.etatInvalide(
                    "Le reglement (" + getTotalPaiements().toPlainString()
                            + ") ne couvre pas le montant TTC (" + montantTTC.toPlainString() + ")");
        }
        this.statut = StatutVente.VALIDEE;
    }

    public void annuler(String motif) {
        if (statut != StatutVente.VALIDEE) {
            throw VenteException.transitionInvalide(statut.name(), StatutVente.ANNULEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw VenteException.invalide("Le motif d annulation est obligatoire");
        }
        this.motifAnnulation = motif;
        this.statut = StatutVente.ANNULEE;
    }

    public void rembourser() {
        if (statut != StatutVente.ANNULEE) {
            throw VenteException.transitionInvalide(statut.name(), StatutVente.REMBOURSEE.name());
        }
        this.statut = StatutVente.REMBOURSEE;
    }

    public boolean estModifiable() {
        return statut == StatutVente.EN_COURS;
    }

    public void verifierModifiable() {
        if (!estModifiable()) {
            throw VenteException.etatInvalide("La vente " + statut + " n accepte plus de modification");
        }
    }

    public boolean estReglee() {
        return getTotalPaiements().compareTo(montantTTC) >= 0;
    }

    public BigDecimal getTotalPaiements() {
        BigDecimal total = BigDecimal.ZERO;
        for (PaiementVente paiement : paiements) {
            total = total.add(paiement.getMontant());
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMontantNet() {
        return montantTTC;
    }

    public UUID getIdVente() {
        return idVente;
    }

    public void setIdVente(UUID idVente) {
        this.idVente = idVente;
    }

    public String getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public CanalVente getCanal() {
        return canal;
    }

    public void setCanal(CanalVente canal) {
        this.canal = canal;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public BigDecimal getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(BigDecimal montantHT) {
        this.montantHT = montantHT;
    }

    public BigDecimal getMontantTVA() {
        return montantTVA;
    }

    public void setMontantTVA(BigDecimal montantTVA) {
        this.montantTVA = montantTVA;
    }

    public BigDecimal getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(BigDecimal montantTTC) {
        this.montantTTC = montantTTC;
    }

    public ModePaiementCaisse getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(ModePaiementCaisse modePaiement) {
        this.modePaiement = modePaiement;
    }

    public int getPointsFideliteGagnes() {
        return pointsFideliteGagnes;
    }

    public void setPointsFideliteGagnes(int pointsFideliteGagnes) {
        this.pointsFideliteGagnes = pointsFideliteGagnes;
    }

    public String getCleIdempotence() {
        return cleIdempotence;
    }

    public void setCleIdempotence(String cleIdempotence) {
        this.cleIdempotence = cleIdempotence;
    }

    public StatutVente getStatut() {
        return statut;
    }

    public void setStatut(StatutVente statut) {
        this.statut = statut;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public List<LigneVente> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneVente> lignes) {
        this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes);
    }

    public List<PaiementVente> getPaiements() {
        return paiements;
    }

    public void setPaiements(List<PaiementVente> paiements) {
        this.paiements = paiements == null ? new ArrayList<>() : new ArrayList<>(paiements);
    }
}

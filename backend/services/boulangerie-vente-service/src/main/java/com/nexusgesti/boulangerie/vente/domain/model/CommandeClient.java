package com.nexusgesti.boulangerie.vente.domain.model;

import com.nexusgesti.boulangerie.vente.domain.VenteException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandeClient {

    private UUID idCommande;
    private String numero;
    private String clientCode;
    private LocalDate dateCommande;
    private LocalDate dateRetrait;
    private BigDecimal montantHT = BigDecimal.ZERO;
    private BigDecimal montantTTC = BigDecimal.ZERO;
    private BigDecimal acompte = BigDecimal.ZERO;
    private StatutCommandeClient statut = StatutCommandeClient.BROUILLON;
    private String motifAnnulation;
    private List<LigneCommandeClient> lignes = new ArrayList<>();
    private List<Acompte> acomptes = new ArrayList<>();

    public CommandeClient() {
    }

    public CommandeClient(String clientCode, LocalDate dateCommande, LocalDate dateRetrait) {
        this.idCommande = UUID.randomUUID();
        this.clientCode = clientCode;
        this.dateCommande = dateCommande;
        this.dateRetrait = dateRetrait;
    }

    public void ajouterLigne(LigneCommandeClient ligne) {
        verifierModifiable();
        if (ligne == null) {
            throw VenteException.invalide("Ligne de commande manquante");
        }
        ligne.verifier();
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        lignes.add(ligne);
        calculerMontants();
    }

    public void calculerMontants() {
        BigDecimal total = BigDecimal.ZERO;
        for (LigneCommandeClient ligne : lignes) {
            total = total.add(ligne.getMontant());
        }
        this.montantHT = total.setScale(2, RoundingMode.HALF_UP);
        this.montantTTC = total.setScale(2, RoundingMode.HALF_UP);
    }

    public void confirmer() {
        if (statut != StatutCommandeClient.BROUILLON) {
            throw VenteException.transitionInvalide(statut.name(), StatutCommandeClient.CONFIRMEE.name());
        }
        if (lignes.isEmpty()) {
            throw VenteException.etatInvalide("Une commande sans ligne ne peut pas etre confirmee");
        }
        this.statut = StatutCommandeClient.CONFIRMEE;
    }

    public void encaisserAcompte(BigDecimal montant, ModePaiementCaisse mode, String reference) {
        if (statut == StatutCommandeClient.LIVREE || statut == StatutCommandeClient.ANNULEE) {
            throw VenteException.etatInvalide(
                    "Acompte impossible sur une commande " + statut);
        }
        if (montant == null || montant.signum() <= 0) {
            throw VenteException.invalide("L acompte doit etre strictement positif");
        }
        Acompte acompteAjoute = new Acompte(montant, mode, reference);
        acomptes.add(acompteAjoute);
        this.acompte = this.acompte.add(montant).setScale(2, RoundingMode.HALF_UP);
    }

    public void demarrerProduction() {
        if (statut != StatutCommandeClient.CONFIRMEE) {
            throw VenteException.transitionInvalide(statut.name(), StatutCommandeClient.EN_PRODUCTION.name());
        }
        this.statut = StatutCommandeClient.EN_PRODUCTION;
    }

    public void marquerPrete() {
        if (statut != StatutCommandeClient.EN_PRODUCTION) {
            throw VenteException.transitionInvalide(statut.name(), StatutCommandeClient.PRETE.name());
        }
        this.statut = StatutCommandeClient.PRETE;
    }

    public void retirer() {
        if (statut != StatutCommandeClient.PRETE) {
            throw VenteException.transitionInvalide(statut.name(), StatutCommandeClient.LIVREE.name());
        }
        if (getSoldeDu().signum() > 0) {
            throw VenteException.etatInvalide(
                    "Le solde du est de " + getSoldeDu().toPlainString() + " : retrait bloque tant que non regle");
        }
        this.statut = StatutCommandeClient.LIVREE;
    }

    public void annuler(String motif) {
        if (statut == StatutCommandeClient.LIVREE || statut == StatutCommandeClient.ANNULEE) {
            throw VenteException.transitionInvalide(statut.name(), StatutCommandeClient.ANNULEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw VenteException.invalide("Le motif d annulation est obligatoire");
        }
        this.motifAnnulation = motif;
        this.statut = StatutCommandeClient.ANNULEE;
    }

    public boolean estModifiable() {
        return statut == StatutCommandeClient.BROUILLON;
    }

    public void verifierModifiable() {
        if (!estModifiable()) {
            throw VenteException.etatInvalide(
                    "La commande " + statut + " n accepte plus de modification");
        }
    }

    public BigDecimal getSoldeDu() {
        return montantTTC.subtract(acompte).setScale(2, RoundingMode.HALF_UP);
    }

    public UUID getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(UUID idCommande) {
        this.idCommande = idCommande;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getClientCode() {
        return clientCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public LocalDate getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDate dateCommande) {
        this.dateCommande = dateCommande;
    }

    public LocalDate getDateRetrait() {
        return dateRetrait;
    }

    public void setDateRetrait(LocalDate dateRetrait) {
        this.dateRetrait = dateRetrait;
    }

    public BigDecimal getMontantHT() {
        return montantHT;
    }

    public void setMontantHT(BigDecimal montantHT) {
        this.montantHT = montantHT;
    }

    public BigDecimal getMontantTTC() {
        return montantTTC;
    }

    public void setMontantTTC(BigDecimal montantTTC) {
        this.montantTTC = montantTTC;
    }

    public BigDecimal getAcompte() {
        return acompte;
    }

    public void setAcompte(BigDecimal acompte) {
        this.acompte = acompte;
    }

    public StatutCommandeClient getStatut() {
        return statut;
    }

    public void setStatut(StatutCommandeClient statut) {
        this.statut = statut;
    }

    public String getMotifAnnulation() {
        return motifAnnulation;
    }

    public void setMotifAnnulation(String motifAnnulation) {
        this.motifAnnulation = motifAnnulation;
    }

    public List<LigneCommandeClient> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeClient> lignes) {
        this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes);
    }

    public List<Acompte> getAcomptes() {
        return acomptes;
    }

    public void setAcomptes(List<Acompte> acomptes) {
        this.acomptes = acomptes == null ? new ArrayList<>() : new ArrayList<>(acomptes);
    }
}

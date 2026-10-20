package com.nexusgesti.boulangerie.approvisionnement.domain.model;

import com.nexusgesti.boulangerie.approvisionnement.domain.ApprovisionnementException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandeAchat {

    private UUID idCommande;
    private String numero;
    private String fournisseurTiersCode;
    private LocalDate date;
    private LocalDate dateLivraisonPrevue;
    private StatutCommandeAchat statut = StatutCommandeAchat.BROUILLON;
    private BigDecimal montantHT = BigDecimal.ZERO;
    private BigDecimal montantTVA = BigDecimal.ZERO;
    private BigDecimal montantTTC = BigDecimal.ZERO;
    private String devise = "USD";
    private String motifAnnulation;
    private List<LigneCommandeAchat> lignes = new ArrayList<>();

    public CommandeAchat valider() {
        if (statut != StatutCommandeAchat.BROUILLON) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutCommandeAchat.VALIDEE.name());
        }
        if (fournisseurTiersCode == null || fournisseurTiersCode.isBlank()) {
            throw ApprovisionnementException.invalide("Le fournisseur est obligatoire");
        }
        if (date == null) {
            throw ApprovisionnementException.invalide("La date de la commande est obligatoire");
        }
        if (lignes.isEmpty()) {
            throw ApprovisionnementException.invalide("Une commande doit porter au moins une ligne");
        }
        lignes.forEach(LigneCommandeAchat::verifier);
        calculerMontants();
        this.statut = StatutCommandeAchat.VALIDEE;
        return this;
    }

    public CommandeAchat envoyer() {
        if (statut != StatutCommandeAchat.VALIDEE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutCommandeAchat.ENVOYEE.name());
        }
        this.statut = StatutCommandeAchat.ENVOYEE;
        return this;
    }

    public CommandeAchat marquerPartielle() {
        if (statut != StatutCommandeAchat.ENVOYEE && statut != StatutCommandeAchat.PARTIELLE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutCommandeAchat.PARTIELLE.name());
        }
        this.statut = StatutCommandeAchat.PARTIELLE;
        return this;
    }

    public CommandeAchat marquerReceptionnee() {
        if (statut != StatutCommandeAchat.ENVOYEE && statut != StatutCommandeAchat.PARTIELLE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutCommandeAchat.RECEPTIONNEE.name());
        }
        this.statut = StatutCommandeAchat.RECEPTIONNEE;
        return this;
    }

    public CommandeAchat annuler(String motif) {
        if (statut == StatutCommandeAchat.RECEPTIONNEE
                || statut == StatutCommandeAchat.FACTUREE
                || statut == StatutCommandeAchat.ANNULEE) {
            throw ApprovisionnementException.transitionInvalide(statut.name(), StatutCommandeAchat.ANNULEE.name());
        }
        if (motif == null || motif.isBlank()) {
            throw ApprovisionnementException.invalide("Le motif d annulation est obligatoire");
        }
        this.statut = StatutCommandeAchat.ANNULEE;
        this.motifAnnulation = motif;
        return this;
    }

    public boolean estRecevable() {
        return statut == StatutCommandeAchat.ENVOYEE || statut == StatutCommandeAchat.PARTIELLE;
    }

    public void ajouterLigne(LigneCommandeAchat ligne) {
        if (ligne == null) {
            return;
        }
        ligne.setCommandeId(idCommande);
        if (ligne.getId() == null) {
            ligne.setId(UUID.randomUUID());
        }
        this.lignes.add(ligne);
    }

    public LigneCommandeAchat trouverLigne(String articleCode) {
        return lignes.stream()
                .filter(l -> l.getArticleCode() != null && l.getArticleCode().equals(articleCode))
                .findFirst()
                .orElseThrow(() -> ApprovisionnementException.notFoundByCode("Article de la commande", articleCode));
    }

    public void calculerMontants() {
        BigDecimal ht = BigDecimal.ZERO;
        BigDecimal tva = BigDecimal.ZERO;
        for (LigneCommandeAchat ligne : lignes) {
            ht = ht.add(ligne.getMontantHT());
            tva = tva.add(ligne.getMontantTVA());
        }
        this.montantHT = ht.setScale(2, RoundingMode.HALF_UP);
        this.montantTVA = tva.setScale(2, RoundingMode.HALF_UP);
        this.montantTTC = ht.add(tva).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getResteARecevoir() {
        BigDecimal reste = BigDecimal.ZERO;
        for (LigneCommandeAchat ligne : lignes) {
            reste = reste.add(ligne.getResteARecevoir());
        }
        return reste;
    }

    public UUID getIdCommande() { return idCommande; }
    public void setIdCommande(UUID idCommande) { this.idCommande = idCommande; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getFournisseurTiersCode() { return fournisseurTiersCode; }
    public void setFournisseurTiersCode(String fournisseurTiersCode) { this.fournisseurTiersCode = fournisseurTiersCode; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDate getDateLivraisonPrevue() { return dateLivraisonPrevue; }
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) { this.dateLivraisonPrevue = dateLivraisonPrevue; }
    public StatutCommandeAchat getStatut() { return statut; }
    public void setStatut(StatutCommandeAchat statut) {
        this.statut = statut == null ? StatutCommandeAchat.BROUILLON : statut;
    }
    public BigDecimal getMontantHT() { return montantHT; }
    public void setMontantHT(BigDecimal montantHT) { this.montantHT = montantHT == null ? BigDecimal.ZERO : montantHT; }
    public BigDecimal getMontantTVA() { return montantTVA; }
    public void setMontantTVA(BigDecimal montantTVA) { this.montantTVA = montantTVA == null ? BigDecimal.ZERO : montantTVA; }
    public BigDecimal getMontantTTC() { return montantTTC; }
    public void setMontantTTC(BigDecimal montantTTC) { this.montantTTC = montantTTC == null ? BigDecimal.ZERO : montantTTC; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) {
        this.devise = (devise == null || devise.isBlank()) ? "USD" : devise;
    }
    public String getMotifAnnulation() { return motifAnnulation; }
    public void setMotifAnnulation(String motifAnnulation) { this.motifAnnulation = motifAnnulation; }
    public List<LigneCommandeAchat> getLignes() { return lignes; }
    public void setLignes(List<LigneCommandeAchat> lignes) {
        this.lignes = lignes == null ? new ArrayList<>() : new ArrayList<>(lignes);
    }
}

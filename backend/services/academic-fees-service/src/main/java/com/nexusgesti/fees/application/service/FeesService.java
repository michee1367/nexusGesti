package com.nexusgesti.fees.application.service;

import com.nexusgesti.common.idempotency.IdempotencyService;
import com.nexusgesti.fees.domain.FeesException;
import com.nexusgesti.fees.domain.model.*;
import com.nexusgesti.fees.domain.port.in.FeesUseCase;
import com.nexusgesti.fees.domain.port.out.FeesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class FeesService implements FeesUseCase {

    private final FeesRepository repository;
    private final IdempotencyService idempotency;

    public FeesService(FeesRepository repository, IdempotencyService idempotency) {
        this.repository = repository;
        this.idempotency = idempotency;
    }

    // ---------- GRILLE TARIFAIRE ----------

    @Override
    public List<GrilleTarifaire> listGrilles(UUID anneeId, String profil) {
        return repository.listGrilles(anneeId, profil);
    }

    @Override
    public GrilleTarifaire getGrille(UUID id) {
        return repository.findGrille(id)
                .orElseThrow(() -> FeesException.notFound("GrilleTarifaire", id));
    }

    @Override
    public GrilleTarifaire getGrilleApplicable(UUID anneeId, String profil) {
        return repository.listGrilles(anneeId, profil).stream()
                .filter(g -> g.isActive() && g.getStatut() == StatutGrille.ACTIVE)
                .findFirst()
                .orElseThrow(() -> FeesException.conflit("NOT_FOUND", "Aucune grille tarifaire applicable"));
    }

    @Override
    @Transactional
    public GrilleTarifaire creerGrille(GrilleTarifaire grille) {
        if (grille.getCode() == null || grille.getCode().isBlank()) {
            throw FeesException.invalide("code obligatoire");
        }
        if (grille.getVersion() == null) {
            grille.setVersion(1);
        }
        if (grille.getStatut() == null) {
            grille.setStatut(StatutGrille.BROUILLON);
        }
        return repository.saveGrille(grille);
    }

    @Override
    @Transactional
    public GrilleTarifaire activerGrille(UUID id) {
        GrilleTarifaire grille = getGrille(id);
        grille.activer();
        return repository.saveGrille(grille);
    }

    @Override
    @Transactional
    public GrilleTarifaire figerGrille(UUID id) {
        GrilleTarifaire grille = getGrille(id);
        grille.figer();
        return repository.saveGrille(grille);
    }

    @Override
    @Transactional
    public GrilleTarifaire archiverGrille(UUID id) {
        GrilleTarifaire grille = getGrille(id);
        grille.archiver();
        return repository.saveGrille(grille);
    }

    @Override
    @Transactional
    public GrilleTarifaire ajouterLigne(UUID grilleId, LigneGrille ligne) {
        GrilleTarifaire grille = getGrille(grilleId);
        grille.verifierModifiable();
        grille.ajouterLigne(ligne);
        return repository.saveGrille(grille);
    }

    @Override
    @Transactional
    public GrilleTarifaire ajouterReduction(UUID grilleId, Reduction reduction) {
        GrilleTarifaire grille = getGrille(grilleId);
        grille.verifierModifiable();
        grille.ajouterReduction(reduction);
        return repository.saveGrille(grille);
    }

    // ---------- AVIS DE PAIEMENT ----------

    @Override
    public List<AvisPaiement> listAvis(UUID eleveId, UUID anneeId) {
        return repository.listAvis(eleveId, anneeId);
    }

    @Override
    public AvisPaiement getAvis(UUID id) {
        return repository.findAvis(id)
                .orElseThrow(() -> FeesException.notFound("AvisPaiement", id));
    }

    @Override
    @Transactional
    public AvisPaiement creerAvis(AvisPaiement avis) {
        if (avis.getEleve() == null) {
            throw FeesException.invalide("eleve obligatoire");
        }
        if (avis.getStatut() == null) {
            avis.setStatut(StatutAvis.BROUILLON);
        }
        for (LigneFrais ligne : avis.getLignes()) {
            ligne.calculerNet();
            if (ligne.getEcheances() == null || ligne.getEcheances().isEmpty()) {
                ligne.genererEcheances();
            }
        }
        avis.recalculerSolde();
        return repository.saveAvis(avis);
    }

    @Override
    @Transactional
    public AvisPaiement validerAvis(UUID id) {
        AvisPaiement avis = getAvis(id);
        avis.valider();
        return repository.saveAvis(avis);
    }

    @Override
    @Transactional
    public AvisPaiement emettreAvis(UUID id) {
        AvisPaiement avis = getAvis(id);
        avis.emettre();
        return repository.saveAvis(avis);
    }

    @Override
    @Transactional
    public AvisPaiement annulerAvis(UUID id, String motif) {
        AvisPaiement avis = getAvis(id);
        avis.annuler(motif);
        return repository.saveAvis(avis);
    }

    @Override
    @Transactional
    public AvisPaiement appliquerRemise(UUID id, UUID reductionId, ContexteReduction contexte) {
        AvisPaiement avis = getAvis(id);
        if (avis.getGrilleId() == null) {
            throw FeesException.etatInvalide("Avis sans grille tarifaire : remise impossible");
        }
        GrilleTarifaire grille = getGrille(avis.getGrilleId());
        Reduction reduction = grille.trouverReduction(reductionId);
        BigDecimal montant = reduction.appliquer(avis.getMontantBrut(), contexte);
        avis.appliquerRemise(reductionId, montant);
        return repository.saveAvis(avis);
    }

    // ---------- PAIEMENT ----------

    @Override
    public List<Paiement> listPaiements(UUID eleveId, UUID avisId) {
        return repository.listPaiements(eleveId, avisId);
    }

    @Override
    public Paiement getPaiement(UUID id) {
        return repository.findPaiement(id)
                .orElseThrow(() -> FeesException.notFound("Paiement", id));
    }

    @Override
    @Transactional
    public Paiement creerPaiement(Paiement paiement, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw FeesException.invalide("Idempotency-Key requise");
        }
        return idempotency.getCachedResponse(idempotencyKey, String.class)
                .map(UUID::fromString)
                .map(this::getPaiement)
                .orElseGet(() -> creerPaiementNouveau(paiement, idempotencyKey));
    }

    private Paiement creerPaiementNouveau(Paiement paiement, String idempotencyKey) {
        if (!idempotency.tryAcquireLock(idempotencyKey)) {
            throw FeesException.conflit("IDEMPOTENCE_EN_COURS", "Requete idempotente deja en cours");
        }
        try {
            if (paiement.getMontant() == null || paiement.getMontant().signum() <= 0) {
                throw FeesException.invalide("montant strictement positif requis");
            }
            if (paiement.getDate() == null) {
                paiement.setDate(LocalDate.now());
            }
            if (paiement.getStatut() == null) {
                paiement.setStatut(StatutPaiement.EN_ATTENTE);
            }
            Paiement saved = repository.savePaiement(paiement);
            idempotency.storeResponse(idempotencyKey, saved.getIdPaiement().toString());
            return saved;
        } finally {
            idempotency.releaseLock(idempotencyKey);
        }
    }

    @Override
    @Transactional
    public Paiement validerPaiement(UUID id) {
        Paiement paiement = getPaiement(id);
        paiement.valider();
        BigDecimal reste = paiement.getMontant();
        int ordre = 0;
        BigDecimal totalImpute = BigDecimal.ZERO;
        AvisPaiement avis = paiement.getAvis() == null ? null : getAvis(paiement.getAvis());
        if (avis != null) {
            for (LigneFrais ligne : avis.getLignes()) {
                for (Echeance echeance : ligne.getEcheances()) {
                    if (echeance.getSolde().signum() <= 0) {
                        continue;
                    }
                    BigDecimal montant = reste.min(echeance.getSolde());
                    echeance.enregistrerReglement(montant, paiement.getDate() == null ? LocalDate.now() : paiement.getDate());
                    Imputation imputation = new Imputation();
                    imputation.setEcheanceId(echeance.getId());
                    imputation.setMontant(montant);
                    imputation.setDateImputation(paiement.getDate() == null ? LocalDate.now() : paiement.getDate());
                    imputation.setOrdre(ordre++);
                    paiement.ajouterImputation(imputation);
                    totalImpute = totalImpute.add(montant);
                    reste = reste.subtract(montant);
                    if (reste.signum() <= 0) {
                        break;
                    }
                }
                if (reste.signum() <= 0) {
                    break;
                }
            }
            avis.enregistrerReglement(totalImpute);
            repository.saveAvis(avis);
        }
        paiement.setResteARembourser(reste.signum() > 0 ? reste : BigDecimal.ZERO);
        if (paiement.getCouponGenere() == null && avis != null && avis.getAnnee() != null) {
            genererCoupon(paiement, avis);
        }
        return repository.savePaiement(paiement);
    }

    private void genererCoupon(Paiement paiement, AvisPaiement avis) {
        long sequence = repository.countCouponsAnnee(avis.getAnnee()) + 1;
        CouponPaiement coupon = new CouponPaiement();
        coupon.setId(UUID.randomUUID());
        coupon.setAnnee(avis.getAnnee());
        coupon.setEleve(paiement.getEleve());
        coupon.setPaiement(paiement.getIdPaiement());
        coupon.setAvis(avis.getIdAvis());
        coupon.setDateEdition(LocalDate.now());
        coupon.setMontant(paiement.getMontant().subtract(paiement.getResteARembourser()));
        coupon.setLibelle(paiement.getMoyen() != null ? "Paiement " + paiement.getMoyen().name() : "Paiement de frais");
        coupon.setMoyen(paiement.getMoyen());
        coupon.setDevise(avis.getDevise());
        coupon.setMention("COUPON DE PAIEMENT - NON CODIFIABLE");
        coupon.setUrlQr("/api/v1/ped/coupons/" + coupon.getId());
        coupon.setCodeBarre(paiement.getReference());
        coupon.setStatut(StatutCoupon.A_TIRER);
        coupon.setSequence((int) sequence);
        coupon.setNumero(String.format("COUPON-PAIEMENT-%d-%05d", LocalDate.now().getYear(), sequence));
        paiement.setCouponGenere(coupon.getId());
        repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public Paiement rejeterPaiement(UUID id, String motif) {
        Paiement paiement = getPaiement(id);
        paiement.rejeter(motif);
        return repository.savePaiement(paiement);
    }

    @Override
    @Transactional
    public Paiement annulerPaiement(UUID id, String motif) {
        Paiement paiement = getPaiement(id);
        paiement.annuler(motif);
        return repository.savePaiement(paiement);
    }

    // ---------- COUPON ----------

    @Override
    public List<CouponPaiement> listCoupons(UUID eleveId, UUID anneeId) {
        return repository.listCoupons(eleveId, anneeId);
    }

    @Override
    public CouponPaiement getCoupon(UUID id) {
        return repository.findCoupon(id)
                .orElseThrow(() -> FeesException.notFound("CouponPaiement", id));
    }

    @Override
    @Transactional
    public CouponPaiement tirerCoupon(UUID id) {
        CouponPaiement coupon = getCoupon(id);
        coupon.tirer();
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponPaiement remettreCoupon(UUID id, UUID destinataire) {
        CouponPaiement coupon = getCoupon(id);
        coupon.remettre(destinataire);
        return repository.saveCoupon(coupon);
    }

    @Override
    @Transactional
    public CouponPaiement annulerCoupon(UUID id, String motif) {
        CouponPaiement coupon = getCoupon(id);
        coupon.annuler(motif);
        return repository.saveCoupon(coupon);
    }

    @Override
    public boolean verifierCoupon(UUID id) {
        return getCoupon(id).verifierEmpreinte();
    }

    // ---------- JOURNALIER ----------

    @Override
    public List<Journalier> listJournaliers(LocalDate date, UUID site) {
        return repository.listJournaliers(date, site);
    }

    @Override
    public Journalier getJournalier(UUID id) {
        return repository.findJournalier(id)
                .orElseThrow(() -> FeesException.notFound("Journalier", id));
    }

    @Override
    @Transactional
    public Journalier ouvrirJournalier(Journalier journalier) {
        if (journalier.getDate() == null) {
            journalier.setDate(LocalDate.now());
        }
        if (journalier.getStatut() == null) {
            journalier.setStatut("OUVERT");
        }
        return repository.saveJournalier(journalier);
    }

    @Override
    @Transactional
    public Journalier cloturerJournalier(UUID id) {
        Journalier journalier = getJournalier(id);
        journalier.cloturer();
        return repository.saveJournalier(journalier);
    }
}
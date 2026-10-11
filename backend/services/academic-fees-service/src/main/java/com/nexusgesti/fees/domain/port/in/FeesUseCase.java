package com.nexusgesti.fees.domain.port.in;

import com.nexusgesti.fees.domain.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeesUseCase {

    List<GrilleTarifaire> listGrilles(UUID anneeId, String profil);
    GrilleTarifaire getGrille(UUID id);
    GrilleTarifaire getGrilleApplicable(UUID anneeId, String profil);
    GrilleTarifaire creerGrille(GrilleTarifaire grille);
    GrilleTarifaire activerGrille(UUID id);
    GrilleTarifaire figerGrille(UUID id);
    GrilleTarifaire archiverGrille(UUID id);
    GrilleTarifaire ajouterLigne(UUID grilleId, LigneGrille ligne);
    GrilleTarifaire ajouterReduction(UUID grilleId, Reduction reduction);

    List<AvisPaiement> listAvis(UUID eleveId, UUID anneeId);
    AvisPaiement getAvis(UUID id);
    AvisPaiement creerAvis(AvisPaiement avis);
    AvisPaiement validerAvis(UUID id);
    AvisPaiement emettreAvis(UUID id);
    AvisPaiement annulerAvis(UUID id, String motif);
    AvisPaiement appliquerRemise(UUID id, UUID reductionId, ContexteReduction contexte);

    List<Paiement> listPaiements(UUID eleveId, UUID avisId);
    Paiement getPaiement(UUID id);
    Paiement creerPaiement(Paiement paiement, String idempotencyKey);
    Paiement validerPaiement(UUID id);
    Paiement rejeterPaiement(UUID id, String motif);
    Paiement annulerPaiement(UUID id, String motif);

    List<CouponPaiement> listCoupons(UUID eleveId, UUID anneeId);
    CouponPaiement getCoupon(UUID id);
    CouponPaiement tirerCoupon(UUID id);
    CouponPaiement remettreCoupon(UUID id, UUID destinataire);
    CouponPaiement annulerCoupon(UUID id, String motif);
    boolean verifierCoupon(UUID id);

    List<Journalier> listJournaliers(LocalDate date, UUID site);
    Journalier getJournalier(UUID id);
    Journalier ouvrirJournalier(Journalier journalier);
    Journalier cloturerJournalier(UUID id);
}
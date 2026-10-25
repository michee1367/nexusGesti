package com.nexusgesti.fees.domain.port.out;

import com.nexusgesti.fees.domain.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeesRepository {

    GrilleTarifaire saveGrille(GrilleTarifaire grille);
    Optional<GrilleTarifaire> findGrille(UUID id);
    List<GrilleTarifaire> listGrilles(UUID anneeId, String profil);

    AvisPaiement saveAvis(AvisPaiement avis);
    Optional<AvisPaiement> findAvis(UUID id);
    List<AvisPaiement> listAvis(UUID eleveId, UUID anneeId);

    Paiement savePaiement(Paiement paiement);
    Optional<Paiement> findPaiement(UUID id);
    List<Paiement> listPaiements(UUID eleveId, UUID avisId);

    CouponPaiement saveCoupon(CouponPaiement coupon);
    Optional<CouponPaiement> findCoupon(UUID id);
    List<CouponPaiement> listCoupons(UUID eleveId, UUID anneeId);
    long countCouponsAnnee(UUID anneeId);
    Optional<CouponPaiement> findCouponNumero(String numero);

    Journalier saveJournalier(Journalier journalier);
    Optional<Journalier> findJournalier(UUID id);
    List<Journalier> listJournaliers(LocalDate date, UUID site);
}
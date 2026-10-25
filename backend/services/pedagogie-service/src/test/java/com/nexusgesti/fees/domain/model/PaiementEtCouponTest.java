package com.nexusgesti.fees.domain.model;

import com.nexusgesti.fees.domain.FeesException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaiementEtCouponTest {

    @Test
    void paiementValideDepuisEnAttente() {
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(UUID.randomUUID());
        paiement.valider();
        assertEquals(StatutPaiement.VALIDE, paiement.getStatut());
        assertNotNull(paiement.getDateValidation());
    }

    @Test
    void paiementAnnuLeNestPasRejetable() {
        Paiement paiement = new Paiement();
        paiement.annuler("motif");
        assertThrows(FeesException.class, () -> paiement.rejeter("motif"));
    }

    @Test
    void ajouterImputationRattacheLePaiement() {
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(UUID.randomUUID());
        Imputation imputation = new Imputation();
        imputation.setEcheanceId(UUID.randomUUID());
        imputation.setMontant(new BigDecimal("30.00"));
        paiement.ajouterImputation(imputation);
        assertEquals(1, paiement.getImputations().size());
        assertEquals(paiement.getIdPaiement(), paiement.getImputations().get(0).getPaiementId());
    }

    @Test
    void estIdempotentSelonIdEvenement() {
        Paiement sansId = new Paiement();
        assertFalse(sansId.estIdempotent());
        Paiement idempotent = new Paiement();
        idempotent.setIdEvenement("event-1");
        assertTrue(idempotent.estIdempotent());
    }

    @Test
    void tirerCalculeEmpreinteEtChangementLaDetecte() {
        CouponPaiement coupon = coupon();
        coupon.tirer();
        assertEquals(StatutCoupon.TIRE, coupon.getStatut());
        assertTrue(coupon.verifierEmpreinte());
        coupon.setMontant(new BigDecimal("999.00"));
        assertFalse(coupon.verifierEmpreinte());
    }

    @Test
    void remettreExigeTiragePrealable() {
        CouponPaiement coupon = coupon();
        assertThrows(FeesException.class, () -> coupon.remettre(UUID.randomUUID()));
        coupon.tirer();
        coupon.remettre(UUID.randomUUID());
        assertEquals(StatutCoupon.REMIS, coupon.getStatut());
        assertNotNull(coupon.getRemisLe());
    }

    @Test
    void couponRemisNonAnnulable() {
        CouponPaiement coupon = coupon();
        coupon.tirer();
        coupon.remettre(UUID.randomUUID());
        assertThrows(FeesException.class, () -> coupon.annuler("motif"));
    }

    private CouponPaiement coupon() {
        CouponPaiement coupon = new CouponPaiement();
        coupon.setId(UUID.randomUUID());
        coupon.setNumero("COUPON-PAIEMENT-2026-00001");
        coupon.setSequence(1);
        coupon.setAnnee(UUID.randomUUID());
        coupon.setEleve(UUID.randomUUID());
        coupon.setMontant(new BigDecimal("100.00"));
        coupon.setLibelle("SCOLARITE");
        coupon.setMoyen(MoyenPaiement.ESPECES);
        coupon.setDevise("USD");
        return coupon;
    }
}
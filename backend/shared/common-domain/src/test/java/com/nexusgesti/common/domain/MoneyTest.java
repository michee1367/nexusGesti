package com.nexusgesti.common.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private static final Money USD_100 = Money.of("USD", new BigDecimal("100.00"));

    @Test
    void refuseDeviseInconnue() {
        assertThatThrownBy(() -> Money.of("XYZ", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("XYZ");
    }

    @Test
    void refuseDeviseVierge() {
        assertThatThrownBy(() -> Money.of("  ", BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("devise is required");
    }

    @Test
    void refuseMontantNul() {
        assertThatThrownBy(() -> Money.of("USD", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void additionMemeDevise() {
        assertThat(USD_100.plus(Money.of("USD", new BigDecimal("50.50"))).montant())
                .isEqualByComparingTo("150.50");
    }

    @Test
    void refuseAdditionDeviseDifferente() {
        Money cdf = Money.of("CDF", new BigDecimal("1000"));
        assertThatThrownBy(() -> USD_100.plus(cdf))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("devise mismatch");
    }

    @Test
    void autoriseMontantNegatifPourAvoirEtCorrection() {
        Money avoir = Money.of("USD", new BigDecimal("-25.00"));
        assertThat(avoir.isNegative()).isTrue();
        assertThat(avoir.abs().montant()).isEqualByComparingTo("25.00");
    }

    @Test
    void arrondiExplicite() {
        Money arrondi = Money.of("CDF", new BigDecimal("1234.5678")).rounded(2, RoundingMode.HALF_UP);
        assertThat(arrondi.montant()).isEqualByComparingTo("1234.57");
    }

    @Test
    void zeroExposePasLEcriture() {
        assertThat(Money.zero("USD").isZero()).isTrue();
    }
}
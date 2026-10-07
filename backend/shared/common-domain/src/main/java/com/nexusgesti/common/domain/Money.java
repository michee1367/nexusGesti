package com.nexusgesti.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public record Money(String devise, BigDecimal montant) {

    public Money {
        if (devise == null || devise.isBlank()) {
            throw new IllegalArgumentException("devise is required");
        }
        Objects.requireNonNull(montant, "montant is required");
        try {
            Currency.getInstance(devise);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unknown ISO 4217 devise: " + devise, e);
        }
    }

    public static Money of(String devise, BigDecimal montant) {
        return new Money(devise, montant);
    }

    public static Money zero(String devise) {
        return new Money(devise, BigDecimal.ZERO);
    }

    public boolean isZero() {
        return montant.signum() == 0;
    }

    public boolean isNegative() {
        return montant.signum() < 0;
    }

    public Money plus(Money other) {
        requireSameDevise(other);
        return new Money(devise, montant.add(other.montant()));
    }

    public Money minus(Money other) {
        requireSameDevise(other);
        return new Money(devise, montant.subtract(other.montant()));
    }

    public Money multipliedBy(BigDecimal factor) {
        return new Money(devise, montant.multiply(factor));
    }

    public Money abs() {
        return new Money(devise, montant.abs());
    }

    public Money negate() {
        return new Money(devise, montant.negate());
    }

    public Money rounded(int scale, RoundingMode mode) {
        return new Money(devise, montant.setScale(scale, mode));
    }

    public BigDecimal normalized() {
        return montant.stripTrailingZeros();
    }

    private void requireSameDevise(Money other) {
        if (!devise.equals(other.devise)) {
            throw new IllegalArgumentException(
                    "devise mismatch: " + devise + " vs " + other.devise());
        }
    }
}
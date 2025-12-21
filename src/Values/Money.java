package Values;

import Enums.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Money implements Comparable<Money> {
    private static final int SCALE = 2; // cents precision
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final BigDecimal amount;
    private final Currency currency;

    public Money(double amount) {
        this(amount, Currency.getDefault());
    }

    public Money(int amount) {
        this(amount, Currency.getDefault());
    }

    public Money(BigDecimal amount) {
        this(amount, Currency.getDefault());
    }

    public Money(double amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null) throw new IllegalArgumentException("Amount cannot be null");
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        this.amount = amount.setScale(SCALE, ROUNDING);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero() {
        return zero(Currency.getDefault());
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot add null money");
        }
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currencies must match to add: " + this.currency + " vs " + other.currency);
        }
        return new Money(this.amount.add(other.amount).doubleValue(), this.currency);
    }

    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Cannot subtract null money");
        }
        if (this.currency != other.currency) {
            throw new IllegalArgumentException("Currencies must match to subtract: " + this.currency + " vs " + other.currency);
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Result cannot be negative");
        }
        return new Money(result.doubleValue(), this.currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Factor cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)).doubleValue(), this.currency);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return amount.compareTo(money.amount) == 0 && currency == money.currency;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }

    @Override
    public int compareTo(Money o) {
        int cmp = this.currency.compareTo(o.currency);
        if (cmp != 0) {
            return cmp;
        }
        return this.amount.compareTo(o.amount);
    }
}

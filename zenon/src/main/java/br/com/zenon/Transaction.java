package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record Transaction(int step, TransactionType type, BigDecimal amount, TransactionCustomer origin,
                          TransactionCustomer recipient, boolean isFraud, boolean isFlaggedFraud) {

    public Transaction {
        Objects.requireNonNull(type, "type deve ser informado");
        Objects.requireNonNull(amount, "amount deve ser informado");
        Objects.requireNonNull(origin, "origin deve ser informado");
        Objects.requireNonNull(recipient, "recipient deve ser informado");

        if (step <= 0) throw new IllegalArgumentException("Step deve ser positivo: " + step);
        if (amount.signum() < 0) throw new IllegalArgumentException("Amonut deve ser positivo: " + amount);
    }
}

package br.com.zenon;

import java.math.BigDecimal;
import java.util.Objects;

public record TransactionCustomer(String name, BigDecimal oldBalance, BigDecimal newBalance) {

    public TransactionCustomer {

        Objects.requireNonNull(name, "name deve ser informado");
        Objects.requireNonNull(oldBalance, "oldBalance deve ser informado");
        Objects.requireNonNull(newBalance, "newBalance deve ser informado");

        if (name.trim().isEmpty()) throw new IllegalArgumentException("Name deve ser informado");
        if (oldBalance.signum() < 0) throw new IllegalArgumentException("Old balance deve ser positivo ou zero: " + oldBalance);
        if (newBalance.signum() < 0) throw new IllegalArgumentException("New balance deve ser positivo ou zero: " + newBalance);
    }
}

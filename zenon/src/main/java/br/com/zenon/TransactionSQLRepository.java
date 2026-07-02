package br.com.zenon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class TransactionSQLRepository implements TransactionRepository {

    public static final int JDBC_BATCH_SIZE = 10_000;

    @Override
    public void save(Transaction transaction) {
        String sql = """
                INSERT INTO transactions (
                    step, type, amount,
                    name_origin, old_balance_origin, new_balance_origin,
                    name_recipient, old_balance_recipient, new_balance_recipient,
                    is_fraud, is_flagged_fraud
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectioFactory.getCnnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, transaction.step());
            ps.setString(2, transaction.type().name());
            ps.setBigDecimal(3, transaction.amount());

            ps.setString(4, transaction.origin().name());
            ps.setBigDecimal(5, transaction.origin().oldBalance());
            ps.setBigDecimal(6, transaction.origin().newBalance());

            ps.setString(7, transaction.recipient().name());
            ps.setBigDecimal(8, transaction.recipient().oldBalance());
            ps.setBigDecimal(9, transaction.recipient().newBalance());

            ps.setBoolean(10, transaction.isFraud());
            ps.setBoolean(11, transaction.isFlaggedFraud());

            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException("erro ao salvar a transação" + e);
        }
    }

    @Override
    public Optional<Transaction> findByOriginName(String originName) {

        String sql = """
                SELECT id, step, `type`, amount, name_origin, old_balance_origin, 
                        new_balance_origin, name_recipient, old_balance_recipient, 
                        new_balance_recipient, is_fraud, is_flagged_fraud
                FROM zenon_frauds.transactions
                where name_origin = ?
                order by step
                """;

        try (Connection conn = ConnectioFactory.getCnnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, originName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    IO.println(rs.getString("name_origin"));
                    Transaction transaction = mapResultSetToTransaction(rs);
                    return Optional.of(transaction);
                } else {
                    IO.println("Nenhum transaction encontrado para o origin " + originName);
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet rs) {
        try {
            int step = rs.getInt("step");
            TransactionType type = TransactionType.valueOf(rs.getString("type"));
            BigDecimal amount = rs.getBigDecimal("amount");

            String nameOrigin = rs.getString("name_origin");
            BigDecimal oldBalanceOrigin = rs.getBigDecimal("old_balance_origin");
            BigDecimal newBalanceOrigin = rs.getBigDecimal("new_balance_origin");
            TransactionCustomer origin = new TransactionCustomer(nameOrigin, oldBalanceOrigin, newBalanceOrigin);

            String nameRecipient = rs.getString("name_recipient");
            BigDecimal oldBalanceRecipient = rs.getBigDecimal("old_balance_recipient");
            BigDecimal newBalanceRecipient = rs.getBigDecimal("new_balance_recipient");
            TransactionCustomer recipient = new TransactionCustomer(nameRecipient, oldBalanceOrigin, newBalanceRecipient);

            boolean isFraud = rs.getBoolean("is_fraud");
            boolean isFlagFraud = rs.getBoolean("is_flagged_fraud");

            return new Transaction(step, type, amount, origin, recipient, isFraud, isFlagFraud);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveAll(List<Transaction> transactions) {

        String sql = """
                INSERT INTO transactions (
                    step, type, amount,
                    name_origin, old_balance_origin, new_balance_origin,
                    name_recipient, old_balance_recipient, new_balance_recipient,
                    is_fraud, is_flagged_fraud
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectioFactory.getCnnection()) {
            conn.setAutoCommit(false);

            int count = 0;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (Transaction transaction : transactions) {

                    ps.setInt(1, transaction.step());
                    ps.setString(2, transaction.type().name());
                    ps.setBigDecimal(3, transaction.amount());

                    ps.setString(4, transaction.origin().name());
                    ps.setBigDecimal(5, transaction.origin().oldBalance());
                    ps.setBigDecimal(6, transaction.origin().newBalance());

                    ps.setString(7, transaction.recipient().name());
                    ps.setBigDecimal(8, transaction.recipient().oldBalance());
                    ps.setBigDecimal(9, transaction.recipient().newBalance());

                    ps.setBoolean(10, transaction.isFraud());
                    ps.setBoolean(11, transaction.isFlaggedFraud());

                  //  IO.println("Salvando transação: " + transaction.step() + "/"   + transaction.amount());

                    ps.addBatch();
                    count++;
                }
                if (count % JDBC_BATCH_SIZE == 0) {  //ps.execute();
                    ps.executeBatch();
                    conn.commit();
                }

                IO.println("Executando batch final...");
                ps.executeBatch();
                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(" Erro ao executar o rollback" + ex);
                }
                throw new RuntimeException("erro ao salvar a transação" + e);
            }
        } catch (
                SQLException e) {
            throw new RuntimeException("Erro na coexão com BD... " + e);
        }

    }

    public void clear() {
        String sql = """
                DELETE transactions FROM transactions
                """;
        try (Connection conn = ConnectioFactory.getCnnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException("erro ao limpar transações" + e);
        }
    }
}


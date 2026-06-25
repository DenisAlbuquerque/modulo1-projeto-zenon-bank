package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

public class TransactionReport {

    private record ReportTransaction(BigDecimal amount, boolean isFraud) {
    }


    public record Statistics(long totalTransactions, long totalFrauds, BigDecimal totalAmount) {

        private final static Statistics ZERO = new Statistics(0 , 0, BigDecimal.ZERO);

        private Statistics addReportTransaction(ReportTransaction rt) {
            return new Statistics(
                totalTransactions + 1,
                totalFrauds + (rt.isFraud ? 1 : 0),
                totalAmount.add(rt.amount));
        }

        private Statistics add(Statistics other) {
            return new Statistics(
                    totalTransactions + other.totalTransactions,
                    totalFrauds + other.totalFrauds,
                    totalAmount.add(other.totalAmount));
        }

    }

    public Statistics generateReport(String fileName) {
        Path path = Paths.get(fileName);
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1)
                    .map(this::parseReportTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(Statistics.ZERO, Statistics::addReportTransaction, Statistics::add);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file " + fileName, e);
        }
    }

    public Optional<ReportTransaction> parseReportTransaction(String linha) {

        try {
            String[] chuncks = linha.split(",");

            if(chuncks[2] == null || chuncks[2].trim().isEmpty()) throw new  IllegalArgumentException("O Valor de amount não poed ser nulo e nem vazio.");
            BigDecimal amount = new BigDecimal(chuncks[2]);

            boolean isFraud = "1".equals(chuncks[9]);

            return Optional.of(new ReportTransaction(amount, isFraud));
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + linha + " => " + e);
            // e.printStackTrace();
            return Optional.empty();
        }
    }
}

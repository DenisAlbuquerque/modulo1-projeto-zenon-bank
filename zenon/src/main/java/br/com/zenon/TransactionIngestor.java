package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class TransactionIngestor {

    private static final long FRAUD_LIMIT = 10_000;

    public List<Transaction> read(String fileName) {

        Path path = Paths.get(fileName);
        try {
           List<String> lines = Files.readAllLines(path);

           return lines.stream()
                   .skip(1)
                   .limit(FRAUD_LIMIT)
                   .map(this::parseTransaction)
                   //.filter(Objects::nonNull)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Transaction> parseTransaction(String linha) {

        try {

            String[] chuncks = linha.split(",");

            int step = Integer.parseInt(chuncks[0]);
            TransactionType type = TransactionType.valueOf(chuncks[1]);

            if(chuncks[2] == null || chuncks[2].trim().isEmpty()) throw new  IllegalArgumentException("O Valor de amount não poed ser nulo e nem vazio.");
            BigDecimal amount = new BigDecimal(chuncks[2]);

            var origin = new TransactionCustomer(chuncks[3], new BigDecimal(chuncks[4]), new BigDecimal(chuncks[5]));
            var recipient = new TransactionCustomer(chuncks[6], new BigDecimal(chuncks[7]), new BigDecimal(chuncks[8]));

            boolean isFraud = "1".equals(chuncks[9]);
            boolean isFlagFraud = "1".equals(chuncks[10]);

            return Optional.of(new Transaction(step, type, amount, origin, recipient, isFraud, isFlagFraud));
        } catch (Exception e) {
            System.err.println("Erro ao processar linha: " + linha + " => " + e);
           // e.printStackTrace();
            return Optional.empty();
        }
    }
}

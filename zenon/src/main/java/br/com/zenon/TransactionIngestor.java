package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TransactionIngestor {

    public List<Transaction> read(String fileName) {

        Path path = Paths.get(fileName);
        try {
           List<String> lines = Files.readAllLines(path);

           return lines.stream()
                   .skip(1)
                   .limit(1000)
                   .map(this::parseTransaction)
                   .toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Transaction parseTransaction(String linha) {

        String[] chuncks = linha.split(",");

        int step = Integer.parseInt(chuncks[0]);
        TransactionType type = TransactionType.valueOf(chuncks[1]);
        BigDecimal amount = new BigDecimal(chuncks[2]);

        var origin = new TransactionCustomer(chuncks[3], new BigDecimal(chuncks[4]), new BigDecimal(chuncks[5]));
        var recipient = new TransactionCustomer(chuncks[6], new BigDecimal(chuncks[7]), new BigDecimal(chuncks[8]));

        boolean isFraud = "1".equals(chuncks[9]);
        boolean isFlagFraud = "1".equals(chuncks[10]);

        return new Transaction(step, type, amount, origin, recipient, isFraud,isFlagFraud);
    }
}

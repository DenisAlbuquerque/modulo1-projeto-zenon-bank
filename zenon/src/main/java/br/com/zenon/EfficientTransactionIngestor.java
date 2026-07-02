package br.com.zenon;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class EfficientTransactionIngestor {

    public static final long FRAUD_LIMIT = 10_000;
    public static final int LINE_BATCH_SIZE = 5_000;

    public void readAsBatch(String fileName, Consumer<List<Transaction>> batchConsumer) {
        Path path = Paths.get(fileName);
        try (ExecutorService executor = Executors.newFixedThreadPool(10);
                Stream<String> lines = Files.lines(path).skip(1)/*.limit(FRAUD_LIMIT)*/) {

            var iterator = lines.iterator();

            List<String> lineBatch = new ArrayList<>(LINE_BATCH_SIZE);
            while (iterator.hasNext()) {

                String line = iterator.next();
                lineBatch.add(line);

                if (lineBatch.size() >= LINE_BATCH_SIZE) {
                    IO.println("Executando batch injestor...");
                    final List<String> currentLineBatch = List.copyOf(lineBatch);
                    executor.submit(() -> executeBatch(currentLineBatch, batchConsumer));
                    //executeBatch(currentLineBatch, batchConsumer); //sem concorrencia
                    lineBatch.clear();
                }
            }

            if (!lineBatch.isEmpty()) {
                IO.println("Executando batch final injestor...");
                final List<String> currentLineBatch = List.copyOf(lineBatch);
                executor.submit(() -> executeBatch(List.copyOf(currentLineBatch), batchConsumer));
                //executeBatch(currentLineBatch, batchConsumer); //sem concorrencia
                lineBatch.clear();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeBatch(List<String> lineBatch, Consumer<List<Transaction>> batchConsumer) {
        List<Transaction> transactions =
            lineBatch
                .stream()
                    .map(this::parseTransaction)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        batchConsumer.accept(transactions);
    }

    public void readAsStream(String fileName, Consumer<Transaction> consumer) {
        Path path = Paths.get(fileName);
        try (Stream<String> lines = Files.lines(path)) {
            lines
                .skip(1)
                //.limit(FRAUD_LIMIT)
                .map(this::parseTransaction)
                //.filter(Objects::nonNull)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(consumer);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Optional<Transaction> parseTransaction(String linha) {

        try {

            String[] chuncks = linha.split(",");

            int step = Integer.parseInt(chuncks[0]);
            TransactionType type = TransactionType.valueOf(chuncks[1]);

            if (chuncks[2] == null || chuncks[2].trim().isEmpty())
                throw new IllegalArgumentException("O Valor de amount não poed ser nulo e nem vazio.");
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

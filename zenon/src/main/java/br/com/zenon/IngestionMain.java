package br.com.zenon;

import java.util.List;

public class IngestionMain {
    void main() {

        var repository = new TransactionSQLRepository();

        var transactionIngestor = new EfficientTransactionIngestor();

        repository.clear();

        long startTimeList = System.nanoTime();
        transactionIngestor.readAsBatch("data/PS_20174392719_1491204439457_log.csv",
                repository::saveAll);

        long endTimeList = System.nanoTime();
        IO.println("Tempo de Ingestão de dados : " +  (endTimeList - startTimeList) / 1_000_000.0 + " ms" );

        /* old Scholl

        var transactionIngestor = new TransactionIngestor();
        repository.clear();

        long startTimeList = System.nanoTime();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        IO.println("Iniciando a ingestão de dados");
        repository.saveAll(transactions);

        long endTimeList = System.nanoTime();
        IO.println("Tempo de Ingestão de dados : " +  (endTimeList - startTimeList) / 1_000_000.0 + " ms" );
        * */

    }
}

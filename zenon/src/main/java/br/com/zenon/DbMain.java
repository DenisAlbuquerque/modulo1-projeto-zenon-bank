package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;

public class DbMain {
    void main() {

        ConnectioFactory.getCnnection();
        IO.println("Conectado com sucesso!");

        var repository = new TransactionSQLRepository();

        var transactionIngestor = new TransactionIngestor();

        long startTimeList = System.nanoTime();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        IO.println("Iniciando");
        transactions.forEach(repository::save);
        long endTimeList = System.nanoTime();

        IO.println("tempo de busca com List : " +  (endTimeList - startTimeList) / 1_000_000.0 + " ms" );

        repository.findByOriginName("C1231006815")
                .ifPresentOrElse(IO::println, () -> IO.println("Não encontrada para: Denis"));


    }
}

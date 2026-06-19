package br.com.zenon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    void main() {

        var t1 = new Transaction(1, TransactionType.PAYMENT, new BigDecimal("9838.64"),
            new TransactionCustomer("C1231006815", new BigDecimal("170136.0"), new BigDecimal("160296.36")),
            new TransactionCustomer("M1979787155", new BigDecimal("0.0"), new BigDecimal("0.0")),
            false, false
        );

        var t2 = new Transaction(743, TransactionType.CASH_OUT, new BigDecimal("850002.52"),
                new TransactionCustomer("C1280323807", new BigDecimal("850002.52"), new BigDecimal("0.0")),
                new TransactionCustomer("C873221189", new BigDecimal("6510099.11"), new BigDecimal("7360101.63")),
                true, false
        );

        IO.println(t1);
        IO.println(t2);

        IO.println("-----------------------------------------------------------");
        var transactionIngestor = new TransactionIngestor();
        List<Transaction> transactions = transactionIngestor.read("data/PS_20174392719_1491204439457_log.csv");
        IO.println(transactions.size());

        transactions.stream()
                    .limit(10)
                    .forEach(IO::println);

        IO.println("-----------------------BadData----------------------------");
        List<Transaction> transactionsBadData = transactionIngestor.read("data/paysim_with_bad_data.csv");
        IO.println(transactionsBadData.size());

        transactionsBadData.forEach(IO::println);

        IO.println("--------------------Fraudes----------------------");

        var fraudAnalyze = new FraudAnalyze(transactions);

        var totalFrauds = fraudAnalyze.countFrauds();
        IO.println("Total de Fraudes: " + totalFrauds);

        IO.println("-----------------Maiores  fraudes-------------------------");

        var highFrauds = fraudAnalyze.findHighestValueFrauds(3);
        highFrauds
                .stream()
                .map(Transaction::amount)
                .forEach(IO::println);

        IO.println("-----------------Maiores fraudadores-------------------------");

        var suspiciousClients = fraudAnalyze.findHighestSuspicious(5);
        suspiciousClients.forEach(IO::println);

        IO.println("-----------------Prejuizo Total com fraudes-------------------------");

        BigDecimal totalPrejuizos =  fraudAnalyze.calculateTotalFraudLoss();
        IO.println( "Prejuizo total: " + totalPrejuizos);

        IO.println("-----------------Fraudes saida e entrada-------------------------");

        Map<TransactionType, Long> fraudCountType =  fraudAnalyze.countFraudsType();

        IO.println("Fraudes por tipo:" );
        IO.println(fraudCountType);

        IO.println("-----------------Busca por nome-------------------------");
        IO.println("-----------------Não encontrada-------------------------");

        TransactionListRepository transactionRepository;

        transactionRepository = new TransactionListRepository(transactions);
        var notFoundOriginName = "c12345";
        transactionRepository.findByOriginName(notFoundOriginName)
                .ifPresentOrElse(IO::println, () -> IO.println("Nenhum transaction encontrado: " + notFoundOriginName ));

        IO.println("-----------------Encontrada com time-------------------------");
        var existingOriginNameTime = "C1868032458";

        long startTimeList = System.nanoTime();
        transactionRepository.findByOriginName(existingOriginNameTime)
                .ifPresentOrElse(IO::println, () -> IO.println("Nenhum transaction encontrado:  " + existingOriginNameTime ));
        long endTimeList = System.nanoTime();
        IO.println("tempo de busca com List : " +  (endTimeList - startTimeList) / 1_000_000.0 + " ms");

        IO.println("-----------------Encontrada com time usando Map-------------------------");
        TransactionMapRepository TransactionMapRepository;
        TransactionMapRepository = new TransactionMapRepository(transactions);

        startTimeList = System.nanoTime();
        TransactionMapRepository.findByOriginName(existingOriginNameTime)
                .ifPresentOrElse(IO::println, () -> IO.println("Nenhum transaction encontrado:  " + existingOriginNameTime ));
        endTimeList = System.nanoTime();
        IO.println("tempo de busca com Map : " +  (endTimeList - startTimeList) / 1_000_000.0 + " ms");


    }
}

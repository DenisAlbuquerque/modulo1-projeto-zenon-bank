package br.com.zenon;

public class ReportMain {

    void main() {
        var trasactionReport = new TransactionReport();
        TransactionReport.Statistics statistics =  trasactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");
        IO.println("""
                Total de linhas: %d
                Total de fraudes: %d
                Valor Total: %.2f
                """.formatted(statistics.totalTransactions(), statistics.totalFrauds(), statistics.totalAmount()));
    }

}

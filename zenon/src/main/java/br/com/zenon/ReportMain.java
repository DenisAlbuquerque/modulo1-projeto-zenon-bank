package br.com.zenon;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {

    void main(String[]args) {

        String language = (args.length > 0 ? args[0] : "pt");
        var locale = Locale.of(language);

        var integerFormat = NumberFormat.getIntegerInstance(locale);
        var currencyFormat = DecimalFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("USD"));

        var resourceBundle = ResourceBundle.getBundle("report", locale);

        var trasactionReport = new TransactionReport();
        TransactionReport.Statistics statistics =  trasactionReport.generateReport("data/PS_20174392719_1491204439457_log.csv");

        String fmtTotalTransaction = integerFormat.format(statistics.totalTransactions());
        String fmtTotalFrauds = integerFormat.format(statistics.totalFrauds());
        String fmtTotalAmount = currencyFormat.format(statistics.totalAmount());

        String msgTotalTransaction = resourceBundle.getString("label.total.transactions");
        String msgTotalFrauds = resourceBundle.getString("label.total.frauds");
        String msgTotalAmount = resourceBundle.getString("label.total.amount");

        IO.println("""
               %s: %s
               %s: %s
               %s: %s
               """.formatted(
                    msgTotalTransaction, fmtTotalTransaction,
                    msgTotalFrauds, fmtTotalFrauds,
                    msgTotalAmount, fmtTotalAmount));

    }

}

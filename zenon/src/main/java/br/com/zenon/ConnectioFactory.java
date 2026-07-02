package br.com.zenon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectioFactory {

    private ConnectioFactory() {}

    public static Connection getCnnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/zenon_frauds?rewriteBatchedStatements=true", "root","senha123"); //rew
        } catch (SQLException e) {
            throw new RuntimeException(" Erro de conexão" + e);
        }

        /*
        * connection original "jdbc:mysql://localhost:3306/zenon_frauds"
        * nova "jdbc:mysql://localhost:3306/zenon_frauds?rewriteBatchedStatements=true"
        * rewriteBatchedStatements=true pega um lote inteiro por exemplo 1000 insert e faz apenas 1 passando todos
        * os outros inserts com uma inserção
        * */

    }
}

package br.com.zenon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectioFactory {

    private ConnectioFactory() {}

    public static Connection getCnnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/zenon_frauds", "root","senha123");
        } catch (SQLException e) {
            throw new RuntimeException(" Erro de conexão" + e);
        }

    }
}

package src.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/OrderManagementSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sabrina";
    private static Connection connection;

    public static Connection getDBConn() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}

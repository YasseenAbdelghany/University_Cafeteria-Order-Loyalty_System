package DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String dbURL = "jdbc:mysql://127.0.0.1:3306/CafeteriaSystem" ;
    private static final String dbUser = "root" ;
    private static final String dbPass = "1234" ;
    private Connection con ;
    public DBconnection() {
        connect();
    }
    public void connect() {
        try {
            // Explicit driver loading
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(dbURL, dbUser, dbPass);
        }catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found. Please add mysql-connector-java to classpath.", e);
        }  catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database. Please check if MySQL server is running and database 'CafeteriaDB' exists.", e);
        }
    }

    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check connection status", e);
        }
        return con;
    }
    public void close() {
        try {
            if (con != null && !con.isClosed()) {
                    con.close();
            }
        }catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }
}

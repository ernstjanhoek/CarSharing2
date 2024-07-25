package carsharing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBClient {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";

    private final Connection conn;

    public DBClient(String dataBaseUrl) throws SQLException {
        conn = DriverManager.getConnection(dataBaseUrl, "", "");
        conn.setAutoCommit(true);
    }

    public Connection getConnection() {
        return conn;
    }

    public void createDataBase() {
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            String sqlCompany =  "CREATE TABLE IF NOT EXISTS company" +
                    "(id INT PRIMARY KEY AUTO_INCREMENT," +
                    " name VARCHAR(255) UNIQUE NOT NULL);";

            String sqlCar = "CREATE TABLE IF NOT EXISTS car" +
                    "(id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(255) UNIQUE NOT NULL, " +
                    "available BOOLEAN NOT NULL, " +
                    "company_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (company_id) REFERENCES company(id));";

            String sqlCustomer = "CREATE TABLE IF NOT EXISTS customer" +
                    "(id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(255) UNIQUE NOT NULL, " +
                    "rented_car_id INTEGER, " +
                    "FOREIGN KEY (rented_car_id) REFERENCES car(id));";

            stmt = conn.createStatement();
            stmt.executeUpdate(sqlCompany);

            stmt = conn.createStatement();
            stmt.executeUpdate(sqlCar);

            stmt = conn.createStatement();
            stmt.executeUpdate(sqlCustomer);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException ignored) {}
        }
    }
}
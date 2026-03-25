package XuatSac5.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/Rikkei_Hospital_DB?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Quang13102006";

    public static Connection openConnection() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initDatabase() {
        String dropTables = """
                DROP TABLE IF EXISTS patientWallet;
                DROP TABLE IF EXISTS patients;
                DROP TABLE IF EXISTS beds;
                """;

        String createBeds = """
                CREATE TABLE beds (
                    bed_id INT PRIMARY KEY,
                    status VARCHAR(20) DEFAULT 'Empty' -- 'Empty' hoặc 'Occupied'
                )
                """;

        String createPatients = """
                CREATE TABLE patients (
                    patient_id INT AUTO_INCREMENT PRIMARY KEY,
                    patient_name VARCHAR(100) NOT NULL,
                    age INT,
                    bed_id INT,
                    FOREIGN KEY (bed_id) REFERENCES beds(bed_id) ON DELETE SET NULL
                )
                """;

        String createWallet = """
                CREATE TABLE patientWallet (
                    wallet_id INT AUTO_INCREMENT PRIMARY KEY,
                    patient_id INT,
                    balance DECIMAL(10,2) DEFAULT 0,
                    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE
                )
                """;

        String insertBeds = "INSERT INTO beds (bed_id, status) VALUES (1, 'Empty'), (2, 'Empty'), (3, 'Empty'), (4, 'Empty'), (5, 'Empty')";

        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS patientWallet");
            stmt.execute("DROP TABLE IF EXISTS patients");
            stmt.execute("DROP TABLE IF EXISTS beds");

            stmt.execute(createBeds);
            stmt.execute(createPatients);
            stmt.execute(createWallet);

            stmt.execute(insertBeds);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

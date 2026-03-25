package BTTH.util;

import java.sql.*;

public class DBConnect {
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/BTTHSS13?createDatabaseIfNotExist=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Quang13102006";

    public static Connection openConnection() throws SQLException {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void initDatabase() {
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS INVOICES");
            stmt.execute("DROP TABLE IF EXISTS BEDS");
            stmt.execute("DROP TABLE IF EXISTS PATIENTS");

            stmt.execute("""
                CREATE TABLE PATIENTS (
                    patient_id INT PRIMARY KEY,
                    patient_name VARCHAR(100),
                    status VARCHAR(50)
                )
            """);

            stmt.execute("""
                CREATE TABLE BEDS (
                    bed_id VARCHAR(10) PRIMARY KEY,
                    patient_id INT,
                    status VARCHAR(50),
                    FOREIGN KEY (patient_id) REFERENCES PATIENTS(patient_id)
                )
            """);

            stmt.execute("""
                CREATE TABLE INVOICES (
                    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
                    patient_id INT,
                    total DECIMAL(10,2),
                    date DATETIME,
                    FOREIGN KEY (patient_id) REFERENCES PATIENTS(patient_id)
                )
            """);

            stmt.execute("INSERT INTO PATIENTS VALUES (101, 'Nguyễn Văn A', 'Đang điều trị')");
            stmt.execute("INSERT INTO BEDS VALUES ('G01', 101, 'Đang sử dụng')");


        } catch (SQLException e) {
            System.err.println("Lỗi khởi tạo DB: " + e.getMessage());
        }
    }
}

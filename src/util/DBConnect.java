package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
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

    public static void initDatabase(){
        String dropTableMedicineInventory = "drop table if exists medicineInventorys";
        String dropTableTablePatient = "drop table if exists patients";
        String dropTablePrescriptionHistory = "drop table if exists prescriptionHistorys";
        String dropTablePatientWallet = "drop table if exists patientWallet";
        String dropTableInvoices = "drop table if exists invoices";


        String sqlCreateTableMedicineInventory = """
                create table if not exists medicineInventorys(
                medicine_id int auto_increment primary key,
                medicine varchar(50) not null,
                quantity int check(quantity > 0)
                )
                """;

        String sqlCreateTablePatient = """
                CREATE TABLE IF NOT EXISTS patients (
                    patient_id int auto_increment primary key,
                    patient_name varchar(50) not null,
                    age int check(age > 0)
                )
                """;

        String sqlCreateTablePrescriptionHistory = """
                create table if not exists prescriptionHistorys(
                id int auto_increment primary key,
                patient_id int,
                medicine_id int,
                quantity int,
                date datetime,
                FOREIGN KEY(patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
                FOREIGN KEY(medicine_id) REFERENCES medicineInventorys(medicine_id) ON DELETE CASCADE
                )
                """;

        String sqlCreateTablePatientWallet = """
                create table if not exists patientWallet(
                    patient_wallet_id int auto_increment primary key,
                    patient_id int,
                    balance decimal(10,2),
                    foreign key(patient_id) references patients(patient_id) on delete cascade
                )
                """;

        String sqlCreateTableInvoices = """
                create table if not exists invoices(
                invoice_id int auto_increment primary key,
                patient_wallet_id int,
                total decimal(10,2),
                status varchar(10),
                foreign key(patient_wallet_id) references patientWallet(patient_wallet_id) on delete cascade
                )
                """;

        String sqlInsertMedicine = """
                insert into medicineInventorys(medicine_id, medicine, quantity) values (1, "Paracetamol", 10)
                """;

        String sqlInsertPatient = """
                insert into patients(patient_id, patient_name, age) values(1, "Quang", 20)
                """;

        String sqlInsertPatientWallet = """
                insert into patientWallet values(1, 1, 10000000)
                """;

        String sqlInsertInvoice = """
                insert into invoices values(1, 1, 5000000, "Pending")
                """;

        try(Connection conn = DBConnect.openConnection();
            Statement stmt = conn.createStatement()){

            stmt.execute(dropTableInvoices);
            stmt.execute(dropTablePatientWallet);
            stmt.execute(dropTablePrescriptionHistory);
            stmt.execute(dropTableTablePatient);
            stmt.execute(dropTableMedicineInventory);

            stmt.execute(sqlCreateTableMedicineInventory);
            stmt.execute(sqlCreateTablePatient);
            stmt.execute(sqlCreateTablePrescriptionHistory);
            stmt.execute(sqlCreateTablePatientWallet);
            stmt.execute(sqlCreateTableInvoices);

            stmt.executeUpdate(sqlInsertMedicine);
            stmt.executeUpdate(sqlInsertPatient);
            stmt.executeUpdate(sqlInsertPatientWallet);
            stmt.executeUpdate(sqlInsertInvoice);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

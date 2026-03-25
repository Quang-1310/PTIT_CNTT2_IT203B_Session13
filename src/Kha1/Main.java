package Kha1;

import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        DBConnect.initDatabase();
        dispenMedicine(1,1);
    }

    public static void dispenMedicine(int medicineId, int patientId){

        Connection conn = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        String sqlUpdateInventory = """
                update medicineInventorys set quantity = quantity - 1 where medicine_id = ?
                """;

        String sqlInsertHistory = """
                insert into prescriptionHistorys(patient_id, medicine_id, date) VALUES (?, ?, NOW())
                """;

        try{
            conn = DBConnect.openConnection();
            conn.setAutoCommit(false);

            ps1 = conn.prepareStatement(sqlUpdateInventory);
            ps1.setInt(1, medicineId);
            ps1.executeUpdate();


            ps2 = conn.prepareStatement(sqlInsertHistory);
            ps2.setInt(1, patientId);
            ps2.setInt(2, medicineId);
            ps2.executeUpdate();

            conn.commit();
            System.out.println("Cấp phát thuốc thành công!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

/*
Lý do khi xảy ra lỗi ở giữa dòng code, thuốc trong kho vẫn bị trừ mà dữ liệu không bị hủy bỏ: bởi vì chế độ auto-commit
mặc định là true vậy nên những đoạn code bên trên dòng code lỗi đó vẫn được lưu trữ vào database
*/

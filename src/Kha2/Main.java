package Kha2;

import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DBConnect.initDatabase();
        try{
            payHospitalFee(1,1, 5000000);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void payHospitalFee(int patientId, int invoiceId, double amount) throws SQLException {
        Connection conn = null;
        try{
            conn = DBConnect.openConnection();
            conn.setAutoCommit(false);

            String sqlDeductWallet = "UPDATE patientWallet SET balance = balance - ? WHERE patient_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sqlDeductWallet);
            ps1.setDouble(1, amount);
            ps1.setInt(2, patientId);
            ps1.executeUpdate();

            String sqlUpdateInvoice = "UPDATE Invoicesss SET status = 'PAID' WHERE invoice_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlUpdateInvoice);
            ps2.setInt(1, invoiceId);
            ps2.executeUpdate();

            conn.commit();
            System.out.println("Thanh toán hoàn tất!");
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("Lỗi hệ thống: Không thể hoàn tất thanh toán. Chi tiết " + e.getMessage());
        }
    }
}

/*
Tại sao việc chỉ dùng System.out.println() để in ra lỗi trong khối catch là vi phạm nguyên tắc của Transaction:
Bởi vì nếu chỉ dùng System.out.println() để in ra lỗi thì không cần đến khối try catch. Khối catch thật sự phải ném ra
lỗi. Trong transaction còn cần phải rollback lại

Hành động thiết yếu nào đã bị lập trình viên bỏ quên khi xảy ra SQLException: rollback

 */
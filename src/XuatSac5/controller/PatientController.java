package XuatSac5.controller;

import XuatSac5.util.DBConnection;

import java.sql.*;

public class PatientController {
    public boolean receptionPatient(String name, int age, int bedId, double deposit) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.openConnection();
            conn.setAutoCommit(false);

            String sql1 = "INSERT INTO patients (patient_name, age, bed_id) VALUES (?, ?, ?)";
            PreparedStatement ps1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
            ps1.setString(1, name);
            ps1.setInt(2, age);
            ps1.setInt(3, bedId);
            ps1.executeUpdate();

            ResultSet rs = ps1.getGeneratedKeys();
            int newPatientId = 0;
            if (rs.next()) newPatientId = rs.getInt(1);

            String sql2 = "UPDATE beds SET status = 'Occupied' WHERE bed_id = ? AND status = 'Empty'";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, bedId);
            int rowBed = ps2.executeUpdate();
            if (rowBed == 0) throw new SQLException("Giường không tồn tại hoặc đã có người!");

            String sql3 = "INSERT INTO patientWallet (patient_id, balance) VALUES (?, ?)";
            PreparedStatement ps3 = conn.prepareStatement(sql3);
            ps3.setInt(1, newPatientId);
            ps3.setDouble(2, deposit);
            ps3.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // ROLLBACK AN TOÀN
            }
            System.err.println("Lỗi tiếp nhận: " + e.getMessage());
            return false;
        } finally {
            conn.close();
        }
    }

    public void showEmptyBeds() {
        String sql = "SELECT bed_id FROM beds WHERE status = 'Empty'";
        try (Connection conn = util.DBConnect.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("--- DANH SÁCH GIƯỜNG TRỐNG ---");
            boolean hasBed = false;
            while (rs.next()) {
                System.out.println("Giường số: " + rs.getInt("bed_id"));
                hasBed = true;
            }
            if (!hasBed) System.out.println("Hiện tại đã hết giường trống!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


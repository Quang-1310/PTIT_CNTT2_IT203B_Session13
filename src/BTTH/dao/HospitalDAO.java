package BTTH.dao;
import java.sql.*;

public class HospitalDAO {
    public void createInvoice(Connection conn, int patientId, double amount) throws SQLException {
        String sql = "INSERT INTO INVOICES (patient_id, total, date) VALUES (?, ?, NOW())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setDouble(2, amount);
            ps.executeUpdate();
        }
    }

    public void updatePatientStatus(Connection conn, int patientId) throws SQLException {
        String sql = "UPDATE PATIENTS SET status = 'Đã xuất viện' WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.executeUpdate();
        }
    }

    public void releaseBed(Connection conn, int patientId) throws SQLException {
        String sql = "UPDATE BEDS SET patient_id = NULL, status = 'Trống' WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.executeUpdate();
        }
    }
}
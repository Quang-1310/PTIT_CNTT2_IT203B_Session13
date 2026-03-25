package BTTH.bussiness;
import BTTH.dao.HospitalDAO;
import BTTH.util.DBConnect;
import java.sql.*;

public class HospitalBusiness {
    private HospitalDAO dao = new HospitalDAO();

    public void processDischarge(int patientId, double fee) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnect.openConnection();
            conn.setAutoCommit(false);

            dao.createInvoice(conn, patientId, fee);
            dao.updatePatientStatus(conn, patientId);
            dao.releaseBed(conn, patientId);

            conn.commit();
            System.out.println("Xuất viện thành công cho bệnh nhân " + patientId);

        } catch (SQLException e) {
            System.err.println("Lỗi hệ thống! Đang thực hiện Rollback");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }
}
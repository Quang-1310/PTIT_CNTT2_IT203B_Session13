package Gioi3;

import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        DBConnect.initDatabase();

        dischargedAndPayFee(1, 1, 5000000);
    }

    public static void dischargedAndPayFee(int patientId, int invoiceId, double totalAmount){
        Connection conn = null;
        PreparedStatement psCheckBalance = null;
        PreparedStatement psDeductWallet = null;
        PreparedStatement psUpdateInvoice = null;

        try {
            conn = DBConnect.openConnection();
            if (conn == null) return;

            conn.setAutoCommit(false);

            // Bẫy 1: Kiểm tra số dư
            String sqlCheckBalance = "SELECT balance FROM patientWallet WHERE patient_id = ?";
            psCheckBalance = conn.prepareStatement(sqlCheckBalance);
            psCheckBalance.setInt(1, patientId);
            ResultSet rs = psCheckBalance.executeQuery();

            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < totalAmount) {
                    throw new SQLException("LỖI: Số dư không đủ để thực hiện giao dịch");
                }
            } else {
                throw new SQLException("LỖI: Không tìm thấy ví của bệnh nhân ID: " + patientId);
            }

            String sqlDeduct = "UPDATE patientWallet SET balance = balance - ? WHERE patient_id = ?";
            psDeductWallet = conn.prepareStatement(sqlDeduct);
            psDeductWallet.setDouble(1, totalAmount);
            psDeductWallet.setInt(2, patientId);
            int rowWallet = psDeductWallet.executeUpdate();


            // Bẫy 2: Kiểm tra Row Affected
            if (rowWallet == 0) {
                throw new SQLException("LỖI: Cập nhật ví thất bại");
            }

            String sqlUpdateInvoice = "UPDATE invoices SET status = 'PAID' WHERE invoice_id = ? AND patient_wallet_id = (SELECT patient_wallet_id FROM patientWallet WHERE patient_id = ?)";
            psUpdateInvoice = conn.prepareStatement(sqlUpdateInvoice);
            psUpdateInvoice.setInt(1, invoiceId);
            psUpdateInvoice.setInt(2, patientId);
            int rowInvoice = psUpdateInvoice.executeUpdate();

            if (rowInvoice == 0) {
                throw new SQLException("LỖI: Cập nhật hóa đơn thất bại");
            }

            conn.commit();
            System.out.println("Thành công: Đã trừ " + totalAmount + " và cập nhật hóa đơn " + invoiceId);

        } catch (SQLException e) {
            System.err.println("Giao dịch bị hủy!");
            System.err.println("Chi tiết: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (psCheckBalance != null){
                    psCheckBalance.close();
                }
                if (psDeductWallet != null){
                    psDeductWallet.close();
                }
                if (psUpdateInvoice != null){
                    psUpdateInvoice.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

/*
1. Dữ liệu đầu vào (Input):
patientId (kiểu int): Mã số định danh của bệnh nhân cần thực hiện thanh toán.
invoiceId (kiểu int): Mã số hóa đơn cần được cập nhật trạng thái.
totalAmount (kiểu double): Tổng số tiền viện phí mà bệnh nhân phải trả.

Kết quả trả về (Output):
Thành công: In ra thông báo đã trừ tiền và cập nhật hóa đơn kèm mã ID tương ứng; dữ liệu được lưu vĩnh viễn vào Database.
Thất bại: In ra thông báo "Giao dịch bị hủy!" kèm chi tiết lỗi cụ thể; dữ liệu được khôi phục về trạng thái ban đầu trước khi thực hiện hàm (Rollback).

2. Đề xuất giải pháp
Tắt chế độ autoCommit mặc định của JDBC
Cơ chế Try-Catch-Rollback: * Giải quyết Bẫy 1
Giải quyết Bẫy 2 (Dữ liệu ảo): Sử dụng giá trị trả về của phương thức executeUpdate().
Nếu số dòng bị tác động (Row Affected) bằng 0, điều đó có nghĩa là ID nhập vào không tồn tại, hệ thống sẽ ném ngoại lệ để thực hiện rollback().

3. Thiết kế các bước
- Mở kết nối: Khởi tạo Connection từ lớp DBConnect.
- Thiết lập Transaction: Tắt autoCommit để bắt đầu quản lý giao dịch thủ công.
- Kiểm tra số dư: Thực thi PreparedStatement truy vấn bảng patientWallet để xác nhận bệnh nhân có đủ tiền thanh toán hay không.
- Cập nhật ví tiền: Thực hiện trừ số tiền viện phí vào cột balance của bệnh nhân. Kiểm tra số dòng ảnh hưởng để đảm bảo ví tồn tại.
- Cập nhật hóa đơn: Thay đổi trạng thái (status) của hóa đơn thành 'PAID' dựa trên invoiceId và patientId. Kiểm tra số dòng ảnh hưởng để đảm bảo hóa đơn khớp với bệnh nhân.
- Xác nhận (Commit): Nếu tất cả các bước trên không phát sinh lỗi, gọi conn.commit() để hoàn tất giao dịch.
- Xử lý lỗi (Rollback): Nếu có bất kỳ SQLException nào xảy ra trong quá trình thực hiện, gọi conn.rollback() để hủy bỏ mọi thay đổi tạm thời.
- Đóng tài nguyên: Trong khối finally, lần lượt đóng các PreparedStatement và trả lại chế độ autoCommit(true) trước khi đóng kết nối để giải phóng tài nguyên hệ thống.
 */

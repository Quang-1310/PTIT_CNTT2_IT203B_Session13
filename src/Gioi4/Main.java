package Gioi4;

import util.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        DBConnect.initDatabase();

        getDashboardData();
    }

    public static List<PatientDTO> getDashboardData() {
        Map<Integer, PatientDTO> patientMap = new LinkedHashMap<>();

        String sql = """
            SELECT p.patient_id, p.patient_name, m.medicine, ph.quantity
            FROM patients p
            LEFT JOIN prescriptionHistorys ph ON p.patient_id = ph.patient_id
            LEFT JOIN medicineInventorys m ON ph.medicine_id = m.medicine_id
            """;

        try (Connection conn = DBConnect.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int pId = rs.getInt("patient_id");

                PatientDTO patient = patientMap.get(pId);
                if (patient == null) {
                    patient = new PatientDTO();
                    patient.setPatientId(pId);
                    patient.setPatientName(rs.getString("patient_name"));
                    patientMap.put(pId, patient);
                }

                String medName = rs.getString("medicine");
                if (medName != null) {
                    PrescriptionDTO pre = new PrescriptionDTO();
                    pre.setMedicineName(medName);
                    pre.setQuantity(rs.getInt("quantity"));

                    patient.getDsDichVu().add(pre);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(patientMap.values());
    }
}

/*
1. Phân tích & Đề xuất
Input: Không có tham số đầu vào (lấy toàn bộ danh sách Dashboard) hoặc có thể mở rộng thêm tham số phân trang.
Output: Trả về một List<PatientDTO>. Mỗi đối tượng PatientDTO chứa thông tin cơ bản của bệnh nhân và một danh sách List<PrescriptionDTO> các loại thuốc/dịch vụ họ đã sử dụng.

2. So sánh & Lựa chọn
Tiêu chí	          Giải pháp 1 (N+1 Query)	           Giải pháp 2 (JOIN + Map)
Số lượng Query	      Rất lớn (N + 1)	                   Duy nhất 1
Network I/O	          Cao (Bắn tin nhắn liên tục)	       Thấp
Độ phức tạp Java	  Đơn giản	                           Trung bình (Cần Map để gộp)

3. Thiết kế & Triển khai
- Thiết kế câu lệnh SQL: Sử dụng LEFT JOIN thay vì INNER JOIN để giải quyết "Bẫy 2" (Bệnh nhân mới nhập viện chưa có thuốc vẫn phải hiển thị tên trên Dashboard).
- Các bước xử lý trên Java:
- Mở kết nối thông qua DBConnect.openConnection().
- Sử dụng LinkedHashMap<Integer, PatientDTO> để lưu trữ bệnh nhân: vừa giúp tìm kiếm nhanh bằng ID, vừa giữ đúng thứ tự hiển thị từ Database đổ về.
- Trong vòng lặp while (rs.next()):
- Nếu patient_id chưa có trong Map: Khởi tạo PatientDTO mới và đưa vào Map.
- Kiểm tra cột medicine: Nếu khác null, khởi tạo PrescriptionDTO và add vào danh sách dịch vụ của bệnh nhân đó (Xử lý chống lỗi NullPointerException).
- Chuyển đổi Map.values() thành ArrayList để trả về kết quả cuối cùng.
 */

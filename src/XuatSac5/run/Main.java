package XuatSac5.run;

import XuatSac5.controller.PatientController;
import XuatSac5.util.DBConnection;
import util.DBConnect;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        util.DBConnect.initDatabase();
        PatientController controller = new PatientController();
        showMenu(controller);
    }

    public static void showMenu(PatientController controller) {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        do{
            try {
                System.out.println("\n--- RIKKEI HOSPITAL MENU ---");
                System.out.println("1. Xem tình trạng giường bệnh");
                System.out.println("2. Tiếp nhận bệnh nhân");
                System.out.println("3. Thoát");
                System.out.print("Lựa chọn của bạn: ");
                choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        controller.showEmptyBeds();
                        break;

                    case 2:
                        System.out.println("--- TIẾP NHẬN BỆNH NHÂN MỚI ---");
                        System.out.print("Tên BN: ");
                        String name = sc.nextLine();

                        System.out.print("Tuổi: ");
                        int age = Integer.parseInt(sc.nextLine());

                        System.out.print("Mã giường muốn chọn: ");
                        int bed = Integer.parseInt(sc.nextLine());

                        System.out.print("Số tiền tạm ứng: ");
                        double money = Double.parseDouble(sc.nextLine());

                        if (controller.receptionPatient(name, age, bed, money)) {
                            System.out.println("Bệnh nhân đã được tiếp nhận.");
                        } else {
                            System.out.println("Vui lòng kiểm tra lại mã giường hoặc kết nối.");
                        }
                        break;

                    case 3:
                        break;

                    default:
                        System.out.println("Lựa chọn không hợp lệ (1-3). Vui lòng chọn lại!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.err.println("LỖI: Vui lòng chỉ nhập con số, không nhập ký tự chữ!");
            } catch (Exception e) {
                System.err.println("Đã xảy ra lỗi hệ thống: " + e.getMessage());
            }
        }while(choice != 3);
    }
}


/*
1. Phân tích Rủi ro & Bẫy lỗi
Kịch bản 1: Sai lệch dữ liệu giữa các bảng
Kịch bản 2: Nhập sai kiểu dữ liệu gây treo ứng dụng
Kịch bản 3: Thao tác trên dữ liệu không tồn tại

2.Thiết kế Kiến trúc
Mở kết nối: Gọi DatabaseHelper.getConnection().
Tắt tự động lưu: conn.setAutoCommit(false).
Thực thi liên hoàn:
INSERT thông tin bệnh nhân mới.
UPDATE bảng giường: Chuyển trạng thái từ 'Trống' sang 'Đã có người'.
INSERT số tiền vào bảng tài chính (ví dụ: patient_wallets).
Xác nhận: Nếu 3 lệnh trên chạy không lỗi, gọi conn.commit().
Hủy bỏ: Nếu bất kỳ bước nào bắn ra SQLException, gọi conn.rollback() trong khối catch để đưa Database về trạng thái sạch ban đầu.
 */
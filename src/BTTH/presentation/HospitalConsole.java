package BTTH.presentation;
import BTTH.bussiness.HospitalBusiness;
import BTTH.util.DBConnect;

import java.sql.SQLException;
import java.util.Scanner;

public class HospitalConsole {
    public static void main(String[] args) {
        DBConnect.initDatabase();

        HospitalBusiness bus = new HospitalBusiness();
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("\n--- HỆ THỐNG QUẢN LÝ XUẤT VIỆN ---");
            System.out.print("Nhập mã bệnh nhân: ");
            int id = Integer.parseInt(sc.nextLine());

            System.out.print("Nhập số tiền hóa đơn: ");
            double fee = Double.parseDouble(sc.nextLine());

            bus.processDischarge(id, fee);

        } catch (NumberFormatException | SQLException e) {
            System.err.println("Lỗi: Vui lòng nhập số hợp lệ!");
        }
    }
}
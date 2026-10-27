package com.example.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Lab8Application {

    public static void main(String[] args) {

        String url = "jdbc:sqlite:nhanvien.db";

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            // Nap driver SQLite
            Class.forName("org.sqlite.JDBC");

            // Ket noi database
            conn = DriverManager.getConnection(url);

            System.out.println("Ket noi SQLite thanh cong!");

            // Tao Statement
            stmt = conn.createStatement();

            // Tao bang NhanVien
            String sqlCreate = """
                    CREATE TABLE IF NOT EXISTS NhanVien (
                        id INTEGER PRIMARY KEY,
                        ten TEXT NOT NULL,
                        chuc_vu TEXT
                    )
                    """;

            stmt.executeUpdate(sqlCreate);

            System.out.println("Tao bang NhanVien thanh cong!");

            // Them 3 nhan vien
            String sql1 = """
                    INSERT OR IGNORE INTO NhanVien(id, ten, chuc_vu)
                    VALUES(1, 'Nguyen Van An', 'Nhan vien')
                    """;

            String sql2 = """
                    INSERT OR IGNORE INTO NhanVien(id, ten, chuc_vu)
                    VALUES(2, 'Tran Thi Binh', 'Ke toan')
                    """;

            String sql3 = """
                    INSERT OR IGNORE INTO NhanVien(id, ten, chuc_vu)
                    VALUES(3, 'Le Van Nam', 'Quan ly')
                    """;

            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);

            System.out.println("Them nhan vien thanh cong!");

            // Truy van
            String sqlSelect = "SELECT * FROM NhanVien";

            rs = stmt.executeQuery(sqlSelect);

            System.out.println();
            System.out.println("===== DANH SACH NHAN VIEN =====");

            while (rs.next()) {

                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");

                System.out.println(
                        id + " | "
                        + ten + " | "
                        + chucVu
                );
            }

        } catch (ClassNotFoundException e) {

            System.out.println("Khong tim thay SQLite Driver");
            e.printStackTrace();

        } catch (SQLException e) {

            System.out.println("Loi SQL");
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

                if (stmt != null) {
                    stmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }

        // =============================
        // CHAY PHAN BAI TAP SAN PHAM
        // =============================
        testSanPham();
    }


    // =====================================
    // HAM TEST CRUD SAN PHAM
    // =====================================
    public static void testSanPham() {

        SanPhamDAO dao = new SanPhamDAO();

        // 1. Tao bang SanPham
        dao.taoBangSanPham();


        // 2. Tao 3 san pham
        SanPham sp1 = new SanPham(
                1,
                "Laptop Dell",
                15000000,
                10
        );

        SanPham sp2 = new SanPham(
                2,
                "Chuot Logitech",
                500000,
                20
        );

        SanPham sp3 = new SanPham(
                3,
                "Ban phim co",
                1200000,
                15
        );


        // 3. CREATE - Them san pham
        dao.themSanPham(sp1);
        dao.themSanPham(sp2);
        dao.themSanPham(sp3);


        // 4. READ - Hien thi tat ca
        dao.hienThiTatCa();


        // 5. READ - Tim theo ten
        dao.timTheoTen("Dell");


        // 6. UPDATE - Cap nhat gia
        dao.capNhatGia(
                1,
                14000000
        );


        // 7. UPDATE - Cap nhat so luong
        dao.capNhatSoLuong(
                2,
                30
        );


        // 8. DELETE - Xoa san pham
        dao.xoaSanPham(3);


        // 9. Hien thi lai sau khi sua va xoa
        dao.hienThiTatCa();
    }
}
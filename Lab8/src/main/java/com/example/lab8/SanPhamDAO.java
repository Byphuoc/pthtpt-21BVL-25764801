package com.example.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SanPhamDAO {

    // Dung chung database voi bang NhanVien
    private static final String URL = "jdbc:sqlite:nhanvien.db";


    // ==========================
    // 1. KET NOI DATABASE
    // ==========================
    private Connection getConnection() throws SQLException {

        Connection conn = DriverManager.getConnection(URL);

        return conn;
    }


    // ==========================
    // 2. TAO BANG SANPHAM
    // ==========================
    public void taoBangSanPham() {

        Connection conn = null;
        Statement stmt = null;

        String sql = """
                CREATE TABLE IF NOT EXISTS SanPham (
                    maSP INTEGER PRIMARY KEY,
                    tenSP TEXT NOT NULL,
                    gia REAL,
                    soluong INTEGER
                )
                """;

        try {

            conn = getConnection();

            stmt = conn.createStatement();

            stmt.executeUpdate(sql);

            System.out.println("Tao bang SanPham thanh cong!");

        } catch (SQLException e) {

            System.out.println("Loi tao bang SanPham!");
            e.printStackTrace();

        } finally {

            try {

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
    }


    // ==========================
    // 3. CREATE - THEM SAN PHAM
    // ==========================
    public void themSanPham(SanPham sp) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = """
                INSERT OR IGNORE INTO SanPham
                (maSP, tenSP, gia, soluong)
                VALUES (?, ?, ?, ?)
                """;

        try {

            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, sp.getMaSP());

            pstmt.setString(2, sp.getTenSP());

            pstmt.setDouble(3, sp.getGia());

            pstmt.setInt(4, sp.getSoLuong());

            int ketQua = pstmt.executeUpdate();

            if (ketQua > 0) {

                System.out.println(
                        "Them san pham thanh cong: "
                        + sp.getTenSP()
                );

            } else {

                System.out.println(
                        "San pham ma "
                        + sp.getMaSP()
                        + " da ton tai!"
                );
            }

        } catch (SQLException e) {

            System.out.println("Loi them san pham!");
            e.printStackTrace();

        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ==========================
    // 4. READ - HIEN THI TAT CA
    // ==========================
    public void hienThiTatCa() {

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT * FROM SanPham";

        try {

            conn = getConnection();

            stmt = conn.createStatement();

            rs = stmt.executeQuery(sql);

            System.out.println();
            System.out.println("===== DANH SACH SAN PHAM =====");

            while (rs.next()) {

                int maSP = rs.getInt("maSP");

                String tenSP = rs.getString("tenSP");

                double gia = rs.getDouble("gia");

                int soLuong = rs.getInt("soluong");

                System.out.println(
                        maSP
                        + " | "
                        + tenSP
                        + " | "
                        + gia
                        + " | "
                        + soLuong
                );
            }

        } catch (SQLException e) {

            System.out.println("Loi hien thi san pham!");
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
    }


    // ==========================
    // 5. READ - TIM THEO TEN
    // ==========================
    public void timTheoTen(String tenCanTim) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String sql = """
                SELECT *
                FROM SanPham
                WHERE tenSP LIKE ?
                """;

        try {

            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setString(
                    1,
                    "%" + tenCanTim + "%"
            );

            rs = pstmt.executeQuery();

            System.out.println();
            System.out.println(
                    "===== KET QUA TIM KIEM ====="
            );

            boolean timThay = false;

            while (rs.next()) {

                timThay = true;

                System.out.println(
                        rs.getInt("maSP")
                        + " | "
                        + rs.getString("tenSP")
                        + " | "
                        + rs.getDouble("gia")
                        + " | "
                        + rs.getInt("soluong")
                );
            }

            if (!timThay) {

                System.out.println(
                        "Khong tim thay san pham!"
                );
            }

        } catch (SQLException e) {

            System.out.println("Loi tim san pham!");
            e.printStackTrace();

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ==========================
    // 6. UPDATE - CAP NHAT GIA
    // ==========================
    public void capNhatGia(
            int maSP,
            double giaMoi) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = """
                UPDATE SanPham
                SET gia = ?
                WHERE maSP = ?
                """;

        try {

            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setDouble(1, giaMoi);

            pstmt.setInt(2, maSP);

            int ketQua = pstmt.executeUpdate();

            if (ketQua > 0) {

                System.out.println(
                        "Cap nhat gia thanh cong!"
                );

            } else {

                System.out.println(
                        "Khong tim thay maSP: "
                        + maSP
                );
            }

        } catch (SQLException e) {

            System.out.println("Loi cap nhat gia!");
            e.printStackTrace();

        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ==========================
    // 7. UPDATE - CAP NHAT SO LUONG
    // ==========================
    public void capNhatSoLuong(
            int maSP,
            int soLuongMoi) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = """
                UPDATE SanPham
                SET soluong = ?
                WHERE maSP = ?
                """;

        try {

            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, soLuongMoi);

            pstmt.setInt(2, maSP);

            int ketQua = pstmt.executeUpdate();

            if (ketQua > 0) {

                System.out.println(
                        "Cap nhat so luong thanh cong!"
                );

            } else {

                System.out.println(
                        "Khong tim thay maSP: "
                        + maSP
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Loi cap nhat so luong!"
            );

            e.printStackTrace();

        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // ==========================
    // 8. DELETE - XOA SAN PHAM
    // ==========================
    public void xoaSanPham(int maSP) {

        Connection conn = null;
        PreparedStatement pstmt = null;

        String sql = """
                DELETE FROM SanPham
                WHERE maSP = ?
                """;

        try {

            conn = getConnection();

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, maSP);

            int ketQua = pstmt.executeUpdate();

            if (ketQua > 0) {

                System.out.println(
                        "Xoa san pham thanh cong!"
                );

            } else {

                System.out.println(
                        "Khong tim thay maSP: "
                        + maSP
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Loi xoa san pham!"
            );

            e.printStackTrace();

        } finally {

            try {

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
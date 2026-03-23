package BaoCaoCuoiKi;

import java.sql.*;

/**
 * DAO – thao tác CRUD tài khoản người dùng trên MySQL.
 *
 * Bảng cần có (chạy một lần):
 * ─────────────────────────────────────────────────────────────
 * CREATE DATABASE IF NOT EXISTS baam_db CHARACTER SET utf8mb4;
 * USE baam_db;
 *
 * CREATE TABLE IF NOT EXISTS nguoi_dung (
 *     id            INT AUTO_INCREMENT PRIMARY KEY,
 *     ten_dang_nhap VARCHAR(100) NOT NULL UNIQUE,
 *     mat_khau_hash VARCHAR(255) NOT NULL,
 *     email         VARCHAR(255) NOT NULL UNIQUE,
 *     anh_dai_dien  LONGBLOB,
 *     ngay_tao      DATETIME DEFAULT CURRENT_TIMESTAMP
 * );
 *
 * CREATE TABLE IF NOT EXISTS ma_xac_thuc (
 *     id         INT AUTO_INCREMENT PRIMARY KEY,
 *     email      VARCHAR(255) NOT NULL,
 *     ma         VARCHAR(10)  NOT NULL,
 *     loai       ENUM('DANG_KI','QUEN_MK') NOT NULL,
 *     het_han    DATETIME NOT NULL,
 *     da_dung    TINYINT(1) DEFAULT 0,
 *     INDEX idx_email_loai (email, loai)
 * );
 * ─────────────────────────────────────────────────────────────
 */
public class NguoiDungDAO {

    // ── Kiểm tra tồn tại ─────────────────────────────────────

    public boolean tonTaiTenDangNhap(String ten) throws SQLException {
        String sql = "SELECT 1 FROM nguoi_dung WHERE ten_dang_nhap = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ten);
            return ps.executeQuery().next();
        }
    }

    public boolean tonTaiEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM nguoi_dung WHERE email = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            return ps.executeQuery().next();
        }
    }

    // ── Đăng ký ──────────────────────────────────────────────

    /**
     * Thêm tài khoản mới. matKhauHash đã được hash BCrypt trước khi truyền vào.
     * Trả về id được sinh ra, hoặc -1 nếu thất bại.
     */
    public int themNguoiDung(NguoiDung nd) throws SQLException {
        String sql = "INSERT INTO nguoi_dung (ten_dang_nhap, mat_khau_hash, email, anh_dai_dien) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nd.getTenDangNhap());
            ps.setString(2, nd.getMatKhauHash());
            ps.setString(3, nd.getEmail());
            // Ảnh đại diện (có thể null)
            if (nd.getAnhDaiDien() != null) {
                java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                javax.imageio.ImageIO.write(nd.getAnhDaiDien(), "png", baos);
                ps.setBytes(4, baos.toByteArray());
            } else {
                ps.setNull(4, Types.BLOB);
            }
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (java.io.IOException e) {
            throw new SQLException("Lỗi xử lý ảnh: " + e.getMessage());
        }
    }

    // ── Đăng nhập ────────────────────────────────────────────

    /**
     * Tìm người dùng theo tên đăng nhập HOẶC email.
     * Trả về NguoiDung (chứa hash) hoặc null nếu không tìm thấy.
     */
    public NguoiDung timTheoTenHoacEmail(String taiKhoan) throws SQLException {
        String sql = "SELECT id, ten_dang_nhap, mat_khau_hash, email " +
                     "FROM nguoi_dung WHERE ten_dang_nhap = ? OR email = ? LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            ps.setString(2, taiKhoan);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NguoiDung(
                    rs.getInt("id"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau_hash"),
                    rs.getString("email")
                );
            }
            return null;
        }
    }

    // ── Quên mật khẩu ────────────────────────────────────────

    public boolean emailTonTai(String email) throws SQLException {
        return tonTaiEmail(email);
    }

    /** Cập nhật mật khẩu mới (đã hash) theo email. */
    public boolean capNhatMatKhau(String email, String matKhauHashMoi) throws SQLException {
        String sql = "UPDATE nguoi_dung SET mat_khau_hash = ? WHERE email = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, matKhauHashMoi);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Mã xác thực ──────────────────────────────────────────

    /**
     * Lưu mã xác thực vào bảng ma_xac_thuc.
     * loai: "DANG_KI" hoặc "QUEN_MK"
     * hetHanPhut: số phút hiệu lực
     */
    public void luuMaXacThuc(String email, String ma, String loai, int hetHanPhut)
            throws SQLException {
        // Huỷ các mã cũ chưa dùng
        String xoa = "UPDATE ma_xac_thuc SET da_dung = 1 WHERE email = ? AND loai = ? AND da_dung = 0";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(xoa)) {
            ps.setString(1, email); ps.setString(2, loai); ps.executeUpdate();
        }
        String sql = "INSERT INTO ma_xac_thuc (email, ma, loai, het_han) " +
                     "VALUES (?, ?, ?, DATE_ADD(NOW(), INTERVAL ? MINUTE))";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, ma);
            ps.setString(3, loai);
            ps.setInt(4, hetHanPhut);
            ps.executeUpdate();
        }
    }

    /**
     * Xác minh mã. Trả về true nếu mã đúng, còn hiệu lực, chưa dùng.
     * Nếu đúng → đánh dấu đã dùng.
     */
    public boolean xacMinhMa(String email, String ma, String loai) throws SQLException {
        String sql = "SELECT id FROM ma_xac_thuc " +
                     "WHERE email = ? AND ma = ? AND loai = ? AND da_dung = 0 AND het_han > NOW() " +
                     "LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, email); ps.setString(2, ma); ps.setString(3, loai);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String upd = "UPDATE ma_xac_thuc SET da_dung = 1 WHERE id = ?";
                try (PreparedStatement pu = DatabaseConnection.getConnection().prepareStatement(upd)) {
                    pu.setInt(1, id); pu.executeUpdate();
                }
                return true;
            }
            return false;
        }
    }
}

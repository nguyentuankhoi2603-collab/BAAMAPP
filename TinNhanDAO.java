package BaoCaoCuoiKi;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO – thao tác bảng tin_nhan.
 *
 * DDL cần chạy trước:
 * ─────────────────────────────────────────────────────────
 * CREATE TABLE IF NOT EXISTS tin_nhan (
 *     id            INT AUTO_INCREMENT PRIMARY KEY,
 *     nhom_id       INT      NOT NULL,
 *     nguoi_gui_id  INT      NOT NULL,
 *     noi_dung      TEXT     NOT NULL,
 *     thoi_gian     DATETIME DEFAULT CURRENT_TIMESTAMP,
 *     FOREIGN KEY (nhom_id)      REFERENCES nhom(id)       ON DELETE CASCADE,
 *     FOREIGN KEY (nguoi_gui_id) REFERENCES nguoi_dung(id) ON DELETE CASCADE,
 *     INDEX idx_nhom (nhom_id, thoi_gian)
 * );
 * ─────────────────────────────────────────────────────────
 */
public class TinNhanDAO {

    /**
     * Lưu tin nhắn mới. Trả về id sinh ra, hoặc -1 nếu lỗi.
     */
    public int guiTinNhan(TinNhan tin) throws SQLException {
        String sql = "INSERT INTO tin_nhan (nhom_id, nguoi_gui_id, noi_dung) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection()
                .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tin.getNhomId());
            ps.setInt(2, tin.getNguoiGuiId());
            ps.setString(3, tin.getNoiDung());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    /**
     * 50 tin nhắn gần nhất của nhóm, sắp xếp cũ → mới.
     */
    public List<TinNhan> layTinNhanNhom(int nhomId) throws SQLException {
        String sql =
            "SELECT t.id, t.nhom_id, t.nguoi_gui_id, " +
            "       n.ten_dang_nhap AS ten_nguoi_gui, " +
            "       t.noi_dung, t.thoi_gian " +
            "FROM   tin_nhan t " +
            "JOIN   nguoi_dung n ON n.id = t.nguoi_gui_id " +
            "WHERE  t.nhom_id = ? " +
            "ORDER  BY t.thoi_gian DESC LIMIT 50";

        List<TinNhan> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nhomId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(0, map(rs));   // đảo: cũ → mới
        }
        return list;
    }

    /**
     * Tin nhắn mới hơn lastId – dùng khi polling cập nhật real-time.
     */
    public List<TinNhan> layTinMoi(int nhomId, int lastId) throws SQLException {
        String sql =
            "SELECT t.id, t.nhom_id, t.nguoi_gui_id, " +
            "       n.ten_dang_nhap AS ten_nguoi_gui, " +
            "       t.noi_dung, t.thoi_gian " +
            "FROM   tin_nhan t " +
            "JOIN   nguoi_dung n ON n.id = t.nguoi_gui_id " +
            "WHERE  t.nhom_id = ? AND t.id > ? " +
            "ORDER  BY t.thoi_gian ASC";

        List<TinNhan> list = new ArrayList<>();
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nhomId);
            ps.setInt(2, lastId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── Helper map ResultSet → TinNhan ────────────────────────
    private TinNhan map(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("thoi_gian");
        LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        return new TinNhan(
            rs.getInt("id"),
            rs.getInt("nhom_id"),
            rs.getInt("nguoi_gui_id"),
            rs.getString("ten_nguoi_gui"),
            rs.getString("noi_dung"),
            ldt
        );
    }
}

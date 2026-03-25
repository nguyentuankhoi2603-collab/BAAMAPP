package BaoCaoCuoiKi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model – một tin nhắn trong nhóm chat.
 */
public class TinNhan {

    private int    id;
    private int    nhomId;
    private int    nguoiGuiId;
    private String tenNguoiGui;
    private String noiDung;
    private LocalDateTime thoiGian;

    // ── Constructor đầy đủ (load từ DB) ──────────────────────
    public TinNhan(int id, int nhomId, int nguoiGuiId,
                   String tenNguoiGui, String noiDung, LocalDateTime thoiGian) {
        this.id           = id;
        this.nhomId       = nhomId;
        this.nguoiGuiId   = nguoiGuiId;
        this.tenNguoiGui  = tenNguoiGui;
        this.noiDung      = noiDung;
        this.thoiGian     = thoiGian;
    }

    // ── Constructor gửi mới (chưa có id / thoiGian) ──────────
    public TinNhan(int nhomId, int nguoiGuiId, String tenNguoiGui, String noiDung) {
        this.nhomId      = nhomId;
        this.nguoiGuiId  = nguoiGuiId;
        this.tenNguoiGui = tenNguoiGui;
        this.noiDung     = noiDung;
        this.thoiGian    = LocalDateTime.now();
    }

    // ── Getters ───────────────────────────────────────────────
    public int            getId()           { return id; }
    public int            getNhomId()       { return nhomId; }
    public int            getNguoiGuiId()   { return nguoiGuiId; }
    public String         getTenNguoiGui()  { return tenNguoiGui; }
    public String         getNoiDung()      { return noiDung; }
    public LocalDateTime  getThoiGian()     { return thoiGian; }

    /** HH:mm để hiển thị trong bubble */
    public String getThoiGianStr() {
        if (thoiGian == null) return "";
        return thoiGian.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}

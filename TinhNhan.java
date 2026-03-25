package BaoCaoCuoiKi;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TinNhan implements Serializable {
    private static final long serialVersionUID = 1L;

    private int nhomId;
    private int nguoiGuiId;
    private String tenNguoiGui;
    private String noiDung;
    private LocalDateTime thoiGian;

    // ── Constructor (client gửi, chưa có time) ──
    public TinNhan(int nhomId, int nguoiGuiId, String tenNguoiGui, String noiDung) {
        this.nhomId = nhomId;
        this.nguoiGuiId = nguoiGuiId;
        this.tenNguoiGui = tenNguoiGui;
        this.noiDung = noiDung;
        this.thoiGian = LocalDateTime.now();
    }

    // ── Constructor (từ DB, đã có time) ──
    public TinNhan(int nhomId, int nguoiGuiId, String tenNguoiGui, String noiDung, LocalDateTime thoiGian) {
        this.nhomId = nhomId;
        this.nguoiGuiId = nguoiGuiId;
        this.tenNguoiGui = tenNguoiGui;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
    }

    // ── Getters ──
    public int getNhomId() {
        return nhomId;
    }

    public int getNguoiGuiId() {
        return nguoiGuiId;
    }

    public String getTenNguoiGui() {
        return tenNguoiGui;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    // ✅ Phương thức format thời gian cho hiển thị
    public String getThoiGianStr() {
        if (thoiGian == null) {
            return "Vừa xong";
        }
        return thoiGian.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    // ✅ Setter cho server/controller set thời gian
    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public void setThoiGian(String thoiGianStr) {
        try {
            this.thoiGian = LocalDateTime.parse(thoiGianStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            this.thoiGian = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "TinNhan{" +
                "nhomId=" + nhomId +
                ", nguoiGuiId=" + nguoiGuiId +
                ", tenNguoiGui='" + tenNguoiGui + '\'' +
                ", noiDung='" + noiDung + '\'' +
                ", thoiGian=" + thoiGian +
                '}';
    }
}

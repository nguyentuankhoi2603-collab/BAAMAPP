package BaoCaoCuoiKi;

import java.awt.image.BufferedImage;

/**
 * Model – đại diện cho một tài khoản người dùng.
 */
public class NguoiDung {

    private int    id;
    private String tenDangNhap;
    private String matKhauHash;   // BCrypt hash
    private String email;
    private BufferedImage anhDaiDien;

    public NguoiDung() {}

    public NguoiDung(int id, String tenDangNhap, String matKhauHash, String email) {
        this.id           = id;
        this.tenDangNhap  = tenDangNhap;
        this.matKhauHash  = matKhauHash;
        this.email        = email;
    }

    // ── Getters / Setters ─────────────────────────────────────
    public int    getId()           { return id; }
    public void   setId(int id)     { this.id = id; }

    public String getTenDangNhap()                  { return tenDangNhap; }
    public void   setTenDangNhap(String tenDangNhap){ this.tenDangNhap = tenDangNhap; }

    public String getMatKhauHash()                  { return matKhauHash; }
    public void   setMatKhauHash(String hash)       { this.matKhauHash = hash; }

    public String getEmail()                        { return email; }
    public void   setEmail(String email)            { this.email = email; }

    public BufferedImage getAnhDaiDien()            { return anhDaiDien; }
    public void setAnhDaiDien(BufferedImage img)    { this.anhDaiDien = img; }

    @Override
    public String toString() {
        return "NguoiDung{id=" + id + ", ten='" + tenDangNhap + "', email='" + email + "'}";
    }
}

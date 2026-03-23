package BaoCaoCuoiKi;

import java.awt.event.*;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * DangKiController – xử lý toàn bộ logic màn hình đăng ký.
 *
 * Luồng:
 *   1. Người dùng điền thông tin
 *   2. Nhấn "Lấy mã" → gửi OTP qua email
 *   3. Nhập mã OTP → nhấn "Đăng ký"
 *   4. Xác minh mã → tạo tài khoản → quay lại đăng nhập
 */
public class DangKiController {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MK_MIN_LENGTH = 8;

    private final DangKiUI    view;
    private final DangNhapUI  dangNhapView; // để quay lại
    private final NguoiDungDAO dao;

    private String emailDangGui = null; // email đã gửi OTP, chờ xác minh

    // ── Constructor ───────────────────────────────────────────
    public DangKiController(DangKiUI view, DangNhapUI dangNhapView) {
        this.view         = view;
        this.dangNhapView = dangNhapView;
        this.dao          = new NguoiDungDAO();
        khoiTao();
    }

    private void khoiTao() {
        view.btnLayMa.addActionListener(e -> xuLyLayMa());
        view.btnDangKi.addActionListener(e -> xuLyDangKi());

        // Đóng cửa sổ → hiện lại đăng nhập
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (dangNhapView != null) dangNhapView.setVisible(true);
            }
        });
    }

    // ══════════════════════════════════════════════════════════
    // Bước 1: Lấy mã OTP
    // ══════════════════════════════════════════════════════════
    private void xuLyLayMa() {
        String email = view.txtEmail.getText().trim();

        if (email.isEmpty() || isPlaceholder(email)) {
            view.showWarning("Vui lòng nhập địa chỉ email trước khi lấy mã.");
            view.txtEmail.requestFocus();
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            view.showWarning("Địa chỉ email không hợp lệ.");
            view.txtEmail.requestFocus();
            return;
        }

        view.btnLayMa.setEnabled(false);
        view.btnLayMa.setText("Đang gửi...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            String loi = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    if (dao.tonTaiEmail(email)) {
                        loi = "Email này đã được đăng ký.\nVui lòng dùng email khác hoặc đăng nhập.";
                        return null;
                    }
                    String ma = EmailService.taoMa();
                    dao.luuMaXacThuc(email, ma, "DANG_KI", EmailService.getHetHanPhut());
                    EmailService.guiMaOTP(email, ma, "DANG_KI");
                    emailDangGui = email;
                } catch (Exception ex) {
                    loi = "Không thể gửi email:\n" + ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                view.btnLayMa.setEnabled(true);
                view.btnLayMa.setText("Lấy mã");
                if (loi != null) {
                    view.showWarning(loi);
                } else {
                    view.showInfo("Đã gửi mã",
                        "Mã xác thực đã được gửi đến:\n" + email +
                        "\nKiểm tra hộp thư (kể cả Spam).\nMã có hiệu lực " +
                        EmailService.getHetHanPhut() + " phút.");
                    demNguocLayMa(); // bắt đầu đếm ngược 60s
                }
            }
        };
        worker.execute();
    }

    /** Đếm ngược 60 giây trước khi cho lấy mã lại */
    private void demNguocLayMa() {
        view.btnLayMa.setEnabled(false);
        int[] giay = {60};
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            giay[0]--;
            view.btnLayMa.setText(giay[0] + "s");
            if (giay[0] <= 0) {
                ((Timer) e.getSource()).stop();
                view.btnLayMa.setText("Lấy lại");
                view.btnLayMa.setEnabled(true);
            }
        });
        timer.start();
    }

    // ══════════════════════════════════════════════════════════
    // Bước 2: Đăng ký
    // ══════════════════════════════════════════════════════════
    private void xuLyDangKi() {
        String ten    = view.txtTen.getText().trim();
        String mk     = new String(view.txtMK.getPassword());
        String xnmk   = new String(view.txtXNMK.getPassword());
        String email  = view.txtEmail.getText().trim();
        String ma     = view.txtMa.getText().trim();

        // ── Validate ──────────────────────────────────────────
        if (ten.isEmpty() || isPlaceholder(ten)) {
            view.showWarning("Vui lòng nhập tên đăng nhập."); view.txtTen.requestFocus(); return;
        }
        if (ten.length() < 4 || ten.length() > 50) {
            view.showWarning("Tên đăng nhập phải từ 4–50 ký tự."); view.txtTen.requestFocus(); return;
        }
        if (!ten.matches("[A-Za-z0-9_]+")) {
            view.showWarning("Tên đăng nhập chỉ được chứa chữ, số và dấu gạch dưới (_)."); return;
        }
        if (mk.isEmpty()) {
            view.showWarning("Vui lòng nhập mật khẩu."); view.txtMK.requestFocus(); return;
        }
        if (mk.length() < MK_MIN_LENGTH) {
            view.showWarning("Mật khẩu phải ít nhất " + MK_MIN_LENGTH + " ký tự."); return;
        }
        if (!mk.equals(xnmk)) {
            view.showWarning("Mật khẩu xác nhận không khớp."); view.txtXNMK.requestFocus(); return;
        }
        if (email.isEmpty() || isPlaceholder(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            view.showWarning("Vui lòng nhập đúng địa chỉ email."); view.txtEmail.requestFocus(); return;
        }
        if (emailDangGui == null || !emailDangGui.equals(email)) {
            view.showWarning("Vui lòng nhấn 'Lấy mã' để gửi mã xác thực đến email này."); return;
        }
        if (ma.isEmpty() || isPlaceholder(ma)) {
            view.showWarning("Vui lòng nhập mã xác thực."); view.txtMa.requestFocus(); return;
        }

        view.btnDangKi.setEnabled(false);
        view.btnDangKi.setText("Đang xử lý...");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Kiểm tra trùng tên
                if (dao.tonTaiTenDangNhap(ten))
                    return "Tên đăng nhập '" + ten + "' đã được sử dụng.";
                // Kiểm tra trùng email
                if (dao.tonTaiEmail(email))
                    return "Email '" + email + "' đã được đăng ký.";
                // Xác minh mã OTP
                if (!dao.xacMinhMa(email, ma, "DANG_KI"))
                    return "Mã xác thực không đúng hoặc đã hết hiệu lực.\nVui lòng lấy mã mới.";
                // Tạo tài khoản
                NguoiDung nd = new NguoiDung();
                nd.setTenDangNhap(ten);
                nd.setMatKhauHash(PasswordUtil.hash(mk));
                nd.setEmail(email);
                nd.setAnhDaiDien(view.avatarPanel.getImage());
                int newId = dao.themNguoiDung(nd);
                if (newId < 0) return "Tạo tài khoản thất bại. Vui lòng thử lại.";
                return null; // null = thành công
            }

            @Override
            protected void done() {
                view.btnDangKi.setEnabled(true);
                view.btnDangKi.setText("Tạo tài khoản  →");
                try {
                    String loi = get();
                    if (loi != null) {
                        view.showWarning(loi);
                    } else {
                        view.showInfo("Đăng ký thành công",
                            "Tài khoản '" + ten + "' đã được tạo!\nBạn có thể đăng nhập ngay bây giờ.");
                        view.dispose();
                        if (dangNhapView != null) {
                            // Tự điền sẵn tên đăng nhập
                            dangNhapView.txtTaiKhoan.setText(ten);
                            dangNhapView.txtTaiKhoan.setForeground(DangNhapUI.LABEL_FG);
                            dangNhapView.setVisible(true);
                        }
                    }
                } catch (Exception ex) {
                    view.showWarning("Lỗi hệ thống:\n" + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── Helper ───────────────────────────────────────────────
    private boolean isPlaceholder(String text) {
        return text.startsWith("👤") || text.startsWith("✉")
            || text.startsWith("🔑") || text.equals("example@email.com");
    }
}

package BaoCaoCuoiKi;

import java.awt.event.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.SwingWorker;

/**
 * QuenMKController – xử lý luồng 3 bước quên mật khẩu.
 *
 * Bước 1: Nhập email → gửi OTP
 * Bước 2: Nhập OTP → xác minh
 * Bước 3: Nhập mật khẩu mới → cập nhật DB
 */
public class QuenMKController {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final int MK_MIN_LENGTH = 8;

    private final QuenMKUI   view;
    private final DangNhapUI dangNhapView;
    private final NguoiDungDAO dao;

    private String emailDaXacMinh = null; // email sau khi OTP xác minh thành công

    // ── Constructor ───────────────────────────────────────────
    public QuenMKController(QuenMKUI view, DangNhapUI dangNhapView) {
        this.view         = view;
        this.dangNhapView = dangNhapView;
        this.dao          = new NguoiDungDAO();
        khoiTao();
    }

    private void khoiTao() {
        // Bước 1
        view.btnGuiMa.addActionListener(e -> xuLyGuiMa());
        view.txtEmail.addActionListener(e -> xuLyGuiMa());

        // Bước 2
        view.btnXacMinhMa.addActionListener(e -> xuLyXacMinhMa());
        view.txtMa.addActionListener(e -> xuLyXacMinhMa());

        // Bước 3
        view.btnDatLai.addActionListener(e -> xuLyDatLaiMatKhau());

        // Quay lại đăng nhập
        view.lblQuayLai.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { quayLaiDangNhap(); }
        });

        // Đóng cửa sổ X
        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { quayLaiDangNhap(); }
        });
    }

    // ══════════════════════════════════════════════════════════
    // Bước 1: Gửi OTP
    // ══════════════════════════════════════════════════════════
    private void xuLyGuiMa() {
        String email = view.txtEmail.getText().trim();

        if (email.isEmpty() || isPlaceholder(email)) {
            view.showWarning("Vui lòng nhập địa chỉ email."); return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            view.showWarning("Địa chỉ email không hợp lệ."); return;
        }

        view.setBtnLoading(view.btnGuiMa, true, "Gửi mã xác thực  →");

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>()  {
            @Override
            protected String doInBackground() throws Exception {
                if (!dao.emailTonTai(email))
                    return "Email này chưa được đăng ký trong hệ thống.";
                String ma = EmailService.taoMa();
                dao.luuMaXacThuc(email, ma, "QUEN_MK", EmailService.getHetHanPhut());
                EmailService.guiMaOTP(email, ma, "QUEN_MK");
                return null; // thành công
            }

            @Override
            protected void done() {
                view.setBtnLoading(view.btnGuiMa, false, "Gửi mã xác thực  →");
                try {
                    String loi = get();
                    if (loi != null) { view.showWarning(loi); return; }

                    // Chuyển sang bước 2
                    view.chuyenBuoc(2, email);
                    demNguocGuiLai();

                } catch (Exception ex) {
                    view.showError("Lỗi hệ thống:\n" + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    /** Đếm ngược 60s trên nút gửi (ẩn sau khi chuyển bước, nhưng giữ logic) */
    private void demNguocGuiLai() {
        // Không cần hiển thị vì đã chuyển panel, nhưng giữ logic phòng user quay lại
    }

    // ══════════════════════════════════════════════════════════
    // Bước 2: Xác minh OTP
    // ══════════════════════════════════════════════════════════
    private void xuLyXacMinhMa() {
        String email = view.txtEmail.getText().trim();
        String ma    = view.txtMa.getText().trim();

        if (ma.isEmpty() || isPlaceholder(ma)) {
            view.showWarning("Vui lòng nhập mã xác thực."); return;
        }
        if (ma.length() != 6 || !ma.matches("\\d+")) {
            view.showWarning("Mã xác thực gồm 6 chữ số."); return;
        }

        view.setBtnLoading(view.btnXacMinhMa, true, "Xác nhận mã  →");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>()  {
            @Override
            protected Boolean doInBackground() throws Exception {
                return dao.xacMinhMa(email, ma, "QUEN_MK");
            }

            @Override
            protected void done() {
                view.setBtnLoading(view.btnXacMinhMa, false, "Xác nhận mã  →");
                try {
                    if (get()) {
                        emailDaXacMinh = email;
                        view.chuyenBuoc(3, email);
                    } else {
                        view.showWarning("Mã xác thực không đúng hoặc đã hết hiệu lực.\n" +
                                         "Vui lòng quay lại và lấy mã mới.");
                    }
                } catch (Exception ex) {
                    view.showError("Lỗi hệ thống:\n" + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ══════════════════════════════════════════════════════════
    // Bước 3: Đặt lại mật khẩu
    // ══════════════════════════════════════════════════════════
    private void xuLyDatLaiMatKhau() {
        if (emailDaXacMinh == null) {
            view.showWarning("Phiên xác thực đã hết. Vui lòng bắt đầu lại."); return;
        }

        String mk   = new String(view.txtMKMoi.getPassword());
        String xnmk = new String(view.txtXNMKMoi.getPassword());

        if (mk.isEmpty()) {
            view.showWarning("Vui lòng nhập mật khẩu mới."); view.txtMKMoi.requestFocus(); return;
        }
        if (mk.length() < MK_MIN_LENGTH) {
            view.showWarning("Mật khẩu phải ít nhất " + MK_MIN_LENGTH + " ký tự."); return;
        }
        if (!mk.equals(xnmk)) {
            view.showWarning("Mật khẩu xác nhận không khớp."); view.txtXNMKMoi.requestFocus(); return;
        }

        view.setBtnLoading(view.btnDatLai, true, "Đặt lại mật khẩu  →");

        final String emailFinal = emailDaXacMinh;
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                String hash = PasswordUtil.hash(mk);
                return dao.capNhatMatKhau(emailFinal, hash);
            }

            @Override
            protected void done() {
                view.setBtnLoading(view.btnDatLai, false, "Đặt lại mật khẩu  →");
                try {
                    if (get()) {
                        view.showInfo("Thành công",
                            "Mật khẩu đã được đặt lại thành công!\nBạn có thể đăng nhập ngay bây giờ.");
                        emailDaXacMinh = null;
                        quayLaiDangNhap();
                    } else {
                        view.showError("Không thể cập nhật mật khẩu. Vui lòng thử lại.");
                    }
                } catch (Exception ex) {
                    view.showError("Lỗi hệ thống:\n" + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── Điều hướng ────────────────────────────────────────────
    private void quayLaiDangNhap() {
        view.dispose();
        if (dangNhapView != null) dangNhapView.setVisible(true);
    }

    // ── Helper ───────────────────────────────────────────────
    private boolean isPlaceholder(String text) {
        return text.startsWith("✉") || text.startsWith("🔑");
    }
}

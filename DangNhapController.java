package BaoCaoCuoiKi;

import java.awt.event.*;
import javax.swing.*;
import java.util.prefs.Preferences;

/**
 * DangNhapController – xử lý toàn bộ logic màn hình đăng nhập.
 *
 * Điều hướng:
 *   Đăng nhập thành công → ManHinhChinh
 *   Nhấn "Đăng ký"       → DangKiUI (view ẩn, quay lại khi đóng)
 *   Nhấn "Quên MK"       → QuenMKUI (view ẩn, quay lại khi đóng)
 */
public class DangNhapController {

    private static final String PREF_NODE    = "baam_app";
    private static final String PREF_TK      = "saved_taikhoan";
    private static final String PREF_GHI_NHO = "ghi_nho";

    private final DangNhapUI  view;
    private final NguoiDungDAO dao;

    public DangNhapController(DangNhapUI view) {
        this.view = view;
        this.dao  = new NguoiDungDAO();
        khoiTao();
    }

    private void khoiTao() {
        view.btnDangNhap.addActionListener(e -> xuLyDangNhap());
        view.txtTaiKhoan.addActionListener(e -> xuLyDangNhap());
        view.txtMatKhau.addActionListener(e -> xuLyDangNhap());

        view.lblQuenMatKhau.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { moQuenMatKhau(); }
        });

        view.lblDangKi.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { moDangKi(); }
        });

        loadGhiNho();
    }

    // ── Đăng nhập ─────────────────────────────────────────────
    private void xuLyDangNhap() {
        String taiKhoan = view.txtTaiKhoan.getText().trim();
        String matKhau  = new String(view.txtMatKhau.getPassword());

        if (taiKhoan.isEmpty() || isPlaceholder(taiKhoan)) {
            view.showWarning("Vui lòng nhập tên đăng nhập hoặc email.");
            view.txtTaiKhoan.requestFocus();
            return;
        }
        if (matKhau.isEmpty()) {
            view.showWarning("Vui lòng nhập mật khẩu.");
            view.txtMatKhau.requestFocus();
            return;
        }

        view.setLoading(true);

        SwingWorker<NguoiDung, Void> worker = new SwingWorker<NguoiDung, Void>() {
            @Override
            protected NguoiDung doInBackground() throws Exception {
                return dao.timTheoTenHoacEmail(taiKhoan);
            }

            @Override
            protected void done() {
                view.setLoading(false);
                try {
                    NguoiDung nd = get();

                    if (nd == null) {
                        view.showError("Tài khoản không tồn tại.");
                        return;
                    }
                    if (!PasswordUtil.verify(matKhau, nd.getMatKhauHash())) {
                        view.showError("Mật khẩu không đúng.");
                        return;
                    }

                    luuGhiNho(taiKhoan);

                    // ── Mở màn hình chính ─────────────────────
                    view.dispose();
                    ManHinhChinh mhc = new ManHinhChinh(nd);
                    mhc.setVisible(true);

                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.showError("Lỗi kết nối cơ sở dữ liệu:\n" + msg);
                }
            }
        };
        worker.execute();
    }

    // ── Điều hướng ────────────────────────────────────────────
    private void moDangKi() {
        DangKiUI dangKiView = new DangKiUI();
        new DangKiController(dangKiView, view);
        dangKiView.setVisible(true);
        view.setVisible(false);
    }

    private void moQuenMatKhau() {
        QuenMKUI quenView = new QuenMKUI();
        new QuenMKController(quenView, view);
        quenView.setVisible(true);
        view.setVisible(false);
    }

    // ── Ghi nhớ ───────────────────────────────────────────────
    private void luuGhiNho(String taiKhoan) {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        if (view.chkGhiNho.isSelected()) {
            prefs.put(PREF_TK, taiKhoan);
            prefs.putBoolean(PREF_GHI_NHO, true);
        } else {
            prefs.remove(PREF_TK);
            prefs.putBoolean(PREF_GHI_NHO, false);
        }
    }

    private void loadGhiNho() {
        Preferences prefs = Preferences.userRoot().node(PREF_NODE);
        boolean ghiNho = prefs.getBoolean(PREF_GHI_NHO, false);
        if (ghiNho) {
            String saved = prefs.get(PREF_TK, "");
            if (!saved.isEmpty()) {
                view.txtTaiKhoan.setText(saved);
                view.txtTaiKhoan.setForeground(DangNhapUI.LABEL_FG);
                view.chkGhiNho.setSelected(true);
                view.txtMatKhau.requestFocusInWindow();
            }
        }
    }

    private boolean isPlaceholder(String text) {
        return text.startsWith("👤") || text.equals("Tên đăng nhập hoặc email...");
    }
}

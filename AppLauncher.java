package BaoCaoCuoiKi;

import javax.swing.*;

/**
 * AppLauncher – Điểm khởi động duy nhất của BAAM App.
 *
 * Luồng điều hướng:
 *   AppLauncher
 *     └─► DangNhapUI + DangNhapController
 *           ├─► (đăng nhập thành công) ManHinhChinh
 *           ├─► DangKiUI + DangKiController  → quay lại DangNhapUI
 *           └─► QuenMKUI + QuenMKController  → quay lại DangNhapUI
 */
public class AppLauncher {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Đặt Look & Feel hệ thống
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Khởi động màn hình đăng nhập
            DangNhapUI ui = new DangNhapUI();
            new DangNhapController(ui);
            ui.setVisible(true);
        });
    }
}

package BaoCaoCuoiKi;

import javax.swing.*;
import java.util.List;

public class NhomChatController {

    private final NhomChatPanel view;
    private final TinNhanDAO    dao;        // vẫn dùng để load lịch sử
    private final NguoiDung     nguoiDung;

    private ChatClient chatClient;

    // ── Constructor ───────────────────────────────────────────
    public NhomChatController(NhomChatPanel view, NguoiDung nguoiDung) {
        this.view      = view;
        this.dao       = new TinNhanDAO();
        this.nguoiDung = nguoiDung;

        // callback từ UI
        view.setGuiTinListener((nhomId, noiDung) -> xuLyGuiTin(noiDung));

        // load lịch sử từ DB
        taiLichSu();

        // kết nối server TCP
        khoiTaoSocket();
    }

    // ══════════════════════════════════════════════════════════
    // Kết nối server
    // ══════════════════════════════════════════════════════════
    private void khoiTaoSocket() {
        try {
            chatClient = new ChatClient("localhost", 1234, tin -> {
                SwingUtilities.invokeLater(() -> {
                    // tránh hiển thị lại tin của chính mình
                    if (tin.getNguoiGuiId() != nguoiDung.getId()) {
                    	view.nhanTinNhan(tin, nguoiDung.getId());
                    }
                });
            });
        } catch (Exception e) {
            showError("Không kết nối được server!");
        }
    }

    // ══════════════════════════════════════════════════════════
    // Load lịch sử từ DB
    // ══════════════════════════════════════════════════════════
    private void taiLichSu() {
        SwingWorker<List<TinNhan>, Void> worker = new SwingWorker<List<TinNhan>, Void>() {
            @Override
            protected List<TinNhan> doInBackground() throws Exception {
                return dao.layTinNhanNhom(view.getNhomId());
            }

            @Override
            protected void done() {
                try {
                    List<TinNhan> list = get();
                    view.clearChat();
                    for (TinNhan t : list) {
                        view.nhanTinNhan(t, nguoiDung.getId());
                    }
                } catch (Exception ex) {
                    showError("Không tải được lịch sử chat!");
                }
            }
        };
        worker.execute();
    }

    // ══════════════════════════════════════════════════════════
    // Gửi tin nhắn
    // ══════════════════════════════════════════════════════════
    private void xuLyGuiTin(String noiDung) {
        TinNhan tin = new TinNhan(
                view.getNhomId(),
                nguoiDung.getId(),
                nguoiDung.getTenDangNhap(),
                noiDung
        );

        // ✅ Hiển thị đúng cách
        view.nhanTinNhan(tin, nguoiDung.getId());

        // gửi TCP
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                chatClient.send(tin);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    showError("Gửi tin nhắn thất bại!");
                }
            }
        }.execute();
    }

    // ══════════════════════════════════════════════════════════
    // Đóng kết nối
    // ══════════════════════════════════════════════════════════
    public void destroy() {
        try {
            if (chatClient != null) {
                chatClient.close();
            }
        } catch (Exception ignored) {}
    }

    // ── Helper ────────────────────────────────────────────────
    private void showError(String msg) {
        JOptionPane.showMessageDialog(view, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

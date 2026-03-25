package BaoCaoCuoiKi;

import javax.swing.*;
import java.util.List;

public class NhomChatController {

    private final NhomChatPanel view;
    private final TinNhanDAO    dao;
    private final NguoiDung     nguoiDung;
    private ChatClient chatClient;
    private boolean isConnected = false;

    // ── Constructor ───────────────────────────────────────────
    public NhomChatController(NhomChatPanel view, NguoiDung nguoiDung) {
        this.view      = view;
        this.dao       = new TinNhanDAO();
        this.nguoiDung = nguoiDung;

        // ✅ Callback khi user gửi tin
        view.setGuiTinListener((nhomId, noiDung) -> xuLyGuiTin(nhomId, noiDung));

        // ✅ Load lịch sử từ DB
        taiLichSu();

        // ✅ Kết nối server TCP (async)
        khoiTaoSocket();
    }

    // ═════════════════════════════════════════════���════════════
    // Kết nối server
    // ══════════════════════════════════════════════════════════
    private void khoiTaoSocket() {
        new Thread(() -> {
            try {
                chatClient = new ChatClient("localhost", 1234, tin -> {
                    SwingUtilities.invokeLater(() -> {
                        // ✅ Hiển thị tin từ người khác
                        if (tin.getNguoiGuiId() != nguoiDung.getId()) {
                            view.nhanTinNhan(tin, nguoiDung.getId());
                        }
                    });
                });
                isConnected = true;
                System.out.println("[NhomChatController] ✅ Kết nối server thành công");
            } catch (Exception e) {
                isConnected = false;
                System.err.println("[NhomChatController ERROR] Không kết nối server: " + e.getMessage());
                SwingUtilities.invokeLater(() -> 
                    showError("⚠️ Không kết nối được server. Các tin nhắn sẽ chỉ lưu offline.")
                );
            }
        }).start();
    }

    // ══════════════════════════��═══════════════════════════════
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
                    System.out.println("[NhomChatController] ✅ Tải " + list.size() + " tin nhắn từ DB");
                } catch (Exception ex) {
                    System.err.println("[NhomChatController ERROR] " + ex.getMessage());
                    showError("Không tải được lịch sử chat!");
                }
            }
        };
        worker.execute();
    }

    // ══════════════════════════════════════════════════════════
    // Gửi tin nhắn
    // ══════════��═══════════════════════════════════════════════
    private void xuLyGuiTin(int nhomId, String noiDung) {
        TinNhan tin = new TinNhan(
                nhomId,
                nguoiDung.getId(),
                nguoiDung.getTenDangNhap(),
                noiDung
        );

        // ✅ Hiển thị tin của chính mình ngay lập tức
        view.nhanTinNhan(tin, nguoiDung.getId());

        // ✅ Gửi server + lưu DB
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // ✅ Gửi server (nếu kết nối được)
                if (isConnected && chatClient != null && chatClient.isConnected()) {
                    chatClient.send(tin);
                    System.out.println("[NhomChatController] ✅ Gửi tin lên server");
                } else {
                    System.out.println("[NhomChatController] ⚠️ Chưa kết nối server, chỉ lưu local");
                }
                
                // ✅ Luôn lưu vào DB
                try {
                    int tinId = dao.guiTinNhan(tin);
                    System.out.println("[NhomChatController] ✅ Lưu tin #" + tinId + " vào DB");
                } catch (Exception e) {
                    System.err.println("[NhomChatController DB ERROR] " + e.getMessage());
                }
                
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.err.println("[NhomChatController SEND ERROR] " + e.getMessage());
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
                isConnected = false;
            }
        } catch (Exception ignored) {}
    }

    // ── Helper ────────────────────────────────────────────────
    private void showError(String msg) {
        JOptionPane.showMessageDialog(view, msg, "⚠️ Thông báo", JOptionPane.WARNING_MESSAGE);
    }
}

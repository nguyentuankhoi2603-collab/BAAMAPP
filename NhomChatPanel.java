package BaoCaoCuoiKi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * NhomChatPanel – panel chat của một nhóm.
 *
 * Cách dùng trong ManHinhChinh:
 *   NhomChatPanel chatPanel = new NhomChatPanel(nhomId, tenNguoiDung);
 *   // Khi nhận tin từ DB / server:
 *   chatPanel.nhanTinNhan("Nguyễn A", "Xin chào!");
 *   // Khi đổi nhóm:
 *   chatPanel.loadNhom(nhomIdMoi, tenNhomMoi);
 */
public class NhomChatPanel extends JPanel {

    // ── Màu sắc ───────────────────────────────────────────────
    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color LABEL_FG    = new Color(0x0F172A);
    private static final Color HINT_FG     = new Color(0x64748B);
    private static final Color DIVIDER     = new Color(220, 228, 245);
    private static final Color BG_INPUT    = new Color(255, 255, 255, 220);

    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm");

    // ── State ─────────────────────────────────────────────────
    private int    nhomId;
    private String tenNguoiDung; // tên người đang đăng nhập

    /** Lưu lịch sử tin nhắn: [nguoi, noi_dung, gio] */
    private final ArrayList<String[]> lichSuTinNhan = new ArrayList<>();

    // ── Widgets ───────────────────────────────────────────────
    private JPanel     pnlMessages;
    private JScrollPane scrollPane;
    private JTextField  txtInput;
    private JButton     btnSend;

    /** Callback để Controller xử lý khi người dùng gửi tin */
    private GuiTinListener guiTinListener;

    // ── Interface cho Controller ──────────────────────────────
    public interface GuiTinListener {
        void onGuiTin(int nhomId, String noiDung);
    }

    // ── Constructor ───────────────────────────────────────────
    public NhomChatPanel(int nhomId, String tenNguoiDung) {
        this.nhomId       = nhomId;
        this.tenNguoiDung = tenNguoiDung;

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildMessagesArea(), BorderLayout.CENTER);
        add(buildInputBar(),     BorderLayout.SOUTH);
    }

    // ── Build vùng tin nhắn ───────────────────────────────────
    private JScrollPane buildMessagesArea() {
        pnlMessages = new JPanel();
        pnlMessages.setLayout(new BoxLayout(pnlMessages, BoxLayout.Y_AXIS));
        pnlMessages.setOpaque(false);
        pnlMessages.setBorder(new EmptyBorder(16, 16, 8, 16));

        scrollPane = new JScrollPane(pnlMessages);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    // ── Build thanh nhập ──────────────────────────────────────
    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 16, 14, 16));

        txtInput = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(DIVIDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtInput.setOpaque(false);
        txtInput.setFont(F_BODY);
        txtInput.setForeground(HINT_FG);
        txtInput.setCaretColor(ACCENT);
        txtInput.setBorder(new EmptyBorder(9, 16, 9, 16));
        txtInput.setPreferredSize(new Dimension(0, 40));

        // Placeholder
        final String PH = "Nhập tin nhắn...";
        txtInput.setText(PH);
        txtInput.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtInput.getText().equals(PH)) {
                    txtInput.setText("");
                    txtInput.setForeground(LABEL_FG);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtInput.getText().isEmpty()) {
                    txtInput.setText(PH);
                    txtInput.setForeground(HINT_FG);
                }
            }
        });

        // Enter gửi tin
        txtInput.addActionListener(e -> guiTinNhan());

        btnSend = new JButton("Gửi") {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? ACCENT_DARK : ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnSend.setFont(F_BOLD);
        btnSend.setForeground(Color.WHITE);
        btnSend.setContentAreaFilled(false);
        btnSend.setBorderPainted(false);
        btnSend.setFocusPainted(false);
        btnSend.setPreferredSize(new Dimension(64, 40));
        btnSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener(e -> guiTinNhan());

        bar.add(txtInput, BorderLayout.CENTER);
        bar.add(btnSend,  BorderLayout.EAST);
        return bar;
    }

    // ══════════════════════════════════════════════════════════
    // PUBLIC API (Controller gọi vào đây)
    // ══════════════════════════════════════════════════════════

    /** Đặt callback để Controller biết khi nào người dùng gửi tin */
    public void setGuiTinListener(GuiTinListener listener) {
        this.guiTinListener = listener;
    }

    /**
     * Controller gọi khi nhận được tin nhắn mới
     * (từ DB khi load lịch sử, hoặc từ polling server)
     */
    public void nhanTinNhan(String nguoi, String noiDung) {
        String gio = LocalTime.now().format(TIME_FMT);
        lichSuTinNhan.add(new String[]{nguoi, noiDung, gio});
        boolean mine = nguoi.equals(tenNguoiDung);
        themBubble(nguoi, noiDung, gio, mine);
    }

    /**
     * Đổi sang nhóm khác — xoá lịch sử hiện tại,
     * Controller sẽ load lịch sử nhóm mới sau khi gọi
     */
    public void loadNhom(int nhomIdMoi, String tenNhomMoi) {
        this.nhomId = nhomIdMoi;
        clearChat();
    }

    /** Xoá toàn bộ tin nhắn đang hiển thị */
    public void clearChat() {
        lichSuTinNhan.clear();
        pnlMessages.removeAll();
        pnlMessages.revalidate();
        pnlMessages.repaint();
    }

    public int    getNhomId()        { return nhomId; }
    public String getTenNguoiDung()  { return tenNguoiDung; }

    // ══════════════════════════════════════════════════════════
    // PRIVATE
    // ══════════════════════════════════════════════════════════

    /** Xử lý khi người dùng nhấn Gửi / Enter */
    private void guiTinNhan() {
        String text = txtInput.getText().trim();
        if (text.isEmpty() || text.equals("Nhập tin nhắn...")) return;

        txtInput.setText("");
        txtInput.setForeground(LABEL_FG);

        // Hiển thị ngay trên UI (optimistic update)
        nhanTinNhan(tenNguoiDung, text);

        // Báo cho Controller gửi lên DB
        if (guiTinListener != null) {
            guiTinListener.onGuiTin(nhomId, text);
        }
    }

    /** Tạo bubble tin nhắn và thêm vào panel */
    private void themBubble(String nguoi, String noiDung, String gio, boolean mine) {
        JPanel row = new JPanel(new FlowLayout(
                mine ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bubble = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(mine ? ACCENT : new Color(255, 255, 255, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                if (!mine) {
                    g2.setColor(DIVIDER);
                    g2.setStroke(new BasicStroke(0.8f));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12));

        // Tên người gửi (chỉ hiển thị nếu không phải mình)
        if (!mine) {
            JLabel lblNguoi = new JLabel(nguoi);
            lblNguoi.setFont(F_BOLD);
            lblNguoi.setForeground(ACCENT);
            bubble.add(lblNguoi);
            bubble.add(Box.createVerticalStrut(3));
        }

        // Nội dung
        JLabel lblText = new JLabel(
                "<html><body style='width:220px'>" + noiDung + "</body></html>");
        lblText.setFont(F_BODY);
        lblText.setForeground(mine ? Color.WHITE : LABEL_FG);
        bubble.add(lblText);

        // Giờ gửi
        bubble.add(Box.createVerticalStrut(3));
        JLabel lblGio = new JLabel(gio);
        lblGio.setFont(F_SMALL);
        lblGio.setForeground(mine ? new Color(255, 255, 255, 160) : HINT_FG);
        lblGio.setAlignmentX(mine ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        bubble.add(lblGio);

        row.add(bubble);
        pnlMessages.add(row);
        pnlMessages.add(Box.createVerticalStrut(8));
        pnlMessages.revalidate();
        pnlMessages.repaint();

        // Tự cuộn xuống tin mới nhất
        cuonXuong();
    }

    /** Cuộn scroll xuống cuối */
    private void cuonXuong() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}

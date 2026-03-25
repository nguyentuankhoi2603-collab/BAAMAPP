package BaoCaoCuoiKi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * NhomChatPanel – 4 tab: Chat, Quiz, Tài liệu, Lịch, Thành viên
 * Dùng CardLayout để chuyển giữa các tab
 */
public class NhomChatPanel extends JPanel {

    // ── Màu sắc ───────────────────────────────────────────────
    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color LABEL_FG    = new Color(0x0F172A);
    private static final Color HINT_FG     = new Color(0x64748B);
    private static final Color DIVIDER     = new Color(220, 228, 245);
    private static final Color BG_INPUT    = new Color(255, 255, 255, 220);
    private static final Color BG_TAB      = new Color(245, 247, 252);

    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_TAB   = new Font("Segoe UI", Font.BOLD,  11);

    // ── State ─────────────────────────────────────────────────
    private int    nhomId;
    private String tenNguoiDung;
    private String currentTab = "CHAT"; // track hiện tại tab nào

    // ── Widgets ───────────────────────────────────────────────
    private JPanel     pnlMessages;
    private JScrollPane scrollPane;
    private JTextField  txtInput;
    private JButton     btnSend;

    // Tab buttons
    private JButton btnTabChat;
    private JButton btnTabQuiz;
    private JButton btnTabTaiLieu;
    private JButton btnTabLich;
    private JButton btnTabThanhVien;

    // CardLayout để chuyển đổi nội dung
    private CardLayout cardLayout;
    private JPanel pnlContent;

    // ── Callback ───────────────────────────────────────────────
    private GuiTinListener guiTinListener;

    public interface GuiTinListener {
        void onGuiTin(int nhomId, String noiDung);
    }

    // ── Constructor ───────────────────────────────────────────
    public NhomChatPanel(int nhomId, String tenNguoiDung) {
        this.nhomId       = nhomId;
        this.tenNguoiDung = tenNguoiDung;

        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildCardContent(), BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════
    // Build Tab Bar
    // ══════════════════════════════════════════════════════════
    private JPanel buildTabBar() {
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setBackground(BG_TAB);
        tabBar.setBorder(new EmptyBorder(0, 0, 1, 0));

        btnTabChat = createTabButton("💬 Chat", "CHAT", true);
        btnTabQuiz = createTabButton("❓ Quiz", "QUIZ", false);
        btnTabTaiLieu = createTabButton("📎 Tài liệu", "TAI_LIEU", false);
        btnTabLich = createTabButton("📅 Lịch", "LICH", false);
        btnTabThanhVien = createTabButton("👥 Thành viên", "THANH_VIEN", false);

        tabBar.add(btnTabChat);
        tabBar.add(btnTabQuiz);
        tabBar.add(btnTabTaiLieu);
        tabBar.add(btnTabLich);
        tabBar.add(btnTabThanhVien);

        return tabBar;
    }

    private JButton createTabButton(String text, String tabName, boolean active) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                
                // ✅ Underline active tab
                if (currentTab.equals(tabName)) {
                    g2.setColor(ACCENT);
                    g2.fillRect(0, getHeight()-3, getWidth(), 3);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(F_TAB);
        btn.setForeground(active ? ACCENT : HINT_FG);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 16, 12, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ✅ Wire click để chuyển tab
        btn.addActionListener(e -> switchTab(tabName));

        return btn;
    }

    // ✅ CHUYỂN TAB
    public void switchTab(String tabName) {
        currentTab = tabName;
        cardLayout.show(pnlContent, tabName);
    }

    // ══════════════════════════════════════════════════════════
    // Build Content với CardLayout
    // ══════════��═══════════════════════════════════════════════
    private JPanel buildCardContent() {
        cardLayout = new CardLayout();
        pnlContent = new JPanel(cardLayout);
        pnlContent.setOpaque(false);

        pnlContent.add(buildChatPanel(), "CHAT");
        pnlContent.add(buildPlaceholderPanel("Quiz"), "QUIZ");
        pnlContent.add(new TaiLieuUI(), "TAI_LIEU"); // dùng UI thật
        pnlContent.add(buildPlaceholderPanel("Lịch"), "LICH");
        pnlContent.add(buildPlaceholderPanel("Thành viên"), "THANH_VIEN");

        return pnlContent;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(buildMessagesArea(), BorderLayout.CENTER);
        panel.add(buildInputBar(),     BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildPlaceholderPanel(String tabName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lblPlaceholder = new JLabel("Tab: " + tabName, SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPlaceholder.setForeground(HINT_FG);

        panel.add(lblPlaceholder, BorderLayout.CENTER);
        return panel;
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
    // PUBLIC API
    // ══════════════════════════════════════════════════════════

    public void setGuiTinListener(GuiTinListener listener) {
        this.guiTinListener = listener;
    }

    public void nhanTinNhan(TinNhan t, int currentUserId) {
        if (!currentTab.equals("CHAT")) return; // Chỉ hiển thị nếu đang ở tab CHAT

        String gio = t.getThoiGianStr();
        boolean mine = t.getNguoiGuiId() == currentUserId;

        themBubble(t.getTenNguoiGui(), t.getNoiDung(), gio, mine);
    }

    public void loadNhom(int nhomIdMoi, String tenNhomMoi) {
        this.nhomId = nhomIdMoi;
        clearChat();
    }

    public void clearChat() {
        pnlMessages.removeAll();
        pnlMessages.revalidate();
        pnlMessages.repaint();
    }

    public int    getNhomId()        { return nhomId; }
    public String getTenNguoiDung()  { return tenNguoiDung; }
    public String getCurrentTab()    { return currentTab; }

    // ── Getter Tab Buttons ──────────────────────────────────────
    public JButton getBtnTabChat()       { return btnTabChat; }
    public JButton getBtnTabQuiz()       { return btnTabQuiz; }
    public JButton getBtnTabTaiLieu()    { return btnTabTaiLieu; }
    public JButton getBtnTabLich()       { return btnTabLich; }
    public JButton getBtnTabThanhVien()  { return btnTabThanhVien; }

    // ══════════════════════════════════════════════════════════
    // PRIVATE
    // ══════════════════════════════════════════════════════════

    private void guiTinNhan() {
        String text = txtInput.getText().trim();
        if (text.isEmpty() || text.equals("Nhập tin nhắn...")) return;

        txtInput.setText("Nhập tin nhắn...");
        txtInput.setForeground(HINT_FG);

        if (guiTinListener != null) {
            guiTinListener.onGuiTin(nhomId, text);
        }
    }

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

        if (!mine) {
            JLabel lblNguoi = new JLabel(nguoi);
            lblNguoi.setFont(F_BOLD);
            lblNguoi.setForeground(ACCENT);
            bubble.add(lblNguoi);
            bubble.add(Box.createVerticalStrut(3));
        }

        JLabel lblText = new JLabel(
                "<html><body style='width:220px'>" + noiDung + "</body></html>");
        lblText.setFont(F_BODY);
        lblText.setForeground(mine ? Color.WHITE : LABEL_FG);
        bubble.add(lblText);

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

        cuonXuong();
    }

    private void cuonXuong() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }
}

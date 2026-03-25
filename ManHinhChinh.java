
package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

/**
 * ManHinhChinh – Màn hình chính sau đăng nhập.
 *
 * Điều hướng nội bộ (CardLayout):
 *   BANG_TIN · NHOM_CHAT · LICH · TAI_LIEU · QUAN_LY_TK
 *
 * Mở dialog:
 *   TaoNhomUI         (RightPanel → "Tạo nhóm")
 *   ThamGiaNhomUI     (RightPanel → "Tham gia nhóm")
 *   TaoQuizUI         (tab Quiz trong nhóm)
 *   KhoQuizUI         (tab Quiz → xem kho)
 *   LamQuizUI         (KhoQuizUI → Làm bài) → KetQuaQuizUI
 *   ThongTinCaNhanUI  (popup TK → Thông tin cá nhân)
 *   ThongTinThanhVienUI (danh sách thành viên)
 *   DangNhapUI        (Đăng xuất)
 */
public class ManHinhChinh extends JFrame {

    // ── Màu sắc ───────────────────────────────────────────────
    public static final Color ACCENT       = new Color(0x5B8DEF);
    public static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    public static final Color ACCENT_LIGHT = new Color(0xA8C4FB);
    public static final Color LABEL_FG     = new Color(0x0F172A);
    public static final Color HINT_FG      = new Color(0x64748B);
    public static final Color NAV_BG       = new Color(22, 36, 71, 230);
    public static final Color LIST_BG      = new Color(240, 244, 255, 230);
    public static final Color CONTENT_BG   = new Color(248, 250, 255, 240);
    public static final Color RIGHT_BG     = new Color(235, 242, 255, 220);
    public static final Color ITEM_ACTIVE  = new Color(91, 141, 239, 40);
    public static final Color ITEM_HOVER   = new Color(91, 141, 239, 20);
    public static final Color DIVIDER_CLR  = new Color(220, 228, 245);

    public static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font F_HEADER = new Font("Segoe UI", Font.BOLD,  15);

    // ── State ─────────────────────────────────────────────────
    private final NguoiDung nguoiDung;

    /** ID nhóm đang được chọn (−1 = chưa chọn nhóm nào) */
    private int    nhomActiveId   = -1;
    /** Tên nhóm đang được chọn */
    private String nhomActiveTen  = "";

    private JPanel     pnlCenter;
    private CardLayout cardCenter;

    /** Panel chứa danh sách NhomItem bên trái */
    private JPanel     pnlNhomList;

    /** Cờ đánh dấu list đang ở trạng thái rỗng (hiển thị placeholder) */
    private boolean    listIsEmpty = true;

    private JWindow    popupTK;
    private boolean    popupVisible = false;

    /** Panel bảng tin */
    private JPanel pnlFeed;
    private JLabel lblEmptyFeed;
    private boolean feedIsEmpty = true;

    /** Panel NhomChat – được tái sử dụng, chỉ reload nội dung khi đổi nhóm */
    private NhomChatPanel nhomChatPanel;

    /** Header title trong tab NHOM_CHAT – cần cập nhật khi đổi nhóm */
    private JLabel lblNhomChatTitle;

    // ── Constructor ───────────────────────────────────────────
    public ManHinhChinh(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
        setTitle("BAAM – " + (nguoiDung != null ? nguoiDung.getTenDangNhap() : ""));
        setMinimumSize(new Dimension(960, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(200, 215, 255),
                                              getWidth(), getHeight(), new Color(220, 232, 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(false);
        setContentPane(root);

        root.add(buildNavBar(),   BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    // ══════════════════════════════════════════════════════════
    // NAV BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(NAV_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 20));
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(56, 0));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton btnAvatar = buildAvatarBtn();
        btnAvatar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { togglePopupTK(); }
        });

        JButton btnHome = buildNavTextBtn("Home", "Trang chủ");
        btnHome.addActionListener(e -> showContent("BANG_TIN"));

        top.add(btnAvatar);
        top.add(Box.createVerticalStrut(8));
        top.add(buildNavSep());
        top.add(Box.createVerticalStrut(8));
        top.add(btnHome);
        nav.add(top, BorderLayout.NORTH);

        JPanel bot = new JPanel();
        bot.setOpaque(false);
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        bot.setBorder(new EmptyBorder(0, 0, 12, 0));
        bot.add(buildNavTextBtn("Cài\nđặt", "Cài đặt"));
        nav.add(bot, BorderLayout.SOUTH);

        return nav;
    }

    private JButton buildAvatarBtn() {
        JButton b = new JButton() {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) { g2.setColor(ACCENT_LIGHT); g2.fillOval(4, 4, 34, 34); }

                if (nguoiDung != null && nguoiDung.getAnhDaiDien() != null) {
                    g2.setClip(new Ellipse2D.Float(8, 8, 30, 30));
                    g2.drawImage(nguoiDung.getAnhDaiDien()
                            .getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH), 8, 8, null);
                    g2.setClip(null);
                } else {
                    g2.setPaint(new GradientPaint(6, 6, ACCENT, 40, 40, ACCENT_DARK));
                    g2.fillOval(8, 8, 30, 30);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    String ch = nguoiDung != null
                            ? nguoiDung.getTenDangNhap().substring(0, 1).toUpperCase() : "U";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(ch, 23 - fm.stringWidth(ch) / 2, 23 + fm.getAscent() / 2 - 2);
                }
                g2.dispose();
            }
        };
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(56, 48));
        b.setMaximumSize(new Dimension(56, 48));
        b.setToolTipText("Quản lý tài khoản");
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildNavTextBtn(String text, String tooltip) {
        JButton b = new JButton("<html><center>" + text.replace("\n", "<br>") + "</center></html>") {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hov) {
                    g2.setColor(new Color(255, 255, 255, 25));
                    g2.fillRoundRect(6, 4, 44, 44, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 10));
        b.setForeground(new Color(255, 255, 255, 200));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(56, 52));
        b.setMaximumSize(new Dimension(56, 52));
        b.setToolTipText(tooltip);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════
    // MAIN AREA
    // ══════════════════════════════════════════════════════════
    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildListPanel(), BorderLayout.WEST);

        cardCenter = new CardLayout();
        pnlCenter  = new JPanel(cardCenter) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(CONTENT_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlCenter.setOpaque(false);

        // Đăng ký các panel nội dung
        pnlCenter.add(buildBangTinPanel(),   "BANG_TIN");
        pnlCenter.add(buildNhomChatPanel(),  "NHOM_CHAT");
        LichUI lichUI = new LichUI();
        new LichController(lichUI, nguoiDung);
        pnlCenter.add(lichUI, "LICH");

        TaiLieuUI taiLieuUI = new TaiLieuUI();
        new TaiLieuController(taiLieuUI, "data");
        pnlCenter.add(taiLieuUI, "TAI_LIEU");  
        pnlCenter.add(buildQuanLyTKPanel(),  "QUAN_LY_TK");

        main.add(pnlCenter, BorderLayout.CENTER);
        main.add(buildRightPanel(), BorderLayout.EAST);
        return main;
    }

    // ── List Panel (danh sách nhóm) ───────────────────────────
    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(LIST_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(240, 0));

        // Thanh tìm kiếm
        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setOpaque(false);
        searchWrap.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DIVIDER_CLR),
                new EmptyBorder(10, 12, 10, 12)));
        JTextField txtSearch = new JTextField("Tìm kiếm...");
        txtSearch.setFont(F_BODY);
        txtSearch.setForeground(HINT_FG);
        txtSearch.setBorder(new EmptyBorder(7, 14, 7, 14));
        txtSearch.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Tìm kiếm...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(LABEL_FG);
                }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm kiếm...");
                    txtSearch.setForeground(HINT_FG);
                }
            }
        });
        searchWrap.add(txtSearch);
        panel.add(searchWrap, BorderLayout.NORTH);

        pnlNhomList = new JPanel();
        pnlNhomList.setOpaque(false);
        pnlNhomList.setLayout(new BoxLayout(pnlNhomList, BoxLayout.Y_AXIS));
        pnlNhomList.setBorder(new EmptyBorder(8, 8, 8, 8));
        showEmptyNhomState();

        JScrollPane scroll = new JScrollPane(pnlNhomList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /** Hiển thị placeholder khi chưa có nhóm nào */
    private void showEmptyNhomState() {
        pnlNhomList.removeAll();
        listIsEmpty = true;
        JLabel empty = new JLabel("Chưa có nhóm nào", SwingConstants.CENTER);
        empty.setFont(F_BODY);
        empty.setForeground(HINT_FG);
        empty.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlNhomList.add(Box.createVerticalGlue());
        pnlNhomList.add(empty);
        pnlNhomList.add(Box.createVerticalGlue());
        pnlNhomList.revalidate();
        pnlNhomList.repaint();
    }

    // ── Right Panel ───────────────────────────────────────────
    private JPanel buildRightPanel() {
        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(RIGHT_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(180, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 1, 0, 0, DIVIDER_CLR),
                new EmptyBorder(20, 14, 20, 14)));

        panel.add(buildRightBtn("Tạo nhóm",     true,  e -> moTaoNhom()));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildRightBtn("Tham gia nhóm", false, e -> moThamGiaNhom()));
        panel.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER_CLR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(20));

        panel.add(buildRightBtn("Lịch của bạn", false, e -> showContent("LICH")));
        panel.add(Box.createVerticalStrut(10));
        panel.add(buildRightBtn("Tài liệu",     false, e -> showContent("TAI_LIEU")));
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ── Content Panels ────────────────────────────────────────
    private JPanel buildBangTinPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(buildContentHeader("Bảng tin", "Hoạt động mới nhất từ các nhóm"), BorderLayout.NORTH);

        pnlFeed = new JPanel();
        pnlFeed.setOpaque(false);
        pnlFeed.setLayout(new BoxLayout(pnlFeed, BoxLayout.Y_AXIS));
        pnlFeed.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblEmptyFeed = new JLabel("Chưa có hoạt động nào", SwingConstants.CENTER);
        lblEmptyFeed.setFont(F_BODY);
        lblEmptyFeed.setForeground(HINT_FG);
        lblEmptyFeed.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlFeed.add(Box.createVerticalGlue());
        pnlFeed.add(lblEmptyFeed);
        pnlFeed.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(pnlFeed);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    /** Thêm một dòng hoạt động lên đầu bảng tin */
    public void themHoatDong(String text) {
        if (feedIsEmpty) {
            pnlFeed.removeAll();
            feedIsEmpty = false;
        }
        JLabel item = new JLabel("• " + text);
        item.setFont(F_BODY);
        item.setForeground(LABEL_FG);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFeed.add(item, 0);
        pnlFeed.revalidate();
        pnlFeed.repaint();
    }

    /**
     * Xây dựng panel NHOM_CHAT.
     * NhomChatPanel được tạo một lần; nội dung thay đổi qua loadNhom().
     */
    private JPanel buildNhomChatPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        // ── Header (title + tab bar) ──
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        lblNhomChatTitle = new JLabel("Chọn một nhóm để bắt đầu");
        lblNhomChatTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNhomChatTitle.setForeground(LABEL_FG);

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.setOpaque(false);
        headerWrap.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DIVIDER_CLR),
                new EmptyBorder(18, 24, 14, 24)));
        headerWrap.add(lblNhomChatTitle, BorderLayout.WEST);
        top.add(headerWrap,         BorderLayout.NORTH);
        top.add(buildNhomTabBar(),  BorderLayout.SOUTH);
        p.add(top, BorderLayout.NORTH);

        // ── Chat panel ──
        // Khởi tạo với nhomId = -1 (chưa chọn nhóm)
        nhomChatPanel = new NhomChatPanel(-1,
                nguoiDung != null ? nguoiDung.getTenDangNhap() : "User");

        nhomChatPanel.setGuiTinListener((nhomId, noiDung) -> {
            // TODO: gửi lên server / DB
            System.out.println("[Chat nhomId=" + nhomId + "] " + noiDung);

            // Đẩy nhóm này lên đầu danh sách khi có tin mới
            if (nhomId >= 0 && !nhomActiveTen.isEmpty()) {
                SwingUtilities.invokeLater(() -> duaNhomLenDau(nhomActiveTen));
            }
        });

        p.add(nhomChatPanel, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildQuanLyTKPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(buildContentHeader("Quản lý tài khoản", "Thông tin & bảo mật"), BorderLayout.NORTH);

        ThongTinCaNhanUI ttcn = new ThongTinCaNhanUI(nguoiDung);
        ttcn.btnQuanLyTK.setVisible(false);
        ttcn.btnCapNhat.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin thành công!", "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE));
        p.add(ttcn, BorderLayout.CENTER);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    // POPUP tài khoản
    // ══════════════════════════════════════════════════════════
    private void togglePopupTK() {
        if (popupVisible) {
            if (popupTK != null) popupTK.setVisible(false);
            popupVisible = false;
            return;
        }
        popupTK = new JWindow(this);
        JPanel pop = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Shape s = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16);
                for (int i = 6; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, i * 6));
                    g2.fillRoundRect(i, i + 2, getWidth() - i * 2, getHeight() - i * 2, 16, 16);
                }
                g2.setClip(s);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fill(s);
                g2.setClip(null);
                g2.setColor(new Color(220, 228, 245));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        pop.setOpaque(false);
        pop.setLayout(new BoxLayout(pop, BoxLayout.Y_AXIS));
        pop.setBorder(new EmptyBorder(12, 0, 12, 0));

        JLabel lblUser = new JLabel("  " + (nguoiDung != null ? nguoiDung.getTenDangNhap() : "Người dùng"));
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(LABEL_FG);
        lblUser.setBorder(new EmptyBorder(4, 16, 12, 16));
        pop.add(lblUser);
        addSep(pop);
        pop.add(Box.createVerticalStrut(4));

        pop.add(buildPopupItem("Tên tài khoản", e -> {}));
        pop.add(buildPopupItem("Thông tin cá nhân", e -> {
            popupTK.setVisible(false);
            popupVisible = false;
            showContent("QUAN_LY_TK");
        }));
        pop.add(buildPopupItem("Đổi mật khẩu", e -> {
            popupTK.setVisible(false);
            popupVisible = false;
            QuenMKUI quenView = new QuenMKUI();
            new QuenMKController(quenView, null);
            quenView.setVisible(true);
        }));
        pop.add(Box.createVerticalStrut(4));
        addSep(pop);
        pop.add(Box.createVerticalStrut(4));
        pop.add(buildPopupItemDanger("Đăng xuất", e -> dangXuat()));

        popupTK.setContentPane(pop);
        popupTK.setSize(210, 215);
        Point loc = getLocationOnScreen();
        popupTK.setLocation(loc.x + 56 + 8, loc.y + 60);
        popupTK.setVisible(true);
        popupVisible = true;

        popupTK.addWindowFocusListener(new WindowFocusListener() {
            public void windowLostFocus(WindowEvent e) { popupTK.setVisible(false); popupVisible = false; }
            public void windowGainedFocus(WindowEvent e) {}
        });
    }

    // ══════════════════════════════════════════════════════════
    // NAVIGATION PUBLIC API
    // ══════════════════════════════════════════════════════════

    /** Hiển thị tab nội dung theo key */
    public void showContent(String key) {
        cardCenter.show(pnlCenter, key);
    }

    /**
     * Chọn nhóm từ danh sách → cập nhật header, load chat, chuyển tab.
     *
     * @param nhomId  ID nhóm trong DB (≥ 0)
     * @param ten     Tên nhóm hiển thị
     */
    public void chonNhom(int nhomId, String ten) {
        // Không reload nếu đã đang xem nhóm này
        if (nhomId == nhomActiveId) {
            showContent("NHOM_CHAT");
            return;
        }

        nhomActiveId  = nhomId;
        nhomActiveTen = ten;

        // Cập nhật header title
        if (lblNhomChatTitle != null) {
            lblNhomChatTitle.setText(ten);
        }

        // Tải nội dung chat của nhóm mới
        nhomChatPanel.loadNhom(nhomId, ten);

        // Đánh dấu item active trong danh sách
        capNhatActiveItem(nhomId);

        // Chuyển sang tab chat
        showContent("NHOM_CHAT");
    }

    /**
     * Đánh dấu NhomItem đang active, bỏ active các item còn lại.
     */
    private void capNhatActiveItem(int nhomId) {
        for (Component c : pnlNhomList.getComponents()) {
            if (c instanceof NhomItem) {
                NhomItem item = (NhomItem) c;
                item.setActive(item.getNhomId() == nhomId);
            }
        }
    }

    /**
     * Thêm một nhóm mới vào danh sách bên trái.
     *
     * @param nhomId  ID nhóm trong DB
     * @param ten     Tên nhóm
     * @param sub     Dòng phụ (tin nhắn cuối / trạng thái)
     * @param time    Thời gian tin nhắn cuối
     */
    public void themNhomVaoList(int nhomId, String ten, String sub, String time) {
        // Xóa placeholder rỗng lần đầu
        if (listIsEmpty) {
            pnlNhomList.removeAll();
            listIsEmpty = false;
        }

        // Kiểm tra tránh thêm trùng
        for (Component c : pnlNhomList.getComponents()) {
            if (c instanceof NhomItem && ((NhomItem) c).getNhomId() == nhomId) return;
        }

        NhomItem item = new NhomItem(nhomId, ten, sub, time, false);
        item.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { chonNhom(nhomId, ten); }
        });

        // Thêm lên đầu danh sách
        pnlNhomList.add(item, 0);
        pnlNhomList.add(Box.createVerticalStrut(4), 1);

        pnlNhomList.revalidate();
        pnlNhomList.repaint();
    }

    /**
     * Đẩy một nhóm (theo tên) lên đầu danh sách khi có tin nhắn mới.
     * Cập nhật sub-text và timestamp.
     */
    public void duaNhomLenDau(String ten) {
        duaNhomLenDauVoiTin(ten, null, "Vừa xong");
    }

    /**
     * Đẩy nhóm lên đầu và cập nhật preview tin nhắn cuối.
     */
    public void duaNhomLenDauVoiTin(String ten, String tinMoi, String time) {
        Component found = null;
        int foundIdx = -1;
        Component[] comps = pnlNhomList.getComponents();

        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof NhomItem) {
                NhomItem item = (NhomItem) comps[i];
                if (item.getTen().equals(ten)) {
                    found    = item;
                    foundIdx = i;
                    if (tinMoi != null) item.updateSub(tinMoi, time);
                    break;
                }
            }
        }

        if (found == null) return;

        // Xóa item và spacer ngay sau nó (nếu có)
        pnlNhomList.remove(foundIdx);
        if (foundIdx < pnlNhomList.getComponentCount()
                && !(pnlNhomList.getComponent(foundIdx) instanceof NhomItem)) {
            pnlNhomList.remove(foundIdx); // xóa spacer
        }

        pnlNhomList.add(found, 0);
        pnlNhomList.add(Box.createVerticalStrut(4), 1);

        pnlNhomList.revalidate();
        pnlNhomList.repaint();
    }

    // ══════════════════════════════════════════════════════════
    // DIALOG OPENERS
    // ══════════════════════════════════════════════════════════

    private void moTaoNhom() {
        TaoNhomUI dialog = new TaoNhomUI(this);
        dialog.btnTao.addActionListener(e -> {
            String ten = dialog.getTenNhom();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên nhóm.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.dispose();

            // TODO: lưu vào DB, lấy nhomId thực từ DB
            // Tạm thời dùng timestamp làm ID giả
            int nhomId = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);

            themNhomVaoList(nhomId, ten, "Nhóm mới tạo", "Vừa xong");
            chonNhom(nhomId, ten);
            themHoatDong("Bạn vừa tạo nhóm \"" + ten + "\"");
        });
        dialog.setVisible(true);
    }

    private void moThamGiaNhom() {
        ThamGiaNhomUI dialog = new ThamGiaNhomUI(this);
        dialog.btnGuiYeuCau.addActionListener(e -> {
            String id = dialog.getIdNhom();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ID nhóm.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.dispose();
            JOptionPane.showMessageDialog(this,
                    "Đã gửi yêu cầu tham gia nhóm " + id + ".\nChờ trưởng nhóm phê duyệt.",
                    "Yêu cầu đã gửi", JOptionPane.INFORMATION_MESSAGE);
        });
        dialog.setVisible(true);
    }

    /** Mở TaoQuizUI và kết nối nút Kho Quiz → KhoQuizUI */
    public void moTaoQuiz() {
        TaoQuizUI taoQuiz = new TaoQuizUI();
        taoQuiz.btnThemCauHoi.addActionListener(e -> {
            String q = taoQuiz.txtQuestion.getText().trim();
            if (q.isEmpty() || q.startsWith("Nhập câu hỏi")) return;
            String dapAnDung = "?";
            for (int i = 0; i < 4; i++) {
                if (taoQuiz.cmbTrueFalse[i].getSelectedItem().equals("True")) {
                    dapAnDung = new String[]{"A","B","C","D"}[i];
                    break;
                }
            }
            taoQuiz.themCauHoiVaoList(q.length() > 40 ? q.substring(0, 40) + "…" : q, dapAnDung);
            taoQuiz.txtQuestion.setText("");
            for (int i = 0; i < 4; i++) {
                taoQuiz.txtOptions[i].setText("");
                taoQuiz.cmbTrueFalse[i].setSelectedIndex(0);
            }
        });
        taoQuiz.btnKhoQuiz.addActionListener(e -> {
            taoQuiz.dispose();
            moKhoQuiz();
        });
        taoQuiz.btnCapNhat.addActionListener(e ->
                JOptionPane.showMessageDialog(taoQuiz,
                        "Quiz đã lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE));
        taoQuiz.setVisible(true);
    }

    /** Mở KhoQuizUI và kết nối nút Làm bài → LamQuizUI */
    public void moKhoQuiz() {
        KhoQuizUI kho = new KhoQuizUI();
        JFrame khoFrame = new JFrame("Kho Quiz");
        khoFrame.setSize(760, 520);
        khoFrame.setLocationRelativeTo(this);
        khoFrame.setContentPane(kho);

        kho.btnTaoQuiz.addActionListener(e -> {
            khoFrame.dispose();
            moTaoQuiz();
        });

        kho.btnLamQuiz.addActionListener(e -> {
            int row = kho.tblQuiz.getSelectedRow();
            String tenQuiz = row >= 0 ? kho.tblQuiz.getValueAt(row, 1).toString() : "Quiz";
            khoFrame.dispose();
            moLamQuiz(tenQuiz);
        });

        khoFrame.setVisible(true);
    }

    /** Mở LamQuizUI với dữ liệu mẫu */
    private void moLamQuiz(String tenQuiz) {
        LamQuizUI lamQuiz = new LamQuizUI();
        lamQuiz.setTenQuiz(tenQuiz);

        String[][] cauHoi = {
            {"Java là ngôn ngữ lập trình gì?",
             "Hướng đối tượng","Hướng thủ tục","Script","Hàm thuần túy","0"},
            {"Swing thuộc thư viện nào của Java?",
             "javax.swing","java.awt","java.io","java.net","0"},
            {"JFrame là lớp dùng để?",
             "Tạo cửa sổ ứng dụng","Kết nối CSDL","Mã hóa dữ liệu","Gửi email","0"},
        };
        final int[] currentIdx = {0};
        final int[] answers    = new int[cauHoi.length];
        java.util.Arrays.fill(answers, -1);

        hienThiCauHoi(lamQuiz, cauHoi, 0, answers);

        final int[] giay = {cauHoi.length * 60};
        javax.swing.Timer countdown = new javax.swing.Timer(1000, null);
        countdown.addActionListener(ev -> {
            giay[0]--;
            lamQuiz.lblTimer.setText(String.format("%02d:%02d", giay[0] / 60, giay[0] % 60));
            if (giay[0] <= 0) {
                countdown.stop();
                moKetQua(lamQuiz, cauHoi, answers, tenQuiz);
            }
        });
        countdown.start();

        lamQuiz.btnCauTruoc.addActionListener(e -> {
            if (currentIdx[0] > 0) {
                answers[currentIdx[0]] = lamQuiz.getSelectedOption();
                currentIdx[0]--;
                hienThiCauHoi(lamQuiz, cauHoi, currentIdx[0], answers);
            }
        });
        lamQuiz.btnCauSau.addActionListener(e -> {
            answers[currentIdx[0]] = lamQuiz.getSelectedOption();
            if (currentIdx[0] < cauHoi.length - 1) {
                currentIdx[0]++;
                hienThiCauHoi(lamQuiz, cauHoi, currentIdx[0], answers);
            }
        });
        lamQuiz.btnNopBai.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(lamQuiz,
                    "Bạn có chắc muốn nộp bài?", "Xác nhận nộp bài",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                countdown.stop();
                answers[currentIdx[0]] = lamQuiz.getSelectedOption();
                moKetQua(lamQuiz, cauHoi, answers, tenQuiz);
            }
        });

        lamQuiz.setVisible(true);
    }

    private void hienThiCauHoi(LamQuizUI ui, String[][] cauHoi, int idx, int[] answers) {
        String[] data = cauHoi[idx];
        ui.hienThiCauHoi(idx + 1, cauHoi.length, data[0],
                new String[]{data[1], data[2], data[3], data[4]});
        if (answers[idx] >= 0) ui.btnOptions[answers[idx]].doClick();
    }

    private void moKetQua(LamQuizUI lamQuiz, String[][] cauHoi, int[] answers, String tenQuiz) {
        lamQuiz.dispose();
        KetQuaQuizUI kq = new KetQuaQuizUI();
        final int[] showIdx = {0};

        int diem = 0;
        for (int i = 0; i < cauHoi.length; i++)
            if (answers[i] == Integer.parseInt(cauHoi[i][5])) diem++;
        final int diemFinal = diem;

        hienThiKetQua(kq, cauHoi, answers, 0, diemFinal);

        kq.btnCauTruoc.addActionListener(e -> {
            if (showIdx[0] > 0) { showIdx[0]--; hienThiKetQua(kq, cauHoi, answers, showIdx[0], diemFinal); }
        });
        kq.btnCauSau.addActionListener(e -> {
            if (showIdx[0] < cauHoi.length - 1) { showIdx[0]++; hienThiKetQua(kq, cauHoi, answers, showIdx[0], diemFinal); }
        });

        kq.setVisible(true);
    }

    private void hienThiKetQua(KetQuaQuizUI ui, String[][] cauHoi, int[] answers, int idx, int diem) {
        String[] data = cauHoi[idx];
        ui.hienThiKetQua(idx + 1, cauHoi.length, data[0],
                new String[]{data[1], data[2], data[3], data[4]},
                Integer.parseInt(data[5]), answers[idx],
                diem + "/" + cauHoi.length);
    }

    // ── Đăng xuất ─────────────────────────────────────────────
    private void dangXuat() {
        if (popupTK != null) popupTK.setVisible(false);
        dispose();
        SwingUtilities.invokeLater(() -> {
            DangNhapUI ui = new DangNhapUI();
            new DangNhapController(ui);
            ui.setVisible(true);
        });
    }

    // ══════════════════════════════════════════════════════════
    // COMPONENT BUILDERS
    // ══════════════════════════════════════════════════════════

    private JPanel buildContentHeader(String title, String sub) {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DIVIDER_CLR),
                new EmptyBorder(18, 24, 14, 24)));
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t.setForeground(LABEL_FG);
        JLabel s = new JLabel(sub);
        s.setFont(F_SMALL);
        s.setForeground(HINT_FG);
        col.add(t);
        col.add(Box.createVerticalStrut(2));
        col.add(s);
        h.add(col, BorderLayout.WEST);
        return h;
    }

    private JPanel buildNhomTabBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bar.setOpaque(false);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, DIVIDER_CLR),
                new EmptyBorder(0, 16, 0, 16)));

        String[] tabs = {"Chat", "Quiz", "Tài liệu", "Lịch", "Thành viên"};
        for (int i = 0; i < tabs.length; i++) {
            final String tabName = tabs[i];
            final boolean active = (i == 0);
            JButton b = buildTabChip(tabName, active);
            if (tabName.equals("Quiz")) {
                b.addActionListener(e -> {
                    // Chỉ mở Quiz nếu đã chọn nhóm
                    if (nhomActiveId < 0) {
                        JOptionPane.showMessageDialog(ManHinhChinh.this,
                                "Vui lòng chọn một nhóm trước.", "Thông báo",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    moTaoQuiz();
                });
            }
            bar.add(b);
            bar.add(Box.createHorizontalStrut(2));
        }
        return bar;
    }

    private JButton buildTabChip(String text, boolean active) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                if (active) {
                    g.setColor(ACCENT);
                    g.fillRect(0, getHeight() - 2, getWidth(), 2);
                }
                super.paintComponent(g);
            }
        };
        b.setFont(active ? F_BOLD : F_BODY);
        b.setForeground(active ? ACCENT : HINT_FG);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildRightBtn(String text, boolean primary, ActionListener action) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (primary) {
                    g2.setPaint(new GradientPaint(0, 0, hov ? new Color(0x4A7EE8) : ACCENT,
                                                  getWidth(), getHeight(), ACCENT_DARK));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else {
                    g2.setColor(hov ? new Color(255, 255, 255, 230) : new Color(255, 255, 255, 180));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(hov ? ACCENT : DIVIDER_CLR);
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD);
        b.setForeground(primary ? Color.WHITE : LABEL_FG);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 14, 10, 14));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(action);
        return b;
    }

    private JButton buildPopupItem(String text, ActionListener action) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                if (hov) { g.setColor(new Color(91, 141, 239, 15)); g.fillRect(0, 0, getWidth(), getHeight()); }
                super.paintComponent(g);
            }
        };
        b.setFont(F_BODY); b.setForeground(LABEL_FG);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(9, 16, 9, 16));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(action);
        return b;
    }

    private JButton buildPopupItemDanger(String text, ActionListener action) {
        JButton b = buildPopupItem(text, action);
        b.setForeground(new Color(0xEF4444));
        return b;
    }

    private void addSep(JPanel p) {
        JSeparator s = new JSeparator();
        s.setForeground(DIVIDER_CLR);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        p.add(s);
    }

    private JPanel buildNavSep() {
        JPanel d = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 20));
                g.fillRect(12, 0, 32, 1);
            }
        };
        d.setOpaque(false);
        d.setMaximumSize(new Dimension(56, 1));
        d.setPreferredSize(new Dimension(56, 1));
        return d;
    }

    // ══════════════════════════════════════════════════════════
    // INNER CLASS: NhomItem
    // ══════════════════════════════════════════════════════════
    public static class NhomItem extends JPanel {
        private boolean active, hov;
        private final int    nhomId;
        private final String ten;
        private JLabel       lblSub;
        private JLabel       lblTime;

        NhomItem(int nhomId, String ten, String sub, String time, boolean isActive) {
            this.nhomId = nhomId;
            this.ten    = ten;
            this.active = isActive;
            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
            setPreferredSize(new Dimension(0, 64));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            });

            // Avatar nhóm (chữ cái đầu tên)
            JPanel av = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(active
                            ? new GradientPaint(0, 0, ACCENT, 38, 38, ACCENT_DARK)
                            : new GradientPaint(0, 0, new Color(180, 200, 240), 38, 38, new Color(160, 185, 225)));
                    g2.fillRoundRect(0, 0, 38, 38, 10, 10);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    FontMetrics fm = g2.getFontMetrics();
                    // Dùng chữ cái ĐẦU (thay vì cuối) để đúng convention hơn
                    String ch = ten.isEmpty() ? "?" : ten.substring(0, 1).toUpperCase();
                    g2.drawString(ch, 19 - fm.stringWidth(ch) / 2, 19 + fm.getAscent() / 2 - 2);
                    g2.dispose();
                }
            };
            av.setOpaque(false);
            av.setPreferredSize(new Dimension(38, 38));

            JPanel info = new JPanel();
            info.setOpaque(false);
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

            JLabel ln = new JLabel(ten);
            ln.setFont(active ? F_BOLD : F_BODY);
            ln.setForeground(active ? ACCENT : LABEL_FG);

            lblSub = new JLabel(sub);
            lblSub.setFont(F_SMALL);
            lblSub.setForeground(HINT_FG);

            info.add(ln);
            info.add(Box.createVerticalStrut(3));
            info.add(lblSub);

            lblTime = new JLabel(time);
            lblTime.setFont(F_SMALL);
            lblTime.setForeground(HINT_FG);
            lblTime.setVerticalAlignment(SwingConstants.TOP);

            add(av,      BorderLayout.WEST);
            add(info,    BorderLayout.CENTER);
            add(lblTime, BorderLayout.EAST);
        }

        public int    getNhomId() { return nhomId; }
        public String getTen()   { return ten; }

        public void setActive(boolean a) {
            this.active = a;
            repaint();
        }

        /** Cập nhật dòng preview tin nhắn cuối */
        public void updateSub(String sub, String time) {
            lblSub.setText(sub);
            lblTime.setText(time);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g2.setColor(ITEM_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(new Color(91, 141, 239, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            } else if (hov) {
                g2.setColor(ITEM_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}

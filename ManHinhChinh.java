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
 *   BANG_TIN   · NHOM_CHAT · LICH · TAI_LIEU · QUAN_LY_TK
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
    private String     nhomActive = "";
    private JPanel     pnlCenter;
    private CardLayout cardCenter;
    private JPanel     pnlNhomList;
    private JWindow    popupTK;
    private boolean    popupVisible = false;
    private JPanel pnlFeed;
    private JLabel lblEmptyFeed;
    private NhomChatPanel nhomChatPanel;
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

        // Avatar button
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

                // Ảnh đại diện hoặc chữ cái đầu
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
        pnlCenter.add(new LichUI(),          "LICH");
        pnlCenter.add(new TaiLieuUI(),       "TAI_LIEU");
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
                if (txtSearch.getText().equals("Tìm kiếm...")) { txtSearch.setText(""); txtSearch.setForeground(LABEL_FG); }
            }
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setText("Tìm kiếm..."); txtSearch.setForeground(HINT_FG); }
            }
        });
        searchWrap.add(txtSearch);
        panel.add(searchWrap, BorderLayout.NORTH);

        pnlNhomList = new JPanel();
        pnlNhomList.setOpaque(false);
        pnlNhomList.setLayout(new BoxLayout(pnlNhomList, BoxLayout.Y_AXIS));
        pnlNhomList.setBorder(new EmptyBorder(8, 8, 8, 8));
        showEmptyState();

        JScrollPane scroll = new JScrollPane(pnlNhomList);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void showEmptyState() {
        pnlNhomList.removeAll();
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

        // ── Tạo nhóm
        panel.add(buildRightBtn("Tạo nhóm", true, e -> moTaoNhom()));
        panel.add(Box.createVerticalStrut(10));

        // ── Tham gia nhóm
        panel.add(buildRightBtn("Tham gia nhóm", false, e -> moThamGiaNhom()));
        panel.add(Box.createVerticalStrut(20));

        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER_CLR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createVerticalStrut(20));

        // ── Lịch
        panel.add(buildRightBtn("Lịch của bạn", false, e -> showContent("LICH")));
        panel.add(Box.createVerticalStrut(10));

        // ── Tài liệu
        panel.add(buildRightBtn("Tài liệu", false, e -> showContent("TAI_LIEU")));
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
    
    public void themHoatDong(String text) {
        // Xóa trạng thái rỗng nếu có
        if (pnlFeed.getComponentCount() == 3 && lblEmptyFeed != null) {
            pnlFeed.removeAll();
        }

        JLabel item = new JLabel("• " + text);
        item.setFont(F_BODY);
        item.setForeground(LABEL_FG);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlFeed.add(item, 0); // thêm lên đầu
        pnlFeed.revalidate();
        pnlFeed.repaint();
    }
    
    
    private JPanel buildNhomChatPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(buildContentHeader(
            nhomActive.isEmpty() ? "Chat nhóm" : nhomActive, ""
        ), BorderLayout.NORTH);
        top.add(buildNhomTabBar(), BorderLayout.SOUTH);

        p.add(top, BorderLayout.NORTH);

        // 🔥 THAY ĐOẠN NÀY
        nhomChatPanel = new NhomChatPanel(0,
            nguoiDung != null ? nguoiDung.getTenDangNhap() : "User");

        // callback gửi tin
        nhomChatPanel.setGuiTinListener((nhomId, noiDung) -> {
            System.out.println("Gửi: " + noiDung);

            // TODO: gửi lên server hoặc DB
        });

        p.add(nhomChatPanel, BorderLayout.CENTER);

        return p;
    }

    private JPanel buildQuanLyTKPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(buildContentHeader("Quản lý tài khoản", "Thông tin & bảo mật"), BorderLayout.NORTH);

        // Nhúng ThongTinCaNhanUI vào đây
        ThongTinCaNhanUI ttcn = new ThongTinCaNhanUI(nguoiDung);
        ttcn.btnQuanLyTK.setVisible(false); // ẩn nút thừa
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

        // Tên người dùng
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
            // Mở QuenMKUI để đặt lại mật khẩu
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

    /** Chọn nhóm từ danh sách → chuyển sang tab chat */
    public void chonNhom(String ten) {
        nhomActive = ten;

        nhomChatPanel.clearChat();

        // ví dụ test
        nhomChatPanel.nhanTinNhan("Nguyễn A", "Hello bro");
        nhomChatPanel.nhanTinNhan(nguoiDung.getTenDangNhap(), "Hi");

        showContent("NHOM_CHAT");
    }
    
    /** Thêm nhóm vào danh sách bên trái */
    public void themNhomVaoList(String ten, String sub, String time) {
        // Xóa trạng thái rỗng
        if (pnlNhomList.getComponentCount() == 2
                && pnlNhomList.getComponent(1) instanceof JLabel) {
            pnlNhomList.removeAll();
        }

        NhomItem item = new NhomItem(ten, sub, time, false);
        item.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { chonNhom(ten); }
        });

        pnlNhomList.add(item, 0);                 // 🔥 thêm lên đầu
        pnlNhomList.add(Box.createVerticalStrut(4), 1);

        pnlNhomList.revalidate();
        pnlNhomList.repaint();
    }

    public void duaNhomLenDau(String ten) {
        Component[] comps = pnlNhomList.getComponents();

        for (Component c : comps) {
            if (c instanceof NhomItem) {
                NhomItem item = (NhomItem) c;
                if (item.getTen().equals(ten)) {
                    pnlNhomList.remove(item);
                    pnlNhomList.add(item, 0);
                    pnlNhomList.revalidate();
                    pnlNhomList.repaint();
                    break;
                }
            }
        }
    }
    // ══════════════════════════════════════════════════════════
    // DIALOG OPENERS
    // ══════════════════════════════════════════════════════════

    private void moTaoNhom() {
        TaoNhomUI dialog = new TaoNhomUI(this);
        dialog.btnTao.addActionListener(e -> {
            String ten = dialog.getTenNhom();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên nhóm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.dispose();
            themNhomVaoList(ten, "Nhóm mới tạo", "Vừa xong");
            chonNhom(ten);
        });
        dialog.setVisible(true);
    }

    private void moThamGiaNhom() {
        ThamGiaNhomUI dialog = new ThamGiaNhomUI(this);
        dialog.btnGuiYeuCau.addActionListener(e -> {
            String id = dialog.getIdNhom();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập ID nhóm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
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
        // Thêm câu hỏi
        taoQuiz.btnThemCauHoi.addActionListener(e -> {
            String q = taoQuiz.txtQuestion.getText().trim();
            if (q.isEmpty() || q.startsWith("Nhập câu hỏi")) return;
            // Tìm đáp án đúng
            String dapAnDung = "?";
            for (int i = 0; i < 4; i++) {
                if (taoQuiz.cmbTrueFalse[i].getSelectedItem().equals("True")) {
                    dapAnDung = new String[]{"A","B","C","D"}[i];
                    break;
                }
            }
            taoQuiz.themCauHoiVaoList(q.length() > 40 ? q.substring(0, 40) + "…" : q, dapAnDung);
            // Reset form
            taoQuiz.txtQuestion.setText("");
            for (int i = 0; i < 4; i++) {
                taoQuiz.txtOptions[i].setText("");
                taoQuiz.cmbTrueFalse[i].setSelectedIndex(0);
            }
        });
        // Mở Kho Quiz
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
            String tenQuiz = row >= 0
                    ? kho.tblQuiz.getValueAt(row, 1).toString()
                    : "Quiz";
            khoFrame.dispose();
            moLamQuiz(tenQuiz);
        });

        khoFrame.setVisible(true);
    }

    /** Mở LamQuizUI với dữ liệu mẫu → khi nộp bài mở KetQuaQuizUI */
    private void moLamQuiz(String tenQuiz) {
        LamQuizUI lamQuiz = new LamQuizUI();
        lamQuiz.setTenQuiz(tenQuiz);

        // Dữ liệu câu hỏi mẫu
        String[][] cauHoi = {
            {"Java là ngôn ngữ lập trình gì?",
             "Hướng đối tượng", "Hướng thủ tục", "Script", "Hàm thuần túy", "0"},
            {"Swing thuộc thư viện nào của Java?",
             "javax.swing", "java.awt", "java.io", "java.net", "0"},
            {"JFrame là lớp dùng để?",
             "Tạo cửa sổ ứng dụng", "Kết nối CSDL", "Mã hóa dữ liệu", "Gửi email", "0"},
        };
        final int[] currentIdx = {0};
        final int[] answers    = new int[cauHoi.length];
        java.util.Arrays.fill(answers, -1);

        // Hiển thị câu đầu tiên
        hienThiCauHoi(lamQuiz, cauHoi, 0, answers);

        // Đếm ngược
        final int[] giay = {Integer.parseInt(cauHoi.length * 60 + "")};
        javax.swing.Timer countdown = new javax.swing.Timer(1000, null);
        countdown.addActionListener(ev -> {
            giay[0]--;
            int m = giay[0] / 60, s = giay[0] % 60;
            lamQuiz.lblTimer.setText(String.format("%02d:%02d", m, s));
            if (giay[0] <= 0) {
                countdown.stop();
                moKetQua(lamQuiz, cauHoi, answers, tenQuiz);
            }
        });
        countdown.start();

        // Câu trước / sau
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
        String[] opts = {data[1], data[2], data[3], data[4]};
        ui.hienThiCauHoi(idx + 1, cauHoi.length, data[0], opts);
        // Khôi phục đáp án đã chọn nếu có (bằng cách click lại)
        if (answers[idx] >= 0) {
            ui.btnOptions[answers[idx]].doClick();
        }
    }

    private void moKetQua(LamQuizUI lamQuiz, String[][] cauHoi, int[] answers, String tenQuiz) {
        lamQuiz.dispose();

        KetQuaQuizUI kq = new KetQuaQuizUI();
        final int[] showIdx = {0};

        // Tính điểm
        int diem = 0;
        for (int i = 0; i < cauHoi.length; i++) {
            int correct = Integer.parseInt(cauHoi[i][5]);
            if (answers[i] == correct) diem++;
        }
        final int diemFinal = diem;

        // Hiển thị câu đầu tiên
        hienThiKetQua(kq, cauHoi, answers, showIdx[0], diemFinal);

        kq.btnCauTruoc.addActionListener(e -> {
            if (showIdx[0] > 0) {
                showIdx[0]--;
                hienThiKetQua(kq, cauHoi, answers, showIdx[0], diemFinal);
            }
        });
        kq.btnCauSau.addActionListener(e -> {
            if (showIdx[0] < cauHoi.length - 1) {
                showIdx[0]++;
                hienThiKetQua(kq, cauHoi, answers, showIdx[0], diemFinal);
            }
        });

        kq.setVisible(true);
    }

    private void hienThiKetQua(KetQuaQuizUI ui, String[][] cauHoi, int[] answers, int idx, int diem) {
        String[] data = cauHoi[idx];
        String[] opts = {data[1], data[2], data[3], data[4]};
        int correct = Integer.parseInt(data[5]);
        ui.hienThiKetQua(idx + 1, cauHoi.length, data[0], opts, correct, answers[idx],
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
                b.addActionListener(e -> moTaoQuiz());
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
    // ── Widget Factories ──────────────────────────────────────
    private JPanel buildGlassCard() {
        JPanel c = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Shape s = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(s);
                g2.setColor(new Color(220, 228, 245));
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(s);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setOpaque(false);
        return c;
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

    private JButton buildAccentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, hov ? new Color(0x4A7EE8) : ACCENT,
                                              getWidth(), getHeight(), ACCENT_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
    // INNER: NhomItem
    // ══════════════════════════════════════════════════════════
    public static class NhomItem extends JPanel {
        private boolean active, hov;
        private final String ten;

        NhomItem(String ten, String sub, String time, boolean isActive) {
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

            // Avatar nhóm (chữ cái cuối tên)
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
                    String ch = ten.substring(ten.length() - 1);
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
            JLabel ls = new JLabel(sub);
            ls.setFont(F_SMALL);
            ls.setForeground(HINT_FG);
            info.add(ln);
            info.add(Box.createVerticalStrut(3));
            info.add(ls);

            JLabel lt = new JLabel(time);
            lt.setFont(F_SMALL);
            lt.setForeground(HINT_FG);
            lt.setVerticalAlignment(SwingConstants.TOP);

            add(av,   BorderLayout.WEST);
            add(info, BorderLayout.CENTER);
            add(lt,   BorderLayout.EAST);
        }

        public String getTen()          { return ten; }
        public void   setActive(boolean a) { this.active = a; repaint(); }

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

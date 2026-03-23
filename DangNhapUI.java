package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

/**
 * DangNhapUI – Giao diện màn hình đăng nhập.
 * Cấu trúc đồng nhất với DangKiUI: GlassCard cao cấp, field tự vẽ,
 * spacing đồng nhất, nút gradient, overlay gradient nền.
 */
public class DangNhapUI extends JFrame {

    // ── Đường dẫn ảnh nền cố định ────────────────────────────
    private static final String DEFAULT_BG =
        "src/main/resources/Untitled design.jpg";

    // ── Bảng màu (đồng nhất với DangKiUI) ────────────────────
    public static final Color BG           = new Color(0xF0F2F5);
    public static final Color ACCENT       = new Color(0x5B8DEF);
    public static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    public static final Color ACCENT_LIGHT = new Color(0xA8C4FB);
    public static final Color LABEL_FG     = new Color(0x0F172A);
    public static final Color HINT_FG      = new Color(0x64748B);
    public static final Color RING_COLOR   = new Color(0xBFD3F8);
    public static final Color INPUT_BG     = new Color(255, 255, 255, 200);
    public static final Color INPUT_BG_FOC = new Color(255, 255, 255, 240);
    public static final Color GLASS_BG     = new Color(255, 255, 255, 145);
    public static final Color BORDER       = new Color(255, 255, 255, 100);

    // ── Font chữ (đồng nhất với DangKiUI) ────────────────────
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,   11);
    public static final Font F_INPUT = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_HINT  = new Font("Segoe UI", Font.ITALIC, 11);
    public static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font F_LINK  = new Font("Segoe UI", Font.BOLD,   12);

    // ── Khoảng cách đồng nhất (giống DangKiUI) ───────────────
    private static final int GAP_FIELD = 14;
    private static final int GAP_LABEL = 5;
    private static final int FIELD_H   = 44;

    // ── Widgets công khai cho Controller ─────────────────────
    public JTextField     txtTaiKhoan;
    public JPasswordField txtMatKhau;
    public JCheckBox      chkGhiNho;
    public JButton        btnDangNhap;
    public JLabel         lblQuenMatKhau;
    public JLabel         lblDangKi;

    // ── Constructor ───────────────────────────────────────────
    public DangNhapUI() {
        setTitle("Đăng nhập");
        setMinimumSize(new Dimension(500, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        BgPanel bgPanel = new BgPanel(DEFAULT_BG);
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        GlassCard card = new GlassCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(470, 600));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;

        int row = 0;

        // ── Logo ──────────────────────────────────────────────
        g.gridy = row++; g.insets = ins(0, 0, 16, 0);
        card.add(buildLogoPanel(), g);

        // ── Tiêu đề ───────────────────────────────────────────
        g.gridy = row++; g.insets = ins(0, 0, 28, 0);
        card.add(buildHeader(), g);

        // ── Tài khoản ─────────────────────────────────────────
        txtTaiKhoan = modernField("👤  Tên đăng nhập hoặc email...");
        g.gridy = row++; g.insets = ins(0, 0, GAP_FIELD, 0);
        card.add(fieldGroup("TÀI KHOẢN", txtTaiKhoan, null), g);

        // ── Mật khẩu ──────────────────────────────────────────
        txtMatKhau = modernPassField();
        JCheckBox chkShow = eyeToggle();
        chkShow.addActionListener(e ->
            txtMatKhau.setEchoChar(chkShow.isSelected() ? (char) 0 : '●'));
        g.gridy = row++; g.insets = ins(0, 0, 10, 0);
        card.add(fieldGroup("MẬT KHẨU", passRow(txtMatKhau, chkShow), null), g);

        // ── Ghi nhớ + Quên mật khẩu ──────────────────────────
        chkGhiNho = new JCheckBox("Ghi nhớ đăng nhập");
        chkGhiNho.setOpaque(false);
        chkGhiNho.setFont(F_LINK);
        chkGhiNho.setForeground(LABEL_FG);

        lblQuenMatKhau = new JLabel("Quên mật khẩu?");
        lblQuenMatKhau.setFont(F_LINK);
        lblQuenMatKhau.setForeground(ACCENT);
        lblQuenMatKhau.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblQuenMatKhau.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblQuenMatKhau.setForeground(ACCENT_DARK); }
            public void mouseExited (MouseEvent e) { lblQuenMatKhau.setForeground(ACCENT); }
        });
        JPanel rowOpts = new JPanel(new BorderLayout());
        rowOpts.setOpaque(false);
        rowOpts.add(chkGhiNho,      BorderLayout.WEST);
        rowOpts.add(lblQuenMatKhau, BorderLayout.EAST);
        g.gridy = row++; g.insets = ins(0, 0, 28, 0);
        card.add(rowOpts, g);

        // ── Nút Đăng nhập ─────────────────────────────────────
        btnDangNhap = gradientBtn("Đăng nhập  →");
        g.gridy = row++; g.insets = ins(0, 0, 24, 0);
        card.add(btnDangNhap, g);

        // ── Divider ───────────────────────────────────────────
        g.gridy = row++; g.insets = ins(0, 0, 20, 0);
        card.add(buildDivider(), g);

        // ── Chưa có tài khoản? ────────────────────────────────
        g.gridy = row; g.insets = ins(0, 0, 0, 0);
        card.add(buildRegisterRow(), g);

        // ── Đặt card vào bgPanel ──────────────────────────────
        GridBagConstraints fc = new GridBagConstraints();
        fc.anchor  = GridBagConstraints.CENTER;
        fc.weightx = fc.weighty = 1;
        fc.insets  = new Insets(24, 24, 24, 24);
        bgPanel.add(card, fc);

        pack();
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════════════════
    // UI Builders
    // ══════════════════════════════════════════════════════════

    /** Logo tròn căn giữa */
    private JPanel buildLogoPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(new LogoCircle());
        return p;
    }

    /** Tiêu đề + subtitle */
    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel title = new JLabel("Chào mừng trở lại!", SwingConstants.CENTER);
        title.setFont(F_TITLE);
        title.setForeground(LABEL_FG);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Đăng nhập để tiếp tục hành trình", SwingConstants.CENTER);
        sub.setFont(F_SUB);
        sub.setForeground(HINT_FG);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(6));
        p.add(sub);
        return p;
    }

    /**
     * Nhóm: label chữ hoa nhỏ + field + hint tùy chọn.
     * Giống hệt DangKiUI → spacing đồng nhất.
     */
    private JPanel fieldGroup(String label, JComponent field, String hint) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL);
        lbl.setForeground(HINT_FG);
        lbl.setBorder(new EmptyBorder(0, 2, GAP_LABEL, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(lbl);
        p.add(field);

        if (hint != null) {
            JLabel h = new JLabel("  " + hint);
            h.setFont(F_HINT);
            h.setForeground(HINT_FG);
            h.setBorder(new EmptyBorder(4, 2, 0, 0));
            h.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(h);
        }
        return p;
    }

    /** Divider "hoặc" ở giữa */
    private JPanel buildDivider() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JSeparator sl = new JSeparator();
        sl.setForeground(new Color(255, 255, 255, 100));
        JSeparator sr = new JSeparator();
        sr.setForeground(new Color(255, 255, 255, 100));

        JLabel or = new JLabel("hoặc", SwingConstants.CENTER);
        or.setFont(F_HINT);
        or.setForeground(HINT_FG);

        p.add(sl, BorderLayout.WEST);
        p.add(or, BorderLayout.CENTER);
        p.add(sr, BorderLayout.EAST);
        return p;
    }

    /** Dòng "Chưa có tài khoản? Đăng ký ngay" */
    private JPanel buildRegisterRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        row.setOpaque(false);

        JLabel prompt = new JLabel("Chưa có tài khoản?");
        prompt.setFont(F_LINK);
        prompt.setForeground(HINT_FG);

        lblDangKi = new JLabel("Đăng ký ngay");
        lblDangKi.setFont(F_LINK);
        lblDangKi.setForeground(ACCENT);
        lblDangKi.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblDangKi.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblDangKi.setForeground(ACCENT_DARK); }
            public void mouseExited (MouseEvent e) { lblDangKi.setForeground(ACCENT); }
        });

        row.add(prompt);
        row.add(lblDangKi);
        return row;
    }

    private JPanel passRow(JPasswordField pf, JCheckBox toggle) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        p.add(pf,     BorderLayout.CENTER);
        p.add(toggle, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    // Widget Factories (đồng nhất với DangKiUI)
    // ══════════════════════════════════════════════════════════

    public JTextField modernField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? INPUT_BG_FOC : INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(F_INPUT);
        f.setForeground(HINT_FG);
        f.setCaretColor(ACCENT);
        f.setOpaque(false);
        f.setPreferredSize(new Dimension(0, FIELD_H));
        f.setText(placeholder);
        applyModernBorder(f, false);

        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText(""); f.setForeground(LABEL_FG);
                }
                applyModernBorder(f, true);
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder); f.setForeground(HINT_FG);
                }
                applyModernBorder(f, false);
                f.repaint();
            }
        });
        return f;
    }

    public JPasswordField modernPassField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? INPUT_BG_FOC : INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(F_INPUT);
        f.setForeground(LABEL_FG);
        f.setCaretColor(ACCENT);
        f.setEchoChar('●');
        f.setOpaque(false);
        f.setPreferredSize(new Dimension(0, FIELD_H));
        applyModernBorder(f, false);

        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { applyModernBorder(f, true);  f.repaint(); }
            public void focusLost (FocusEvent e)  { applyModernBorder(f, false); f.repaint(); }
        });
        return f;
    }

    private JCheckBox eyeToggle() {
        JCheckBox cb = new JCheckBox("👁");
        cb.setOpaque(false);
        cb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        cb.setForeground(HINT_FG);
        cb.setFocusPainted(false);
        cb.setBorderPainted(false);
        cb.setToolTipText("Hiện/ẩn mật khẩu");
        return cb;
    }

    public void applyModernBorder(JComponent c, boolean focused) {
        c.setBorder(new CompoundBorder(
            new LineBorder(focused ? ACCENT : new Color(255, 255, 255, 140),
                           focused ? 2 : 1, true),
            new EmptyBorder(0, 14, 0, 14)
        ));
    }

    // Backward compat cho Controller
    public JTextField     styledField(String ph) { return modernField(ph); }
    public JPasswordField passField()            { return modernPassField(); }
    public void applyBorder(JComponent c, boolean focused) { applyModernBorder(c, focused); }

    public JButton gradientBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov; boolean press;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered (MouseEvent e) { hov = true;   repaint(); }
                public void mouseExited  (MouseEvent e) { hov = false; press = false; repaint(); }
                public void mousePressed (MouseEvent e) { press = true;  repaint(); }
                public void mouseReleased(MouseEvent e) { press = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (hov && !press) {
                    g2.setColor(new Color(91, 141, 239, 55));
                    g2.fillRoundRect(4, 6, w - 8, h - 4, 14, 14);
                }
                Color c1 = press ? ACCENT_DARK
                         : hov   ? new Color(0x4A7EE8) : ACCENT;
                Color c2 = press ? new Color(0x2A56BB)
                         : hov   ? ACCENT_DARK : new Color(0x3D6FD4);
                g2.setPaint(new GradientPaint(0, 0, c1, w, h, c2));
                g2.fillRoundRect(0, 0, w, h - (press ? 0 : 2), 14, 14);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillRoundRect(0, 0, w, h / 2, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BTN);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(0, 50));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Backward compat cho Controller
    public JButton filledBtn(String text) { return gradientBtn(text); }

    public void setLoading(boolean loading) {
        btnDangNhap.setEnabled(!loading);
        btnDangNhap.setText(loading ? "Đang xử lý..." : "Đăng nhập  →");
    }

    // ══════════════════════════════════════════════════════════
    // Inner: Nền ảnh cố định (đồng nhất với DangKiUI)
    // ══════════════════════════════════════════════════════════
    private static class BgPanel extends JPanel {
        private BufferedImage bgImage;

        BgPanel(String path) {
            setOpaque(true);
            try {
                BufferedImage img = ImageIO.read(new File(path));
                if (img != null) bgImage = img;
            } catch (Exception ignored) {}
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int pw = getWidth(), ph = getHeight();
            if (bgImage != null) {
                int iw = bgImage.getWidth(), ih = bgImage.getHeight();
                double scale = Math.max((double) pw / iw, (double) ph / ih);
                int nw = (int)(iw * scale), nh = (int)(ih * scale);
                g2.drawImage(bgImage, (pw-nw)/2, (ph-nh)/2, nw, nh, null);
                // Overlay gradient (đồng nhất với DangKiUI)
                g2.setPaint(new GradientPaint(
                    0, 0,  new Color(10, 20, 60, 80),
                    0, ph, new Color(5, 10, 40, 50)
                ));
                g2.fillRect(0, 0, pw, ph);
            } else {
                g2.setColor(BG);
                g2.fillRect(0, 0, pw, ph);
            }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════
    // Inner: Card Liquid Glass (đồng nhất với DangKiUI)
    // ══════════════════════════════════════════════════════════
    public static class GlassCard extends JPanel {
        private static final int R = 28;

        GlassCard() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Bóng đổ sâu
            for (int i = 12; i >= 1; i--) {
                int alpha = (int) Math.min(5f + (12 - i) * 1.5f, 35);
                g2.setColor(new Color(10, 25, 80, alpha));
                g2.fillRoundRect(i, i + 4, w - i*2, h - i*2, R + i, R + i);
            }

            Shape shape = new RoundRectangle2D.Float(0, 0, w, h, R, R);
            g2.setClip(shape);

            // Base frosted glass
            g2.setColor(GLASS_BG);
            g2.fill(shape);

            // Highlight gradient trên
            g2.setPaint(new GradientPaint(
                0, 0,         new Color(255, 255, 255, 150),
                0, h * 0.35f, new Color(255, 255, 255, 0)
            ));
            g2.fill(shape);

            // Reflection gradient dưới (liquid)
            g2.setPaint(new GradientPaint(
                0, h * 0.72f, new Color(255, 255, 255, 0),
                0, h,          new Color(255, 255, 255, 55)
            ));
            g2.fill(shape);

            // Accent tint nhẹ bên trái
            g2.setPaint(new GradientPaint(
                0, h * 0.3f, new Color(91, 141, 239, 18),
                w, h * 0.7f, new Color(91, 141, 239, 0)
            ));
            g2.fill(shape);

            // Viền ngoài + inner glow
            g2.setClip(null);
            g2.setStroke(new BasicStroke(1.8f));
            g2.setColor(new Color(255, 255, 255, 160));
            g2.draw(new RoundRectangle2D.Float(0.9f, 0.9f, w-1.8f, h-1.8f, R, R));
            g2.setStroke(new BasicStroke(0.8f));
            g2.setColor(new Color(255, 255, 255, 60));
            g2.draw(new RoundRectangle2D.Float(2.5f, 2.5f, w-5f, h-5f, R-3, R-3));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════
    // Inner: Logo tròn
    // ══════════════════════════════════════════════════════════
    private static class LogoCircle extends JPanel {
        private static final int S = 88;

        LogoCircle() {
            setOpaque(false);
            setPreferredSize(new Dimension(S, S));
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            // Ring gradient (đồng nhất với AvatarPanel của DangKiUI)
            g2.setPaint(new GradientPaint(0, 0, ACCENT, S, S, ACCENT_LIGHT));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(1, 1, S - 3, S - 3);

            // Fill gradient
            g2.setPaint(new GradientPaint(6, 6, ACCENT, S - 6, S - 6, ACCENT_DARK));
            g2.fillOval(6, 6, S - 12, S - 12);

            // Highlight trên cùng
            g2.setColor(new Color(255, 255, 255, 35));
            g2.fillOval(6, 6, (S - 12) / 2, S - 12);

            // Text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            FontMetrics fm = g2.getFontMetrics();
            String txt = "BAAM";
            g2.drawString(txt,
                (S - fm.stringWidth(txt)) / 2,
                (S + fm.getAscent() - fm.getDescent()) / 2);

            g2.dispose();
        }
    }

    // ── Tiện ích ──────────────────────────────────────────────
    private Insets ins(int t, int l, int b, int r) { return new Insets(t, l, b, r); }

    public void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
    }
    public void showInfo(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // ── main test ─────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new DangNhapUI().setVisible(true);
        });
    }
}
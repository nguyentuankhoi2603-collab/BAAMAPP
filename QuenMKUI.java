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
 * QuenMKUI – Giao diện Quên mật khẩu.
 * Cấu trúc & style đồng nhất với DangNhapUI / DangKiUI.
 *
 * Luồng 3 bước:
 *   Bước 1 – Nhập email → Gửi mã OTP
 *   Bước 2 – Nhập mã OTP → Xác minh
 *   Bước 3 – Nhập mật khẩu mới → Đặt lại
 */
public class QuenMKUI extends JFrame {

    // ── Đường dẫn ảnh nền cố định ────────────────────────────
    private static final String DEFAULT_BG =
        "src/main/resources/Untitled design.jpg";

    // ── Màu (đồng nhất) ───────────────────────────────────────
    public static final Color BG           = new Color(0xF0F2F5);
    public static final Color ACCENT       = new Color(0x5B8DEF);
    public static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    public static final Color ACCENT_LIGHT = new Color(0xA8C4FB);
    public static final Color LABEL_FG     = new Color(0x0F172A);
    public static final Color HINT_FG      = new Color(0x64748B);
    public static final Color INPUT_BG     = new Color(255, 255, 255, 200);
    public static final Color INPUT_BG_FOC = new Color(255, 255, 255, 240);
    public static final Color GLASS_BG     = new Color(255, 255, 255, 145);

    // ── Font ──────────────────────────────────────────────────
    public static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font F_SUB    = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,   11);
    public static final Font F_INPUT  = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_HINT   = new Font("Segoe UI", Font.ITALIC, 11);
    public static final Font F_BTN    = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font F_LINK   = new Font("Segoe UI", Font.BOLD,   12);

    private static final int GAP_FIELD = 14;
    private static final int GAP_LABEL = 5;
    private static final int FIELD_H   = 44;

    // ── Widgets công khai cho Controller ─────────────────────
    // Bước 1
    public JTextField txtEmail;
    public JButton    btnGuiMa;
    // Bước 2
    public JTextField txtMa;
    public JButton    btnXacMinhMa;
    // Bước 3
    public JPasswordField txtMKMoi;
    public JPasswordField txtXNMKMoi;
    public JButton        btnDatLai;
    // Chung
    public JLabel lblQuayLai;

    // ── Panels từng bước ─────────────────────────────────────
    private JPanel panelBuoc1, panelBuoc2, panelBuoc3;
    private JPanel cardContainer; // chứa 3 panel, dùng CardLayout
    private CardLayout cardLayout;

    private JLabel lblTieuDe, lblSubtitle;

    // ── Constructor ───────────────────────────────────────────
    public QuenMKUI() {
        setTitle("Quên mật khẩu");
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Controller xử lý
        setResizable(true);

        BgPanel bgPanel = new BgPanel(DEFAULT_BG);
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        // ── Card chính ────────────────────────────────────────
        DangNhapUI.GlassCard card = new DangNhapUI.GlassCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(470, 480));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;
        int row = 0;

        // ── Icon khóa ─────────────────────────────────────────
        g.gridy = row++; g.insets = ins(0, 0, 14, 0);
        card.add(buildIconPanel(), g);

        // ── Tiêu đề động ──────────────────────────────────────
        lblTieuDe = new JLabel("Quên mật khẩu?", SwingConstants.CENTER);
        lblTieuDe.setFont(F_TITLE); lblTieuDe.setForeground(LABEL_FG);
        lblSubtitle = new JLabel("Nhập email để nhận mã xác thực", SwingConstants.CENTER);
        lblSubtitle.setFont(F_SUB); lblSubtitle.setForeground(HINT_FG);

        g.gridy = row++; g.insets = ins(0, 0, 4, 0);  card.add(lblTieuDe,   g);
        g.gridy = row++; g.insets = ins(0, 0, 28, 0); card.add(lblSubtitle, g);

        // ── CardLayout chứa 3 bước ────────────────────────────
        cardLayout    = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);

        panelBuoc1 = buildBuoc1();
        panelBuoc2 = buildBuoc2();
        panelBuoc3 = buildBuoc3();

        cardContainer.add(panelBuoc1, "BUOC1");
        cardContainer.add(panelBuoc2, "BUOC2");
        cardContainer.add(panelBuoc3, "BUOC3");

        g.gridy = row++; g.insets = ins(0, 0, 20, 0);
        card.add(cardContainer, g);

        // ── Quay lại ──────────────────────────────────────────
        lblQuayLai = new JLabel("← Quay lại đăng nhập", SwingConstants.CENTER);
        lblQuayLai.setFont(F_LINK);
        lblQuayLai.setForeground(HINT_FG);
        lblQuayLai.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblQuayLai.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblQuayLai.setForeground(ACCENT); }
            public void mouseExited (MouseEvent e) { lblQuayLai.setForeground(HINT_FG); }
        });
        g.gridy = row; g.insets = ins(0, 0, 0, 0);
        card.add(wrapCenter(lblQuayLai), g);

        GridBagConstraints fc = new GridBagConstraints();
        fc.anchor = GridBagConstraints.CENTER;
        fc.weightx = fc.weighty = 1;
        fc.insets = new Insets(24, 24, 24, 24);
        bgPanel.add(card, fc);

        pack();
        setLocationRelativeTo(null);
    }

    // ── Bước 1: Nhập email ────────────────────────────────────
    private JPanel buildBuoc1() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        txtEmail = modernField("✉  Nhập địa chỉ email của bạn...");
        p.add(fieldGroup("ĐỊA CHỈ EMAIL", txtEmail, null));
        p.add(Box.createVerticalStrut(20));
        btnGuiMa = gradientBtn("Gửi mã xác thực  →");
        p.add(btnGuiMa);
        return p;
    }

    // ── Bước 2: Nhập mã OTP ───────────────────────────────────
    private JPanel buildBuoc2() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        txtMa = modernField("🔑  Nhập mã 6 chữ số...");
        p.add(fieldGroup("MÃ XÁC THỰC", txtMa, "Kiểm tra hộp thư, kể cả Spam"));
        p.add(Box.createVerticalStrut(20));
        btnXacMinhMa = gradientBtn("Xác nhận mã  →");
        p.add(btnXacMinhMa);
        return p;
    }

    // ── Bước 3: Đặt mật khẩu mới ─────────────────────────────
    private JPanel buildBuoc3() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        txtMKMoi   = modernPassField();
        txtXNMKMoi = modernPassField();

        // Eye toggle đồng bộ 2 field
        JCheckBox eye = eyeToggle();
        eye.addActionListener(e -> {
            boolean show = eye.isSelected();
            txtMKMoi.setEchoChar(show ? (char) 0 : '●');
            txtXNMKMoi.setEchoChar(show ? (char) 0 : '●');
        });

        JPanel rowMK = new JPanel(new BorderLayout(6, 0));
        rowMK.setOpaque(false);
        rowMK.add(txtMKMoi, BorderLayout.CENTER);
        rowMK.add(eye,      BorderLayout.EAST);

        p.add(fieldGroup("MẬT KHẨU MỚI", rowMK, null));
        p.add(Box.createVerticalStrut(GAP_FIELD));
        p.add(fieldGroup("XÁC NHẬN MẬT KHẨU MỚI", txtXNMKMoi, null));
        p.add(Box.createVerticalStrut(20));
        btnDatLai = gradientBtn("Đặt lại mật khẩu  →");
        p.add(btnDatLai);
        return p;
    }

    // ── Chuyển bước (gọi từ Controller) ──────────────────────

    public void chuyenBuoc(int buoc, String email) {
        switch (buoc) {
            case 2:
                lblTieuDe.setText("Nhập mã xác thực");
                lblSubtitle.setText("Mã đã gửi đến: " + email);
                cardLayout.show(cardContainer, "BUOC2");
                pack();
                break;

            case 3:
                lblTieuDe.setText("Đặt mật khẩu mới");
                lblSubtitle.setText("Nhập mật khẩu mới cho tài khoản của bạn");
                cardLayout.show(cardContainer, "BUOC3");
                pack();
                break;
        }
    }

    // ── Icon khóa ─────────────────────────────────────────────
    private JPanel buildIconPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(new LockIcon());
        return p;
    }

    // ══════════════════════════════════════════════════════════
    // Widget Factories (giống DangNhapUI / DangKiUI)
    // ══════════════════════════════════════════════════════════

    public JTextField modernField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? INPUT_BG_FOC : INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(F_INPUT); f.setForeground(HINT_FG);
        f.setCaretColor(ACCENT); f.setOpaque(false);
        f.setPreferredSize(new Dimension(0, FIELD_H));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
        f.setText(placeholder);
        applyModernBorder(f, false);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(LABEL_FG); }
                applyModernBorder(f, true); f.repaint();
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(HINT_FG); }
                applyModernBorder(f, false); f.repaint();
            }
        });
        return f;
    }

    public JPasswordField modernPassField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? INPUT_BG_FOC : INPUT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(F_INPUT); f.setForeground(LABEL_FG);
        f.setCaretColor(ACCENT); f.setEchoChar('●');
        f.setOpaque(false);
        f.setPreferredSize(new Dimension(0, FIELD_H));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_H));
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
        cb.setFocusPainted(false); cb.setBorderPainted(false);
        return cb;
    }

    public void applyModernBorder(JComponent c, boolean focused) {
        c.setBorder(new CompoundBorder(
            new LineBorder(focused ? ACCENT : new Color(255, 255, 255, 140),
                           focused ? 2 : 1, true),
            new EmptyBorder(0, 14, 0, 14)
        ));
    }

    private JPanel fieldGroup(String label, JComponent field, String hint) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL); lbl.setForeground(HINT_FG);
        lbl.setBorder(new EmptyBorder(0, 2, GAP_LABEL, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl); p.add(field);
        if (hint != null) {
            JLabel h = new JLabel("  " + hint);
            h.setFont(F_HINT); h.setForeground(HINT_FG);
            h.setBorder(new EmptyBorder(4, 2, 0, 0));
            h.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(h);
        }
        return p;
    }

    private JPanel wrapCenter(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false); p.add(c); return p;
    }

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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                if (hov && !press) { g2.setColor(new Color(91,141,239,55)); g2.fillRoundRect(4,6,w-8,h-4,14,14); }
                Color c1 = press ? ACCENT_DARK : hov ? new Color(0x4A7EE8) : ACCENT;
                Color c2 = press ? new Color(0x2A56BB) : hov ? ACCENT_DARK : new Color(0x3D6FD4);
                g2.setPaint(new GradientPaint(0,0,c1,w,h,c2));
                g2.fillRoundRect(0, 0, w, h-(press?0:2), 14, 14);
                g2.setColor(new Color(255,255,255,40)); g2.fillRoundRect(0,0,w,h/2,14,14);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BTN); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setContentAreaFilled(false);
        b.setBorderPainted(false); b.setOpaque(false);
        b.setPreferredSize(new Dimension(0, 50));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Tiện ích ──────────────────────────────────────────────
    private Insets ins(int t, int l, int b, int r) { return new Insets(t, l, b, r); }

    public void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
    public void showInfo(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    public void setBtnLoading(JButton btn, boolean loading, String normalText) {
        btn.setEnabled(!loading);
        btn.setText(loading ? "Đang xử lý..." : normalText);
    }

    // ══════════════════════════════════════════════════════════
    // Inner: Nền ảnh cố định
    // ══════════════════════════════════════════════════════════
    private static class BgPanel extends JPanel {
        private BufferedImage bgImage;
        BgPanel(String path) {
            setOpaque(true);
            try { BufferedImage img = ImageIO.read(new File(path)); if (img != null) bgImage = img; }
            catch (Exception ignored) {}
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            int pw = getWidth(), ph = getHeight();
            if (bgImage != null) {
                int iw = bgImage.getWidth(), ih = bgImage.getHeight();
                double scale = Math.max((double)pw/iw, (double)ph/ih);
                int nw = (int)(iw*scale), nh = (int)(ih*scale);
                g2.drawImage(bgImage, (pw-nw)/2, (ph-nh)/2, nw, nh, null);
                g2.setPaint(new GradientPaint(0,0,new Color(10,20,60,80),0,ph,new Color(5,10,40,50)));
                g2.fillRect(0, 0, pw, ph);
            } else { g2.setColor(BG); g2.fillRect(0,0,pw,ph); }
            g2.dispose();
        }
    }

    // ══════════════════════════════════════════════════════════
    // Inner: Icon khóa
    // ══════════════════════════════════════════════════════════
    private static class LockIcon extends JPanel {
        private static final int S = 72;
        LockIcon() { setOpaque(false); setPreferredSize(new Dimension(S, S)); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Ring gradient
            g2.setPaint(new GradientPaint(0, 0, ACCENT, S, S, ACCENT_LIGHT));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(1, 1, S-3, S-3);

            // Fill
            g2.setPaint(new GradientPaint(6, 6, ACCENT, S-6, S-6, ACCENT_DARK));
            g2.fillOval(6, 6, S-12, S-12);

            // Highlight
            g2.setColor(new Color(255,255,255,35));
            g2.fillOval(6, 6, (S-12)/2, S-12);

            // Icon khóa (vẽ thủ công)
            int cx = S/2, cy = S/2;
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Thân khóa
            g2.fillRoundRect(cx-11, cy-4, 22, 16, 5, 5);
            // Vòng cung khóa
            g2.setColor(new Color(255,255,255,220));
            g2.drawArc(cx-8, cy-16, 16, 16, 0, 180);
            // Lỗ khóa
            g2.setColor(ACCENT_DARK);
            g2.fillOval(cx-3, cy+1, 6, 6);

            g2.dispose();
        }
    }
}

package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * DangKiUI – Giao diện màn hình đăng ký.
 * Phong cách: Premium Glassmorphism – sang trọng, hiện đại.
 * Ảnh nền cố định, card liquid glass, spacing đồng nhất.
 */
public class DangKiUI extends JFrame {

    // ── Đường dẫn ảnh nền cố định ────────────────────────────
    private static final String DEFAULT_BG =
        "src/main/resources/Untitled design.jpg";

    // ── Bảng màu ──────────────────────────────────────────────
    public static final Color BG           = new Color(0xF0F2F5);
    public static final Color ACCENT       = new Color(0x5B8DEF);
    public static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    public static final Color ACCENT_LIGHT = new Color(0xA8C4FB);
    public static final Color LABEL_FG     = new Color(0x0F172A);
    public static final Color HINT_FG      = new Color(0x64748B);
    public static final Color RING_COLOR   = new Color(0xBFD3F8);
    public static final Color AVATAR_BG    = new Color(0xDBEAFE);
    public static final Color AVATAR_ICON  = new Color(0x93B4F5);
    public static final Color INPUT_BG     = new Color(255, 255, 255, 200);
    public static final Color INPUT_BG_FOC = new Color(255, 255, 255, 240);
    public static final Color GLASS_BG     = new Color(255, 255, 255, 145);
    public static final Color BORDER       = new Color(255, 255, 255, 100);

    // ── Font chữ ──────────────────────────────────────────────
    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,   22);
    public static final Font F_SUB   = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,   11);
    public static final Font F_INPUT = new Font("Segoe UI", Font.PLAIN,  13);
    public static final Font F_HINT  = new Font("Segoe UI", Font.ITALIC, 11);
    public static final Font F_BTN   = new Font("Segoe UI", Font.BOLD,   14);
    public static final Font F_LINK  = new Font("Segoe UI", Font.BOLD,   12);

    // Khoảng cách đồng nhất
    private static final int GAP_FIELD  = 14; // giữa các field group
    private static final int GAP_LABEL  = 5;  // label → field
    private static final int FIELD_H    = 44; // chiều cao field

    // ── Widgets công khai cho Controller ──────────────────────
    public JTextField     txtTen, txtEmail, txtMa;
    public JPasswordField txtMK, txtXNMK;
    public JButton        btnLayMa, btnDangKi;
    public AvatarPanel    avatarPanel;

    // ── Constructor ───────────────────────────────────────────
    public DangKiUI() {
        setTitle("Tạo tài khoản");
        setMinimumSize(new Dimension(500, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        BgPanel bgPanel = new BgPanel(DEFAULT_BG);
        bgPanel.setLayout(new GridBagLayout());
        setContentPane(bgPanel);

        GlassCard card = new GlassCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(36, 40, 36, 40));
        card.setPreferredSize(new Dimension(470, 790));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        int row = 0;

        // ── Tiêu đề ───────────────────────────────────────────
        g.gridy = row++; g.insets = ins(0, 0, 6, 0);
        card.add(buildHeader(), g);

        // ── Avatar ────────────────────────────────────────────
        avatarPanel = new AvatarPanel(this);
        JPanel avatarWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarWrap.setOpaque(false);
        avatarWrap.add(avatarPanel);
        g.gridy = row++; g.insets = ins(0, 0, 6, 0);
        card.add(avatarWrap, g);

        JLabel lblAvatar = makeLink("+ Thêm ảnh đại diện");
        lblAvatar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { avatarPanel.chonAnh(); }
            public void mouseEntered(MouseEvent e) { lblAvatar.setForeground(ACCENT_DARK); }
            public void mouseExited (MouseEvent e) { lblAvatar.setForeground(ACCENT); }
        });
        g.gridy = row++; g.insets = ins(0, 0, 24, 0);
        card.add(wrapCenter(lblAvatar), g);

        // ── Tên đăng nhập ─────────────────────────────────────
        txtTen = modernField("👤  Nhập tên đăng nhập...");
        g.gridy = row++; g.insets = ins(0, 0, GAP_FIELD, 0);
        card.add(fieldGroup("TÊN ĐĂNG NHẬP", txtTen, null), g);

        // ── Mật khẩu ──────────────────────────────────────────
        txtMK = modernPassField();
        JCheckBox chkShowMK = eyeToggle();

        txtXNMK = modernPassField();
        JCheckBox chkShowXNMK = eyeToggle();

        // Khi nhấn "hiện MK" → tự động hiện/ẩn cả XNMK và sync checkbox
        chkShowMK.addActionListener(e -> {
            boolean show = chkShowMK.isSelected();
            txtMK.setEchoChar(show ? (char) 0 : '●');
            txtXNMK.setEchoChar(show ? (char) 0 : '●');
            chkShowXNMK.setSelected(show); // sync trạng thái checkbox XNMK
        });

        // Checkbox XNMK vẫn hoạt động độc lập nếu muốn
        chkShowXNMK.addActionListener(e ->
            txtXNMK.setEchoChar(chkShowXNMK.isSelected() ? (char) 0 : '●'));

        g.gridy = row++; g.insets = ins(0, 0, GAP_FIELD, 0);
        card.add(fieldGroup("MẬT KHẨU", passRow(txtMK, chkShowMK), null), g);

        g.gridy = row++; g.insets = ins(0, 0, GAP_FIELD, 0);
        card.add(fieldGroup("XÁC NHẬN MẬT KHẨU", passRow(txtXNMK, chkShowXNMK), null), g);

        // ── Email ─────────────────────────────────────────────
        txtEmail = modernField("✉  example@email.com");
        g.gridy = row++; g.insets = ins(0, 0, GAP_FIELD, 0);
        card.add(fieldGroup("ĐỊA CHỈ EMAIL", txtEmail, "Dùng để khôi phục tài khoản"), g);

        // ── Mã xác thực ───────────────────────────────────────
        txtMa    = modernField("🔑  Nhập mã 6 chữ số...");
        btnLayMa = modernOutlineBtn("Lấy mã");
        JPanel rowMa = new JPanel(new BorderLayout(10, 0));
        rowMa.setOpaque(false);
        rowMa.add(txtMa,    BorderLayout.CENTER);
        rowMa.add(btnLayMa, BorderLayout.EAST);
        g.gridy = row++; g.insets = ins(0, 0, 28, 0);
        card.add(fieldGroup("MÃ XÁC THỰC EMAIL", rowMa, null), g);

        // ── Nút Đăng ký ───────────────────────────────────────
        btnDangKi = gradientBtn("Tạo tài khoản  →");
        g.gridy = row; g.insets = ins(0, 0, 0, 0);
        card.add(btnDangKi, g);

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

    /** Header: tiêu đề + subtitle (không có badge) */
    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel title = new JLabel("Tạo tài khoản mới", SwingConstants.CENTER);
        title.setFont(F_TITLE);
        title.setForeground(LABEL_FG);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Điền thông tin để bắt đầu hành trình", SwingConstants.CENTER);
        sub.setFont(F_SUB);
        sub.setForeground(HINT_FG);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(title);
        p.add(Box.createVerticalStrut(6));
        p.add(sub);
        return p;
    }

    /**
     * Nhóm: label (chữ hoa nhỏ) + field + hint tùy chọn.
     * Tất cả field group dùng chung method này → spacing đồng nhất.
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

    private JPanel wrapCenter(JComponent c) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    private JPanel passRow(JPasswordField pf, JCheckBox toggle) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false);
        p.add(pf,     BorderLayout.CENTER);
        p.add(toggle, BorderLayout.EAST);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    // Widget Factories
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

    private JLabel makeLink(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(F_LINK);
        l.setForeground(ACCENT);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    public void applyModernBorder(JComponent c, boolean focused) {
        c.setBorder(new CompoundBorder(
            new LineBorder(focused ? ACCENT : new Color(255, 255, 255, 140),
                           focused ? 2 : 1, true),
            new EmptyBorder(0, 14, 0, 14)
        ));
    }

    // Backward compat
    public JTextField     styledField(String ph) { return modernField(ph); }
    public JPasswordField passField()            { return modernPassField(); }
    public void applyBorder(JComponent c, boolean f) { applyModernBorder(c, f); }

    public JButton modernOutlineBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(219, 234, 254)
                                : new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(ACCENT);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setOpaque(false);
        b.setPreferredSize(new Dimension(90, FIELD_H)); // khớp chiều cao field
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
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

    public JButton filledBtn(String text)  { return gradientBtn(text); }
    public JButton outlineBtn(String text) { return modernOutlineBtn(text); }

    // ══════════════════════════════════════════════════════════
    // Inner: Nền ảnh cố định
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
    // Inner: Card Liquid Glass
    // ══════════════════════════════════════════════════════════
    public static class GlassCard extends JPanel {
        private static final int R = 28;

        GlassCard() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            // Bóng đổ
            for (int i = 12; i >= 1; i--) {
                int alpha = (int) Math.min(5f + (12 - i) * 1.5f, 35);
                g2.setColor(new Color(10, 25, 80, alpha));
                g2.fillRoundRect(i, i + 4, w - i*2, h - i*2, R + i, R + i);
            }

            Shape shape = new RoundRectangle2D.Float(0, 0, w, h, R, R);
            g2.setClip(shape);

            g2.setColor(GLASS_BG);
            g2.fill(shape);

            g2.setPaint(new GradientPaint(
                0, 0,         new Color(255, 255, 255, 150),
                0, h * 0.35f, new Color(255, 255, 255, 0)
            ));
            g2.fill(shape);

            g2.setPaint(new GradientPaint(
                0, h * 0.72f, new Color(255, 255, 255, 0),
                0, h,          new Color(255, 255, 255, 55)
            ));
            g2.fill(shape);

            g2.setPaint(new GradientPaint(
                0, h * 0.3f, new Color(91, 141, 239, 18),
                w, h * 0.7f, new Color(91, 141, 239, 0)
            ));
            g2.fill(shape);

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
    // Inner: Avatar Panel
    // ══════════════════════════════════════════════════════════
    public static class AvatarPanel extends JPanel {
        private static final int S = 100;
        private BufferedImage img = null;
        private final JFrame owner;
        private boolean hovered = false;

        AvatarPanel(JFrame owner) {
            this.owner = owner;
            setOpaque(false);
            setPreferredSize(new Dimension(S, S));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText("Click để chọn ảnh đại diện");
            addMouseListener(new MouseAdapter() {
                public void mouseClicked (MouseEvent e) { chonAnh(); }
                public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e)  { hovered = false; repaint(); }
            });
        }

        public void chonAnh() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn ảnh đại diện");
            fc.setFileFilter(new FileNameExtensionFilter(
                "Ảnh (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
            if (fc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage loaded = ImageIO.read(fc.getSelectedFile());
                    if (loaded == null) throw new Exception("Không đọc được ảnh.");
                    img = loaded;
                    repaint();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(owner,
                        "Không thể tải ảnh: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        public BufferedImage getImage() { return img; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = 5, inner = S - pad * 2;

            if (hovered) {
                g2.setColor(new Color(91, 141, 239, 50));
                g2.fillOval(-3, -3, S + 6, S + 6);
            }
            g2.setPaint(new GradientPaint(0, 0, ACCENT, S, S, ACCENT_LIGHT));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(1, 1, S - 3, S - 3);

            Shape circle = new Ellipse2D.Float(pad, pad, inner, inner);
            g2.setClip(circle);

            if (img != null) {
                int iw = img.getWidth(), ih = img.getHeight();
                int size = Math.min(iw, ih);
                BufferedImage cropped = img.getSubimage((iw-size)/2, (ih-size)/2, size, size);
                g2.drawImage(cropped.getScaledInstance(inner, inner, Image.SCALE_SMOOTH),
                             pad, pad, null);
                if (hovered) { g2.setColor(new Color(91, 141, 239, 60)); g2.fill(circle); }
            } else {
                g2.setPaint(new GradientPaint(pad, pad, AVATAR_BG,
                                              pad+inner, pad+inner, new Color(0xC7D7F8)));
                g2.fill(circle);
                g2.setColor(AVATAR_ICON);
                int cx = S / 2;
                g2.fillOval(cx - 13, 18, 26, 26);
                g2.fillRoundRect(cx - 19, 47, 38, 28, 18, 18);
                if (hovered) {
                    g2.setColor(new Color(91, 141, 239, 170));
                    g2.fill(circle);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                                                  BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx - 10, cx, cx + 10, cx);
                    g2.drawLine(cx, cx - 10, cx, cx + 10);
                }
            }
            g2.setClip(null);

            int bx = S - 28, by = S - 28;
            g2.setColor(Color.WHITE);
            g2.fillOval(bx - 2, by - 2, 26, 26);
            g2.setPaint(new GradientPaint(bx, by, ACCENT, bx+22, by+22, ACCENT_DARK));
            g2.fillOval(bx, by, 22, 22);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(bx + 4, by + 6, 14, 10, 3, 3);
            g2.fillOval(bx + 8, by + 8, 6, 6);

            g2.dispose();
        }
    }

    // ── Tiện ích ──────────────────────────────────────────────
    private Insets ins(int t, int l, int b, int r) { return new Insets(t, l, b, r); }

    public void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
    }
    public void showInfo(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // ── main test ─────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new DangKiUI().setVisible(true);
        });
    }
}
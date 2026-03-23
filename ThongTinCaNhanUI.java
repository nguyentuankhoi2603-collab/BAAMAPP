package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * ThongTinCaNhanUI – Thông tin cá nhân (editable) + Quản lý tài khoản.
 * Theo sơ đồ:
 *   Trái: Avatar + Thay đổi ảnh + Tên + Ngày sinh + Email + Cập nhật thông tin
 *   Phải (dialog): Quản lý TK → Tên TK | Thông tin cá nhân | Đổi MK | Đăng xuất
 */
public class ThongTinCaNhanUI extends JPanel {

    private static final Color ACCENT       = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    private static final Color ACCENT_LIGHT = new Color(0xA8C4FB);
    private static final Color BORDER_CLR   = new Color(220, 226, 240);
    private static final Color TEXT_DARK    = new Color(0x1E293B);
    private static final Color TEXT_MID     = new Color(0x475569);
    private static final Color TEXT_LIGHT   = new Color(0x94A3B8);
    private static final Color BG_WHITE     = Color.WHITE;
    private static final Color BG_LIGHT     = new Color(245, 247, 252);
    private static final Color DANGER       = new Color(0xEF4444);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  16);
    private static final Font F_NAME   = new Font("Segoe UI", Font.BOLD,  20);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,  10);

    // ── Widgets công khai ─────────────────────────────────────
    public JTextField     txtTen;
    public JTextField     txtNgaySinh;
    public JTextField     txtEmail;
    public JButton        btnCapNhat;
    public JButton        btnQuanLyTK;
    public AvatarEdit     avatarPanel;

    private NguoiDung nguoiDung;

    public ThongTinCaNhanUI(NguoiDung nd) {
        this.nguoiDung = nd;
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_WHITE);
        h.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 24, 14, 24)
        ));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Thông tin cá nhân");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Xem và chỉnh sửa hồ sơ của bạn");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        left.add(t); left.add(Box.createVerticalStrut(2)); left.add(s);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        btnQuanLyTK = buildOutlineBtn("⚙  Quản lý tài khoản");
        right.add(btnQuanLyTK);

        h.add(left,  BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── Body: 2 cột ──────────────────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(BG_LIGHT);
        body.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(0, 0, 0, 16);

        // Cột trái: avatar card
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.35; gc.weighty = 1;
        body.add(buildAvatarCard(), gc);

        // Cột phải: form card
        gc.gridx = 1; gc.weightx = 0.65; gc.insets = new Insets(0, 0, 0, 0);
        body.add(buildFormCard(), gc);

        return body;
    }

    // ── Avatar Card ───────────────────────────────────────────
    private JPanel buildAvatarCard() {
        JPanel card = buildCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 24, 32, 24));

        avatarPanel = new AvatarEdit(nguoiDung, null);
        JPanel avWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avWrap.setOpaque(false);
        avWrap.add(avatarPanel);
        card.add(avWrap);
        card.add(Box.createVerticalStrut(12));

        // Tên hiển thị
        JLabel lblName = new JLabel(nguoiDung != null ? nguoiDung.getTenDangNhap() : "Người dùng");
        lblName.setFont(F_NAME); lblName.setForeground(TEXT_DARK);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));

        JLabel lblEmail = new JLabel(nguoiDung != null ? nguoiDung.getEmail() : "");
        lblEmail.setFont(F_SMALL); lblEmail.setForeground(TEXT_LIGHT);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblEmail);
        card.add(Box.createVerticalStrut(20));

        // Thay ảnh link
        JLabel lblChangeAvatar = new JLabel("Thay đổi ảnh đại diện");
        lblChangeAvatar.setFont(F_BOLD); lblChangeAvatar.setForeground(ACCENT);
        lblChangeAvatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChangeAvatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblChangeAvatar.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { avatarPanel.chonAnh(); }
            public void mouseEntered(MouseEvent e) { lblChangeAvatar.setForeground(ACCENT_DARK); }
            public void mouseExited (MouseEvent e) { lblChangeAvatar.setForeground(ACCENT); }
        });
        card.add(lblChangeAvatar);
        return card;
    }

    // ── Form Card ─────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = buildCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel sec = new JLabel("THÔNG TIN CÁ NHÂN");
        sec.setFont(F_LABEL); sec.setForeground(TEXT_LIGHT);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sec);
        card.add(Box.createVerticalStrut(18));

        // Tên
        card.add(sectionLabel("TÊN ĐẦY ĐỦ"));
        card.add(Box.createVerticalStrut(6));
        txtTen = buildField(nguoiDung != null ? nguoiDung.getTenDangNhap() : "Nhập tên...");
        card.add(txtTen);
        card.add(Box.createVerticalStrut(16));

        // Ngày sinh
        card.add(sectionLabel("NGÀY SINH"));
        card.add(Box.createVerticalStrut(6));
        txtNgaySinh = buildField("26/03/2003");
        card.add(txtNgaySinh);
        card.add(Box.createVerticalStrut(16));

        // Email
        card.add(sectionLabel("EMAIL"));
        card.add(Box.createVerticalStrut(6));
        txtEmail = buildField(nguoiDung != null ? nguoiDung.getEmail() : "email@example.com");
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(24));

        // Nút cập nhật
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrap.setOpaque(false);
        btnWrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCapNhat = buildAccentBtn("Cập nhật thông tin");
        btnWrap.add(btnCapNhat);
        card.add(btnWrap);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,10));
                g2.fillRoundRect(2,3,getWidth()-4,getHeight()-4,14,14);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0,0,getWidth()-2,getHeight()-2,12,12);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-3,getHeight()-3,12,12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private JTextField buildField(String val) {
        JTextField f = new JTextField(val) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner()?new Color(235,242,255):BG_WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        f.setFont(F_BODY); f.setForeground(TEXT_DARK); f.setOpaque(false);
        f.setCaretColor(ACCENT);
        f.setPreferredSize(new Dimension(0, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true), new EmptyBorder(0,12,0,12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(ACCENT,2,true),new EmptyBorder(0,12,0,12)));
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true),new EmptyBorder(0,12,0,12)));
                f.repaint();
            }
        });
        return f;
    }

    private JButton buildAccentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,hov?new Color(0x4A7EE8):ACCENT,getWidth(),getHeight(),ACCENT_DARK));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,24,10,24));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton buildOutlineBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov?new Color(235,242,255):BG_WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(TEXT_MID);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9,18,9,18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════
    // Inner: AvatarEdit – avatar tròn có thể click để đổi ảnh
    // ══════════════════════════════════════════════════════════
    public static class AvatarEdit extends JPanel {
        private static final int S = 100;
        private BufferedImage img;
        private final JFrame owner;
        private boolean hov;

        public AvatarEdit(NguoiDung nd, JFrame owner) {
            this.owner = owner;
            if (nd != null) this.img = nd.getAnhDaiDien();
            setOpaque(false);
            setPreferredSize(new Dimension(S, S));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) { chonAnh(); }
                public void mouseEntered(MouseEvent e) { hov=true; repaint(); }
                public void mouseExited (MouseEvent e) { hov=false;repaint(); }
            });
        }

        public void chonAnh() {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Ảnh","jpg","jpeg","png","gif"));
            if (fc.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
                try { img = ImageIO.read(fc.getSelectedFile()); repaint(); }
                catch (Exception ignored) {}
            }
        }

        public BufferedImage getImage() { return img; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0,0,ACCENT,S,S,ACCENT_LIGHT));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(1,1,S-3,S-3);
            Shape clip = new Ellipse2D.Float(5,5,S-10,S-10);
            g2.setClip(clip);
            if (img != null) {
                int w=img.getWidth(),h=img.getHeight(),sz=Math.min(w,h);
                BufferedImage c=img.getSubimage((w-sz)/2,(h-sz)/2,sz,sz);
                g2.drawImage(c.getScaledInstance(S-10,S-10,Image.SCALE_SMOOTH),5,5,null);
            } else {
                g2.setColor(new Color(0xDBEAFE));
                g2.fillOval(5,5,S-10,S-10);
                g2.setColor(new Color(0x93B4F5));
                int cx=S/2;
                g2.fillOval(cx-12,16,24,24);
                g2.fillRoundRect(cx-17,43,34,26,14,14);
            }
            if (hov) {
                g2.setColor(new Color(91,141,239,100)); g2.fill(clip);
                g2.setClip(null); g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,12));
                FontMetrics fm=g2.getFontMetrics();
                String txt="Thay ảnh";
                g2.drawString(txt,S/2-fm.stringWidth(txt)/2,S/2+fm.getAscent()/2-2);
            }
            g2.dispose();
        }
    }
}

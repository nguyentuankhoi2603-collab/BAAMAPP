package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

/**
 * ThongTinThanhVienUI – Thông tin thành viên (readonly).
 * Theo sơ đồ: Avatar + Tên + Ngày sinh + Email
 */
public class ThongTinThanhVienUI extends JDialog {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color ACCENT_LIGHT= new Color(0xA8C4FB);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_NAME   = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,  10);

    // ── Widgets công khai ─────────────────────────────────────
    public JLabel lblTen;
    public JLabel lblEmail;
    public JLabel lblNgaySinh;
    public JLabel lblTrangThai;
    public JButton btnQuayLai;

    private NguoiDung nguoiDung;

    public ThongTinThanhVienUI(Frame owner, NguoiDung nd) {
        super(owner, "Thông tin thành viên", true);
        this.nguoiDung = nd;
        setSize(400, 460);
        setMinimumSize(new Dimension(360, 420));
        setResizable(false);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_WHITE);
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_WHITE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(14, 20, 12, 20)
        ));
        JLabel t = new JLabel("Thông tin thành viên");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        header.add(t, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(BG_WHITE);
        body.setBorder(new EmptyBorder(24, 28, 20, 28));

        // Avatar section
        JPanel avatarSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarSection.setOpaque(false);
        avatarSection.add(new AvatarCircle(nd, 90));
        body.add(avatarSection, BorderLayout.NORTH);

        // Info section
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Tên
        lblTen = new JLabel(nd != null ? nd.getTenDangNhap() : "Nguyễn Tuấn Khôi");
        lblTen.setFont(F_NAME);
        lblTen.setForeground(TEXT_DARK);
        lblTen.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Trạng thái online
        lblTrangThai = new JLabel("● Đang hoạt động");
        lblTrangThai.setFont(F_SMALL);
        lblTrangThai.setForeground(new Color(0x22C55E));
        lblTrangThai.setAlignmentX(Component.CENTER_ALIGNMENT);

        info.add(lblTen); info.add(Box.createVerticalStrut(4)); info.add(lblTrangThai);
        info.add(Box.createVerticalStrut(24));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_CLR);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(sep);
        info.add(Box.createVerticalStrut(18));

        // Fields
        info.add(buildInfoRow("TÊN ĐẦY ĐỦ",  nd != null ? nd.getTenDangNhap() : "Nguyễn Tuấn Khôi"));
        info.add(Box.createVerticalStrut(14));
        lblEmail = buildInfoRowLabel("EMAIL", nd != null ? nd.getEmail() : "ntkl@gmail.com");
        info.add(buildInfoRowPanel("EMAIL", lblEmail));
        info.add(Box.createVerticalStrut(14));
        lblNgaySinh = buildInfoRowLabel("NGÀY SINH", "26/03/2003");
        info.add(buildInfoRowPanel("NGÀY SINH", lblNgaySinh));

        body.add(info, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        // Bottom
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setBackground(BG_WHITE);
        btnBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnQuayLai = buildOutlineBtn("← Quay lại");
        btnQuayLai.addActionListener(e -> dispose());
        btnBar.add(btnQuayLai);
        root.add(btnBar, BorderLayout.SOUTH);
    }

    private JPanel buildInfoRow(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_LIGHT);
        JLabel val = new JLabel(value);
        val.setFont(F_BOLD); val.setForeground(TEXT_DARK);
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(lbl); col.add(Box.createVerticalStrut(3)); col.add(val);
        p.add(col, BorderLayout.WEST);
        return p;
    }

    private JLabel buildInfoRowLabel(String label, String value) {
        JLabel l = new JLabel(value);
        l.setFont(F_BOLD); l.setForeground(TEXT_DARK);
        return l;
    }

    private JPanel buildInfoRowPanel(String label, JLabel valLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        JLabel lbl = new JLabel(label);
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_LIGHT);
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.add(lbl); col.add(Box.createVerticalStrut(3)); col.add(valLabel);
        p.add(col, BorderLayout.WEST);
        return p;
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
        b.setBorder(new EmptyBorder(9,20,9,20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static final Color TEXT_MID_1 = new Color(0x475569);

    // ── AvatarCircle inner ────────────────────────────────────
    private static class AvatarCircle extends JPanel {
        private final NguoiDung nd; private final int S;
        AvatarCircle(NguoiDung nd, int s) {
            this.nd = nd; this.S = s;
            setOpaque(false); setPreferredSize(new Dimension(S, S));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new GradientPaint(0, 0, ACCENT, S, S, ACCENT_LIGHT));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(1, 1, S-3, S-3);
            g2.setClip(new Ellipse2D.Float(5, 5, S-10, S-10));
            if (nd != null && nd.getAnhDaiDien() != null) {
                g2.drawImage(nd.getAnhDaiDien().getScaledInstance(S-10,S-10,Image.SCALE_SMOOTH),5,5,null);
            } else {
                g2.setColor(new Color(0xDBEAFE));
                g2.fillOval(5, 5, S-10, S-10);
                g2.setColor(new Color(0x93B4F5));
                int cx=S/2;
                g2.fillOval(cx-12,16,24,24);
                g2.fillRoundRect(cx-17,43,34,26,14,14);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            NguoiDung u = new NguoiDung(1,"nguyentk","hash","ntk@gmail.com");
            new ThongTinThanhVienUI(null, u).setVisible(true);
        });
    }
}

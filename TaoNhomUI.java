package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * TaoNhomUI – Dialog Tạo nhóm.
 * Theo sơ đồ: Nhập tên nhóm + Nhập ID nhóm
 */
public class TaoNhomUI extends JDialog {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,  10);

    // ── Widgets công khai ─────────────────────────────────────
    public JTextField txtTenNhom;
    public JTextField txtIdNhom;
    public JButton    btnTao;
    public JButton    btnHuy;

    public TaoNhomUI(Frame owner) {
        super(owner, "Tạo nhóm mới", true);
        setSize(420, 320);
        setMinimumSize(new Dimension(380, 280));
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
            new EmptyBorder(18, 24, 16, 24)
        ));
        JLabel t = new JLabel("Tạo nhóm mới");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Điền thông tin để tạo nhóm");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        JPanel hCol = new JPanel();
        hCol.setOpaque(false);
        hCol.setLayout(new BoxLayout(hCol, BoxLayout.Y_AXIS));
        hCol.add(t); hCol.add(Box.createVerticalStrut(3)); hCol.add(s);
        header.add(hCol, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(24, 28, 20, 28));

        form.add(sectionLabel("TÊN NHÓM"));
        form.add(Box.createVerticalStrut(6));
        txtTenNhom = field("Nhập tên nhóm...");
        form.add(txtTenNhom);
        form.add(Box.createVerticalStrut(18));

        form.add(sectionLabel("ID NHÓM (tùy chọn)"));
        form.add(Box.createVerticalStrut(6));
        txtIdNhom = field("Để trống để tự động tạo ID...");
        form.add(txtIdNhom);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setBackground(BG_WHITE);
        btnBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnHuy = outlineBtn("Hủy");
        btnTao = accentBtn("Tạo nhóm");
        btnHuy.addActionListener(e -> dispose());
        btnBar.add(btnHuy);
        btnBar.add(btnTao);
        root.add(btnBar, BorderLayout.SOUTH);
    }

    // ── Helpers ───────────────────────────────────────────────
    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t); l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
    }

    private JTextField field(String ph) {
        JTextField f = new JTextField();
        f.setFont(F_BODY); f.setForeground(TEXT_LIGHT);
        f.setCaretColor(ACCENT); f.setText(ph);
        f.setPreferredSize(new Dimension(0, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true), new EmptyBorder(0,12,0,12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph)) { f.setText(""); f.setForeground(TEXT_DARK); }
                f.setBorder(new CompoundBorder(new LineBorder(ACCENT,2,true), new EmptyBorder(0,12,0,12)));
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(TEXT_LIGHT); }
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR,1,true), new EmptyBorder(0,12,0,12)));
            }
        });
        return f;
    }

    private JButton accentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov=true; repaint(); }
                public void mouseExited (MouseEvent e) { hov=false;repaint(); }
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
        b.setBorder(new EmptyBorder(9,22,9,22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov=true; repaint(); }
                public void mouseExited (MouseEvent e) { hov=false;repaint(); }
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
        b.setFont(F_BOLD); b.setForeground(new Color(0x475569));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9,22,9,22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public String getTenNhom() {
        String t = txtTenNhom.getText().trim();
        return t.equals("Nhập tên nhóm...") ? "" : t;
    }
    public String getIdNhom() {
        String t = txtIdNhom.getText().trim();
        return t.startsWith("Để trống") ? "" : t;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            TaoNhomUI d = new TaoNhomUI(null);
            d.setVisible(true);
        });
    }
}

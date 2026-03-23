package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * ThamGiaNhomUI – Dialog Tham gia nhóm.
 * Theo sơ đồ: Nhập ID nhóm + Gửi yêu cầu
 */
public class ThamGiaNhomUI extends JDialog {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color WARNING     = new Color(0xF59E0B);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,  10);

    // ── Widgets công khai ─────────────────────────────────────
    public JTextField txtIdNhom;
    public JButton    btnGuiYeuCau;
    public JButton    btnHuy;

    public ThamGiaNhomUI(Frame owner) {
        super(owner, "Tham gia nhóm", true);
        setSize(420, 280);
        setMinimumSize(new Dimension(380, 260));
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
        JLabel t = new JLabel("Tham gia nhóm");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Nhập ID nhóm để gửi yêu cầu tham gia");
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

        JLabel lbl = new JLabel("NHẬP ID NHÓM");
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lbl);
        form.add(Box.createVerticalStrut(8));

        txtIdNhom = buildField("Nhập ID nhóm (vd: GR-12345)...");
        form.add(txtIdNhom);
        form.add(Box.createVerticalStrut(12));

        // Hint
        JPanel hintBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 251, 235));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(WARNING);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        hintBox.setOpaque(false);
        hintBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel hintIcon = new JLabel("ℹ");
        hintIcon.setForeground(WARNING);
        hintIcon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel hintText = new JLabel("Yêu cầu sẽ chờ trưởng nhóm phê duyệt.");
        hintText.setFont(F_SMALL); hintText.setForeground(new Color(0x92400E));
        hintBox.add(hintIcon); hintBox.add(hintText);
        form.add(hintBox);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setBackground(BG_WHITE);
        btnBar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnHuy       = buildOutlineBtn("Hủy");
        btnGuiYeuCau = buildAccentBtn("Gửi yêu cầu →");
        btnHuy.addActionListener(e -> dispose());
        btnBar.add(btnHuy);
        btnBar.add(btnGuiYeuCau);
        root.add(btnBar, BorderLayout.SOUTH);
    }

    private JTextField buildField(String ph) {
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
        b.setBorder(new EmptyBorder(9,22,9,22));
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
        b.setFont(F_BOLD); b.setForeground(new Color(0x475569));
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9,22,9,22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public String getIdNhom() {
        String t = txtIdNhom.getText().trim();
        return t.startsWith("Nhập ID") ? "" : t;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ThamGiaNhomUI(null).setVisible(true);
        });
    }
}

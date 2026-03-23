package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class KhoQuizUI extends JPanel {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);
    private static final Color TAB_SEL     = new Color(210, 225, 255);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);

    public JButton btnTaoQuiz;
    public JButton btnLamQuiz;
    public JTable  tblQuiz;
    public JTable  tblLichSu;

    public KhoQuizUI() {
        setLayout(new BorderLayout());
        setBackground(BG_WHITE);
        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildQuizList(), buildLichSu());
        split.setDividerSize(1);
        split.setDividerLocation(420);
        split.setBorder(null);
        split.setContinuousLayout(true);
        split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_WHITE);
        h.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(16, 24, 14, 24)));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Kho Quiz");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Danh sách quiz và lịch sử làm bài");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        left.add(t); left.add(Box.createVerticalStrut(2)); left.add(s);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        btnTaoQuiz = accentBtn("+ Tạo quiz");
        right.add(btnTaoQuiz);
        h.add(left, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    private JPanel buildQuizList() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_WHITE);
        p.setBorder(new MatteBorder(0, 0, 0, 1, BORDER_CLR));

        JLabel lbl = new JLabel("DANH SÁCH QUIZ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(12, 20, 8, 20));
        p.add(lbl, BorderLayout.NORTH);

        // Không có dữ liệu mẫu
        String[] cols = {"#", "Tên Quiz", "Số câu", "Thời gian"};
        tblQuiz = buildTable(cols, new Object[0][0]);

        JScrollPane scroll = new JScrollPane(tblQuiz);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLichSu() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);

        JLabel lbl = new JLabel("LỊCH SỬ LÀM BÀI");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(12, 20, 8, 20));
        p.add(lbl, BorderLayout.NORTH);

        // Không có dữ liệu mẫu
        String[] cols = {"Tên Quiz", "Điểm", "Thời gian", "Ngày làm"};
        tblLichSu = buildTable(cols, new Object[0][0]);
        tblLichSu.setBackground(BG_LIGHT);
        tblLichSu.getTableHeader().setBackground(BG_LIGHT);

        JScrollPane scroll = new JScrollPane(tblLichSu);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        bar.setBackground(BG_WHITE);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnLamQuiz = accentBtn("▶  Làm bài");
        JButton btnXemLai = outlineBtn("Xem lại");
        bar.add(btnXemLai);
        bar.add(btnLamQuiz);
        return bar;
    }

    private JTable buildTable(String[] cols, Object[][] data) {
        JTable t = new JTable(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        t.setFont(F_BODY);
        t.setRowHeight(42);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(TAB_SEL);
        t.setSelectionForeground(TEXT_DARK);
        t.getTableHeader().setFont(F_BOLD);
        t.getTableHeader().setForeground(TEXT_LIGHT);
        t.getTableHeader().setBackground(BG_WHITE);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        t.getTableHeader().setPreferredSize(new Dimension(0, 36));
        t.setBackground(BG_WHITE);
        t.setForeground(TEXT_DARK);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
                l.setBorder(new EmptyBorder(0, 16, 0, 16));
                l.setFont(F_BODY);
                if (!isSelected)
                    l.setBackground(row % 2 == 0 ? BG_WHITE : new Color(248, 250, 255));
                return l;
            }
        });
        return t;
    }

    /** Thêm một quiz vào bảng danh sách (gọi từ Controller) */
    public void themQuiz(String tenQuiz, String soCau, String thoiGian) {
        DefaultTableModel m = (DefaultTableModel) tblQuiz.getModel();
        m.addRow(new Object[]{m.getRowCount() + 1, tenQuiz, soCau, thoiGian});
    }

    /** Thêm một lịch sử vào bảng lịch sử (gọi từ Controller) */
    public void themLichSu(String tenQuiz, String diem, String thoiGian, String ngayLam) {
        DefaultTableModel m = (DefaultTableModel) tblLichSu.getModel();
        m.addRow(new Object[]{tenQuiz, diem, thoiGian, ngayLam});
    }

    // Helpers ──────────────────────────────────────────────────
    private JButton accentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new java.awt.GradientPaint(0,0,
                    hov ? new Color(0x4A7EE8) : ACCENT, getWidth(), getHeight(), ACCENT_DARK));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton outlineBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(235, 242, 255) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(ACCENT);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(ACCENT);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Cho phép dùng DefaultTableModel để add/remove row
    private JTable buildTable(String[] cols, Object[][] data, boolean dynamic) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setFont(F_BODY);
        t.setRowHeight(42);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(TAB_SEL);
        t.setSelectionForeground(TEXT_DARK);
        t.getTableHeader().setFont(F_BOLD);
        t.getTableHeader().setForeground(TEXT_LIGHT);
        t.getTableHeader().setBackground(BG_WHITE);
        t.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));
        t.getTableHeader().setPreferredSize(new Dimension(0, 36));
        t.setBackground(BG_WHITE);
        t.setForeground(TEXT_DARK);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
                l.setBorder(new EmptyBorder(0, 16, 0, 16));
                l.setFont(F_BODY);
                if (!isSelected)
                    l.setBackground(row % 2 == 0 ? BG_WHITE : new Color(248, 250, 255));
                return l;
            }
        });
        return t;
    }
}

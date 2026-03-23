package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.ArrayList;

public class TaoQuizUI extends JFrame {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  10);

    public JTextField       txtTenQuiz;
    public JTextArea        txtQuestion;
    public JTextField[]     txtOptions;
    @SuppressWarnings("unchecked")
    public JComboBox<String>[] cmbTrueFalse;
    public JTextField       txtTime;
    public JComboBox<String> cmbDonVi;
    public JButton          btnThemCauHoi;
    public JButton          btnKhoQuiz;
    public JButton          btnCapNhat;

    private DefaultListModel<String> listModel;
    public  JList<String>            lstCauHoi;

    @SuppressWarnings("unchecked")
    public TaoQuizUI() {
        setTitle("Tạo Quiz");
        setSize(820, 640);
        setMinimumSize(new Dimension(740, 560));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildForm(), buildCauHoiList());
        split.setDividerSize(1);
        split.setDividerLocation(480);
        split.setBorder(null);
        split.setContinuousLayout(true);
        root.add(split, BorderLayout.CENTER);

        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_WHITE);
        h.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(14, 24, 12, 24)));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Tạo Quiz mới");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Nhập thông tin và thêm câu hỏi cho quiz");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        left.add(t); left.add(Box.createVerticalStrut(2)); left.add(s);
        h.add(left, BorderLayout.WEST);
        return h;
    }

    @SuppressWarnings("unchecked")
    private JScrollPane buildForm() {
        JPanel form = new JPanel();
        form.setBackground(BG_WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Tên quiz – trống
        form.add(sectionLabel("TÊN QUIZ"));
        form.add(Box.createVerticalStrut(6));
        txtTenQuiz = field("Nhập tên quiz...");
        form.add(txtTenQuiz);
        form.add(Box.createVerticalStrut(18));

        // Question – trống
        form.add(sectionLabel("CÂU HỎI (QUESTION)"));
        form.add(Box.createVerticalStrut(6));
        txtQuestion = new JTextArea(3, 20);
        txtQuestion.setFont(F_BODY);
        txtQuestion.setForeground(TEXT_DARK);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(10, 12, 10, 12)));
        JScrollPane scrollQ = new JScrollPane(txtQuestion);
        scrollQ.setBorder(null);
        scrollQ.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollQ.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        form.add(scrollQ);
        form.add(Box.createVerticalStrut(18));

        // 4 đáp án – trống
        form.add(sectionLabel("ĐÁP ÁN (TRUE / FALSE)"));
        form.add(Box.createVerticalStrut(8));

        txtOptions   = new JTextField[4];
        cmbTrueFalse = new JComboBox[4];
        String[] letters = {"A", "B", "C", "D"};
        for (int i = 0; i < 4; i++) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel lbl = new JLabel(letters[i] + ".");
            lbl.setFont(F_BOLD); lbl.setForeground(ACCENT);
            lbl.setPreferredSize(new Dimension(24, 0));

            txtOptions[i] = field("Nhập đáp án " + letters[i] + "...");
            cmbTrueFalse[i] = new JComboBox<>(new String[]{"False", "True"});
            cmbTrueFalse[i].setFont(F_BODY);
            cmbTrueFalse[i].setPreferredSize(new Dimension(90, 40));
            cmbTrueFalse[i].setBorder(new LineBorder(BORDER_CLR, 1, true));

            row.add(lbl,             BorderLayout.WEST);
            row.add(txtOptions[i],   BorderLayout.CENTER);
            row.add(cmbTrueFalse[i], BorderLayout.EAST);
            form.add(row);
            form.add(Box.createVerticalStrut(8));
        }

        form.add(Box.createVerticalStrut(10));

        // Thời gian – mặc định rỗng
        form.add(sectionLabel("THIẾT LẬP THỜI GIAN LÀM BÀI"));
        form.add(Box.createVerticalStrut(8));
        JPanel timeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timeRow.setOpaque(false);
        timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel timeLbl = new JLabel("Time:");
        timeLbl.setFont(F_BOLD); timeLbl.setForeground(TEXT_MID);

        txtTime = new JTextField();
        txtTime.setFont(F_BODY);
        txtTime.setHorizontalAlignment(SwingConstants.CENTER);
        txtTime.setPreferredSize(new Dimension(70, 40));
        txtTime.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 8, 0, 8)));

        cmbDonVi = new JComboBox<>(new String[]{"Phút", "Giây"});
        cmbDonVi.setFont(F_BODY);
        cmbDonVi.setPreferredSize(new Dimension(90, 40));

        timeRow.add(timeLbl);
        timeRow.add(txtTime);
        timeRow.add(cmbDonVi);
        form.add(timeRow);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_WHITE);
        return scroll;
    }

    private JPanel buildCauHoiList() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new MatteBorder(0, 1, 0, 0, BORDER_CLR));

        JLabel lbl = new JLabel("DANH SÁCH CÂU HỎI ĐÃ THÊM");
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(14, 16, 8, 16));
        p.add(lbl, BorderLayout.NORTH);

        // Danh sách ban đầu trống
        listModel = new DefaultListModel<>();
        lstCauHoi = new JList<>(listModel);
        lstCauHoi.setFont(F_BODY);
        lstCauHoi.setBackground(BG_LIGHT);
        lstCauHoi.setForeground(TEXT_DARK);
        lstCauHoi.setFixedCellHeight(48);
        lstCauHoi.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean sel, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                    list, value, index, sel, focus);
                l.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                    new EmptyBorder(4, 16, 4, 16)));
                l.setFont(F_BODY);
                if (sel) { l.setBackground(new Color(210, 225, 255)); l.setForeground(TEXT_DARK); }
                else      { l.setBackground(BG_LIGHT); }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(lstCauHoi);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_LIGHT);
        p.add(scroll, BorderLayout.CENTER);

        JLabel hint = new JLabel("  Nhấn 'Thêm câu hỏi' để thêm vào danh sách");
        hint.setFont(F_SMALL); hint.setForeground(TEXT_LIGHT);
        hint.setBorder(new EmptyBorder(8, 0, 8, 0));
        p.add(hint, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        bar.setBackground(BG_WHITE);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnThemCauHoi = outlineBtn("+ Thêm câu hỏi");
        btnKhoQuiz    = outlineBtn("⊞  Kho Quiz");
        btnCapNhat    = accentBtn("✓  Cập nhật");
        bar.add(btnThemCauHoi);
        bar.add(btnKhoQuiz);
        bar.add(btnCapNhat);
        return bar;
    }

    /** Thêm câu hỏi vào danh sách bên phải */
    public void themCauHoiVaoList(String noiDung, String dapAnDung) {
        int idx = listModel.size() + 1;
        listModel.addElement("Câu " + idx + ": " + noiDung + "  [" + dapAnDung + "]");
        lstCauHoi.ensureIndexIsVisible(listModel.size() - 1);
    }

    // ── Helpers ───────────────────────────────────────────────
    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField field(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(F_BODY); f.setForeground(TEXT_LIGHT);
        f.setCaretColor(ACCENT); f.setText(placeholder);
        f.setPreferredSize(new Dimension(0, 40));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 12, 0, 12)));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) { f.setText(""); f.setForeground(TEXT_DARK); }
                f.setBorder(new CompoundBorder(new LineBorder(ACCENT, 2, true), new EmptyBorder(0, 12, 0, 12)));
            }
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) { f.setText(placeholder); f.setForeground(TEXT_LIGHT); }
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0, 12, 0, 12)));
            }
        });
        return f;
    }

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
        b.setBorder(new EmptyBorder(9, 22, 9, 22));
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
                g2.setColor(hov ? new Color(235, 242, 255) : BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(ACCENT);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(ACCENT);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 22, 9, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}

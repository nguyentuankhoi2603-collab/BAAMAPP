package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class LamQuizUI extends JFrame {

    private static final Color ACCENT         = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK    = new Color(0x3D6FD4);
    private static final Color BORDER_CLR     = new Color(220, 226, 240);
    private static final Color TEXT_DARK      = new Color(0x1E293B);
    private static final Color TEXT_MID       = new Color(0x475569);
    private static final Color TEXT_LIGHT     = new Color(0x94A3B8);
    private static final Color BG_WHITE       = Color.WHITE;
    private static final Color BG_LIGHT       = new Color(245, 247, 252);
    private static final Color OPT_HOVER      = new Color(235, 242, 255);
    private static final Color OPT_SELECT     = new Color(91, 141, 239, 40);
    private static final Color OPT_BORDER_SEL = new Color(0x5B8DEF);

    private static final Font F_TITLE    = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_QUESTION = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font F_OPTION   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font F_BOLD     = new Font("Segoe UI", Font.BOLD,  13);
    private static final Font F_TIMER    = new Font("Segoe UI", Font.BOLD,  20);

    public JLabel    lblCauHoi;
    public JLabel    lblNoiDung;
    public JLabel    lblTimer;
    public JButton[] btnOptions;
    public JButton   btnCauTruoc;
    public JButton   btnNopBai;
    public JButton   btnCauSau;
    public JPanel    pnlProgress;

    private int selectedOption = -1;
    private String tenQuiz     = "";

    public LamQuizUI() {
        setTitle("Làm Quiz");
        setSize(720, 580);
        setMinimumSize(new Dimension(640, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);
        setContentPane(root);

        root.add(buildTopBar(),    BorderLayout.NORTH);
        root.add(buildCenter(),    BorderLayout.CENTER);
        root.add(buildBottomBar(), BorderLayout.SOUTH);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_WHITE);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(14, 24, 14, 24)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        // Tên quiz – sẽ được set từ ngoài
        JLabel quizName = new JLabel(" ");
        quizName.setName("quizName");
        quizName.setFont(F_TITLE); quizName.setForeground(TEXT_DARK);

        lblCauHoi = new JLabel("Câu — / —");
        lblCauHoi.setFont(F_BOLD); lblCauHoi.setForeground(ACCENT);

        left.add(quizName); left.add(Box.createVerticalStrut(2)); left.add(lblCauHoi);

        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        timerPanel.setOpaque(false);
        JLabel timerIcon = new JLabel("◷");
        timerIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        timerIcon.setForeground(TEXT_LIGHT);
        lblTimer = new JLabel("--:--");
        lblTimer.setFont(F_TIMER); lblTimer.setForeground(ACCENT_DARK);
        timerPanel.add(timerIcon);
        timerPanel.add(lblTimer);

        bar.add(left,       BorderLayout.WEST);
        bar.add(timerPanel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenter() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_LIGHT);
        outer.setBorder(new EmptyBorder(24, 40, 16, 40));

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setOpaque(false);

        // Question box
        JPanel qWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        qWrap.setOpaque(false);
        qWrap.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Nội dung câu hỏi – trống ban đầu
        lblNoiDung = new JLabel("<html><body style='width:500px;font-size:14px'>&nbsp;</body></html>");
        lblNoiDung.setFont(F_QUESTION);
        lblNoiDung.setForeground(TEXT_DARK);
        qWrap.add(lblNoiDung, BorderLayout.CENTER);

        // Progress dots – trống ban đầu
        pnlProgress = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlProgress.setOpaque(false);

        JPanel qCard = new JPanel(new BorderLayout(0, 12));
        qCard.setOpaque(false);
        qCard.add(qWrap,       BorderLayout.CENTER);
        qCard.add(pnlProgress, BorderLayout.SOUTH);
        card.add(qCard, BorderLayout.NORTH);

        // 4 Options – nhãn trống ban đầu
        JPanel opts = new JPanel(new GridLayout(2, 2, 14, 14));
        opts.setOpaque(false);
        btnOptions = new JButton[4];
        String[] labels = {"A.", "B.", "C.", "D."};
        for (int i = 0; i < 4; i++) {
            btnOptions[i] = buildOptionBtn(labels[i], i);
        }
        for (JButton b : btnOptions) opts.add(b);
        card.add(opts, BorderLayout.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = gc.weighty = 1;
        outer.add(card, gc);
        return outer;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_WHITE);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_CLR),
            new EmptyBorder(14, 32, 14, 32)));

        JPanel leftPart = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPart.setOpaque(false);
        JLabel tgLabel = new JLabel("THỜI GIAN LÀM BÀI");
        tgLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        tgLabel.setForeground(TEXT_LIGHT);
        JLabel tgValue = new JLabel("  --:--");
        tgValue.setFont(F_BOLD); tgValue.setForeground(TEXT_DARK);
        leftPart.add(tgLabel); leftPart.add(tgValue);

        JPanel navPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPart.setOpaque(false);
        btnCauTruoc = outlineBtn("◀  Câu trước");
        btnNopBai   = buildNopBaiBtn();
        btnCauSau   = accentBtn("Câu sau  ▶");
        navPart.add(btnCauTruoc);
        navPart.add(btnNopBai);
        navPart.add(btnCauSau);

        bar.add(leftPart, BorderLayout.WEST);
        bar.add(navPart,  BorderLayout.EAST);
        return bar;
    }

    // ── Option button ─────────────────────────────────────────
    private JButton buildOptionBtn(String text, int index) {
        JButton b = new JButton(text) {
            boolean hov; boolean sel;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                sel = (selectedOption == index);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg     = sel ? OPT_SELECT : (hov ? OPT_HOVER : BG_WHITE);
                Color border = sel ? OPT_BORDER_SEL : BORDER_CLR;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(border);
                g2.setStroke(sel ? new BasicStroke(2f) : new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_OPTION); b.setForeground(TEXT_DARK);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(14, 20, 14, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            selectedOption = index;
            for (JButton ob : btnOptions) ob.repaint();
        });
        return b;
    }

    private JButton buildNopBaiBtn() {
        JButton b = new JButton("  Nộp bài  ") {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? new Color(0xDC2626) : new Color(0xEF4444));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 22, 9, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Cập nhật hiển thị câu hỏi (gọi từ Controller) */
    public void hienThiCauHoi(int soCau, int tongCau, String noiDung, String[] options) {
        lblCauHoi.setText("Câu " + soCau + " / " + tongCau);
        lblNoiDung.setText("<html><body style='width:500px'>" + noiDung + "</body></html>");
        char[] abc = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < 4; i++) {
            btnOptions[i].setText(abc[i] + ".  " + (i < options.length ? options[i] : ""));
        }
        selectedOption = -1;
        for (JButton b : btnOptions) b.repaint();
    }

    /** Cập nhật tên quiz ở top bar */
    public void setTenQuiz(String ten) {
        this.tenQuiz = ten;
        setTitle("Làm Quiz – " + ten);
        // Tìm label quizName và cập nhật
        for (Component c : ((JPanel)((JPanel)getContentPane().getComponent(0))
                .getComponent(0)).getComponents()) {
            if (c instanceof JPanel) {
                for (Component cc : ((JPanel)c).getComponents()) {
                    if (cc instanceof JLabel && "quizName".equals(cc.getName())) {
                        ((JLabel)cc).setText(ten);
                    }
                }
            }
        }
    }

    public int getSelectedOption() { return selectedOption; }

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
                g2.setColor(BORDER_CLR);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BOLD); b.setForeground(TEXT_MID);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 22, 9, 22));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}

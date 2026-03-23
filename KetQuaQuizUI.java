package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class KetQuaQuizUI extends JFrame {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);
    private static final Color CORRECT_BG  = new Color(220, 252, 231);
    private static final Color CORRECT_BD  = new Color(0x22C55E);
    private static final Color WRONG_BG    = new Color(254, 226, 226);
    private static final Color WRONG_BD    = new Color(0xEF4444);

    private static final Font F_TITLE    = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_QUESTION = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font F_OPTION   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD     = new Font("Segoe UI", Font.BOLD,  13);

    public JLabel  lblCauHoi;
    public JLabel  lblNoiDung;
    public JLabel  lblDapAnDung;
    public JLabel  lblDapAnChon;
    public JLabel  lblDiem;
    public JPanel  pnlOptions;
    public JButton btnCauTruoc;
    public JButton btnCauSau;

    // Trạng thái ban đầu: chưa có câu hỏi / đáp án
    private int correctIdx  = -1;
    private int selectedIdx = -1;

    public KetQuaQuizUI() {
        setTitle("Kết quả Quiz");
        setSize(700, 580);
        setMinimumSize(new Dimension(620, 500));
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
        JLabel title = new JLabel("Kết quả Quiz");
        title.setFont(F_TITLE); title.setForeground(TEXT_DARK);
        lblCauHoi = new JLabel("Câu — / —");
        lblCauHoi.setFont(F_BOLD); lblCauHoi.setForeground(ACCENT);
        left.add(title); left.add(Box.createVerticalStrut(2)); left.add(lblCauHoi);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        lblDiem = new JLabel("Điểm: — / —");
        lblDiem.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblDiem.setForeground(ACCENT_DARK);
        right.add(lblDiem);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenter() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(BG_LIGHT);
        outer.setBorder(new EmptyBorder(24, 40, 16, 40));

        JPanel card = new JPanel(new BorderLayout(0, 20));
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
        lblNoiDung = new JLabel("<html><body style='width:480px;font-size:14px'>&nbsp;</body></html>");
        lblNoiDung.setFont(F_QUESTION);
        lblNoiDung.setForeground(TEXT_DARK);
        qWrap.add(lblNoiDung, BorderLayout.CENTER);
        card.add(qWrap, BorderLayout.NORTH);

        // Options panel – trống ban đầu (4 ô chờ)
        pnlOptions = new JPanel(new GridLayout(2, 2, 14, 14));
        pnlOptions.setOpaque(false);
        String[] opts = {"A.", "B.", "C.", "D."};
        for (int i = 0; i < 4; i++) {
            pnlOptions.add(buildResultOption(opts[i], i, correctIdx, selectedIdx));
        }
        card.add(pnlOptions, BorderLayout.CENTER);

        // Đáp án đúng / chọn – trống ban đầu
        JPanel dapAnWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CORRECT_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(CORRECT_BD);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dapAnWrap.setOpaque(false);
        dapAnWrap.setBorder(new EmptyBorder(12, 20, 12, 20));

        JPanel dapAnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        dapAnRow.setOpaque(false);

        lblDapAnDung = new JLabel("✓  Đáp án đúng:  —");
        lblDapAnDung.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDapAnDung.setForeground(new Color(0x166534));

        lblDapAnChon = new JLabel("✗  Bạn chọn:  —");
        lblDapAnChon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDapAnChon.setForeground(new Color(0x991B1B));

        dapAnRow.add(lblDapAnDung);
        dapAnRow.add(lblDapAnChon);
        dapAnWrap.add(dapAnRow, BorderLayout.CENTER);
        card.add(dapAnWrap, BorderLayout.SOUTH);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = gc.weighty = 1;
        outer.add(card, gc);
        return outer;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 14));
        bar.setBackground(BG_WHITE);
        bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
        btnCauTruoc = outlineBtn("◀  Câu trước");
        btnCauSau   = accentBtn("Câu sau  ▶");
        bar.add(btnCauTruoc);
        bar.add(btnCauSau);
        return bar;
    }

    /**
     * Cập nhật toàn bộ màn hình kết quả (gọi từ Controller).
     * @param soCau      số thứ tự câu hiện tại (bắt đầu từ 1)
     * @param tongCau    tổng số câu
     * @param noiDung    nội dung câu hỏi
     * @param options    mảng 4 đáp án
     * @param correctIdx index đáp án đúng (0–3)
     * @param selectedIdx index đáp án người dùng chọn (0–3, hoặc -1 nếu bỏ)
     * @param diem       điểm đúng tích lũy
     */
    public void hienThiKetQua(int soCau, int tongCau, String noiDung,
                              String[] options, int correctIdx, int selectedIdx,
                              String diem) {
        this.correctIdx  = correctIdx;
        this.selectedIdx = selectedIdx;

        lblCauHoi.setText("Câu " + soCau + " / " + tongCau);
        lblDiem.setText("Điểm: " + diem);
        lblNoiDung.setText("<html><body style='width:480px;font-size:14px'>"
            + noiDung + "</body></html>");

        char[] abc = {'A', 'B', 'C', 'D'};
        String dapAnDungTen  = correctIdx  >= 0 ? String.valueOf(abc[correctIdx])  : "—";
        String dapAnChonTen  = selectedIdx >= 0 ? String.valueOf(abc[selectedIdx]) : "—";

        lblDapAnDung.setText("✓  Đáp án đúng:  " + dapAnDungTen);
        lblDapAnChon.setText((selectedIdx == correctIdx ? "✓" : "✗") + "  Bạn chọn:  " + dapAnChonTen);
        lblDapAnChon.setForeground(selectedIdx == correctIdx
            ? new Color(0x166534) : new Color(0x991B1B));

        // Rebuild options panel
        pnlOptions.removeAll();
        for (int i = 0; i < 4; i++) {
            String label = abc[i] + ".  " + (i < options.length ? options[i] : "");
            pnlOptions.add(buildResultOption(label, i, correctIdx, selectedIdx));
        }
        pnlOptions.revalidate();
        pnlOptions.repaint();
    }

    // ── Option panel (readonly, highlight đúng/sai) ───────────
    private JPanel buildResultOption(String text, int idx, int correct, int selected) {
        boolean isCorrect  = (idx == correct);
        boolean isSelected = (idx == selected);
        boolean isWrong    = isSelected && !isCorrect;

        Color bg = isCorrect ? CORRECT_BG : (isWrong ? WRONG_BG : BG_WHITE);
        Color bd = isCorrect ? CORRECT_BD  : (isWrong ? WRONG_BD  : BORDER_CLR);
        Color fg = isCorrect ? new Color(0x166534) : (isWrong ? new Color(0x991B1B) : TEXT_DARK);
        float bw = (isCorrect || isWrong) ? 2f : 1f;

        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(bd);
                g2.setStroke(new BasicStroke(bw));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel lbl = new JLabel(text);
        lbl.setFont(F_OPTION); lbl.setForeground(fg);
        p.add(lbl, BorderLayout.WEST);

        if (isCorrect) {
            JLabel badge = new JLabel("✓");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 16));
            badge.setForeground(CORRECT_BD);
            p.add(badge, BorderLayout.EAST);
        } else if (isWrong) {
            JLabel badge = new JLabel("✗");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 16));
            badge.setForeground(WRONG_BD);
            p.add(badge, BorderLayout.EAST);
        }
        return p;
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
        b.setBorder(new EmptyBorder(10, 28, 10, 28));
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
        b.setBorder(new EmptyBorder(10, 28, 10, 28));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}

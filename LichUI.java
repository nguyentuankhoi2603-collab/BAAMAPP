package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.*;

/**
 * LichUI – Màn hình Lịch.
 * Theo sơ đồ: Lịch cá nhân + Đối tượng lịch từng nhóm + Đối tượng lịch của bạn
 * Layout: Lịch tháng (trái) + Danh sách sự kiện (phải)
 */
public class LichUI extends JPanel {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);
    private static final Color TODAY_BG    = ACCENT;
    private static final Color CAL_HOV     = new Color(235, 242, 255);
    private static final Color CAL_SEL     = new Color(210, 225, 255);

    private static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD    = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL   = new Font("Segoe UI", Font.BOLD,  10);
    private static final Font F_CAL_DAY = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font F_CAL_HDR = new Font("Segoe UI", Font.BOLD,  11);

    // ── Widgets công khai ─────────────────────────────────────
    public JButton btnThemSuKien;
    public JButton btnThangTruoc;
    public JButton btnThangSau;
    public JLabel  lblThangNam;

    private int selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    private int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int currentYear  = Calendar.getInstance().get(Calendar.YEAR);
    private JPanel calGrid;

    public LichUI() {
        setLayout(new BorderLayout());
        setBackground(BG_WHITE);
        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildCalPanel(), buildEventPanel());
        split.setDividerSize(1);
        split.setDividerLocation(340);
        split.setBorder(null);
        split.setContinuousLayout(true);
        add(split, BorderLayout.CENTER);
    }

    // ── Header ────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(BG_WHITE);
        h.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0,BORDER_CLR),
            new EmptyBorder(16,24,14,24)
        ));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JLabel t = new JLabel("Lịch của bạn");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("Sự kiện cá nhân & từ các nhóm");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        left.add(t); left.add(Box.createVerticalStrut(2)); left.add(s);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.setOpaque(false);
        btnThemSuKien = accentBtn("+ Thêm sự kiện");
        right.add(btnThemSuKien);

        h.add(left, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── Lịch tháng (trái) ─────────────────────────────────────
    private JPanel buildCalPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_WHITE);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0,0,0,1,BORDER_CLR),
            new EmptyBorder(16,16,16,16)
        ));

        // Nav tháng
        JPanel nav = new JPanel(new BorderLayout());
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(0,0,12,0));

        btnThangTruoc = navBtn("◀");
        btnThangSau   = navBtn("▶");
        lblThangNam   = new JLabel(thangNamStr(), SwingConstants.CENTER);
        lblThangNam.setFont(F_BOLD); lblThangNam.setForeground(TEXT_DARK);

        btnThangTruoc.addActionListener(e -> { currentMonth--; if(currentMonth<0){currentMonth=11;currentYear--;} refreshCal(); });
        btnThangSau.addActionListener  (e -> { currentMonth++; if(currentMonth>11){currentMonth=0;currentYear++;} refreshCal(); });

        nav.add(btnThangTruoc, BorderLayout.WEST);
        nav.add(lblThangNam,   BorderLayout.CENTER);
        nav.add(btnThangSau,   BorderLayout.EAST);
        p.add(nav, BorderLayout.NORTH);

        // Tên ngày trong tuần
        JPanel weekRow = new JPanel(new GridLayout(1,7,4,0));
        weekRow.setOpaque(false);
        String[] days = {"T2","T3","T4","T5","T6","T7","CN"};
        for (String d : days) {
            JLabel l = new JLabel(d, SwingConstants.CENTER);
            l.setFont(F_CAL_HDR); l.setForeground(TEXT_LIGHT);
            weekRow.add(l);
        }
        weekRow.setBorder(new EmptyBorder(0,0,8,0));
        p.add(weekRow, BorderLayout.CENTER);

        // Grid ngày
        calGrid = new JPanel(new GridLayout(6,7,4,4));
        calGrid.setOpaque(false);
        fillCalGrid();
        p.add(calGrid, BorderLayout.SOUTH);
        return p;
    }

    private void fillCalGrid() {
        calGrid.removeAll();
        Calendar cal = Calendar.getInstance();
        cal.set(currentYear, currentMonth, 1);
        int firstDow = cal.get(Calendar.DAY_OF_WEEK); // 1=Sun
        int startOffset = (firstDow == 1) ? 6 : firstDow - 2; // Mon=0
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int today = -1;
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.MONTH) == currentMonth && now.get(Calendar.YEAR) == currentYear)
            today = now.get(Calendar.DAY_OF_MONTH);

        int cell = 0;
        for (int i = 0; i < startOffset; i++) { calGrid.add(new JLabel("")); cell++; }
        for (int d = 1; d <= daysInMonth; d++) {
            final int day = d;
            boolean isToday = (d == today);
            boolean isSel   = (d == selectedDay);
            JPanel dayCell = new JPanel(new BorderLayout()) {
                boolean hov;
                { addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e){hov=true;repaint();}
                    public void mouseExited(MouseEvent e){hov=false;repaint();}
                    public void mouseClicked(MouseEvent e){selectedDay=day;refreshCal();}
                }); }
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isToday)    g2.setColor(TODAY_BG);
                    else if (isSel) g2.setColor(CAL_SEL);
                    else if (hov)   g2.setColor(CAL_HOV);
                    else            g2.setColor(BG_WHITE);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                    g2.dispose(); super.paintComponent(g);
                }
            };
            dayCell.setOpaque(false);
            dayCell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel lbl = new JLabel(String.valueOf(d), SwingConstants.CENTER);
            lbl.setFont(F_CAL_DAY);
            lbl.setForeground(isToday ? Color.WHITE : TEXT_DARK);
            dayCell.add(lbl, BorderLayout.CENTER);
            // Dot sự kiện (mẫu)
            if (d == 15 || d == 20 || d == 25) {
                JPanel dot = new JPanel() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2=(Graphics2D)g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(isToday?Color.WHITE:ACCENT);
                        g2.fillOval(getWidth()/2-3,0,6,6);
                        g2.dispose();
                    }
                };
                dot.setOpaque(false);
                dot.setPreferredSize(new Dimension(0,8));
                dayCell.add(dot, BorderLayout.SOUTH);
            }
            calGrid.add(dayCell); cell++;
        }
        while (cell < 42) { calGrid.add(new JLabel("")); cell++; }
        calGrid.revalidate(); calGrid.repaint();
    }

    private void refreshCal() {
        lblThangNam.setText(thangNamStr());
        fillCalGrid();
    }

    private String thangNamStr() {
        return "Tháng " + (currentMonth+1) + " / " + currentYear;
    }

    // ── Danh sách sự kiện (phải) ──────────────────────────────
    private JPanel buildEventPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);

        JLabel lbl = new JLabel("SỰ KIỆN SẮP TỚI");
        lbl.setFont(F_LABEL); lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(14,20,8,20));
        p.add(lbl, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(0,16,16,16));

        // Nhóm: hôm nay
        list.add(secLbl("HÔM NAY"));
        list.add(Box.createVerticalStrut(8));
        list.add(eventCard("Quiz Toán học",     "20:00 – 21:00", "Nhóm 1", new Color(0x5B8DEF), true));
        list.add(Box.createVerticalStrut(8));

        list.add(Box.createVerticalStrut(8));
        list.add(secLbl("NHÓM 1 – SẮP TỚI"));
        list.add(Box.createVerticalStrut(8));
        list.add(eventCard("Họp nhóm dự án",    "Thứ 7, 09:00",  "Nhóm 1", new Color(0x10B981), false));
        list.add(Box.createVerticalStrut(8));

        list.add(Box.createVerticalStrut(8));
        list.add(secLbl("CỦA BẠN – SẮP TỚI"));
        list.add(Box.createVerticalStrut(8));
        list.add(eventCard("Ôn thi cuối kì",    "CN, 14:00",     "Cá nhân", new Color(0xF59E0B), false));
        list.add(Box.createVerticalStrut(8));
        list.add(eventCard("Nộp bài tập lớn",   "T2, 23:59",     "Cá nhân", new Color(0xEF4444), false));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JLabel secLbl(String t) {
        JLabel l = new JLabel(t); l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel eventCard(String title, String time, String nhom, Color color, boolean isNow) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(BORDER_CLR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12,14,12,14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel colorBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color); g2.fillRoundRect(0,0,getWidth(),getHeight(),4,4);
                g2.dispose();
            }
        };
        colorBar.setOpaque(false);
        colorBar.setPreferredSize(new Dimension(4,0));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lTitle = new JLabel(title); lTitle.setFont(F_BOLD); lTitle.setForeground(TEXT_DARK);
        JLabel lTime  = new JLabel(time + "  ·  " + nhom); lTime.setFont(F_SMALL); lTime.setForeground(TEXT_LIGHT);
        info.add(lTitle); info.add(Box.createVerticalStrut(3)); info.add(lTime);

        card.add(colorBar, BorderLayout.WEST);
        card.add(info,     BorderLayout.CENTER);
        if (isNow) {
            JLabel badge = new JLabel("Ngay bây giờ");
            badge.setFont(new Font("Segoe UI",Font.BOLD,10));
            badge.setForeground(color);
            badge.setBorder(new EmptyBorder(2,8,2,8));
            card.add(badge, BorderLayout.EAST);
        }
        return card;
    }

    private JButton navBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e){hov=true;repaint();}
                public void mouseExited(MouseEvent e){hov=false;repaint();}
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov?CAL_HOV:BG_WHITE);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(F_BODY); b.setForeground(TEXT_MID);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(4,10,4,10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton accentBtn(String text) {
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
        b.setBorder(new EmptyBorder(8,18,8,18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static final Color TEXT_MID_1 = new Color(0x475569);
}

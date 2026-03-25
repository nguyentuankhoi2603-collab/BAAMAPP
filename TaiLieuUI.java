package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * TaiLieuUI – Màn hình Tài liệu.
 * Theo sơ đồ: Ảnh/Video | File | Link — 3 tab hiển thị tài liệu của nhóm/cá nhân
 */
public class TaiLieuUI extends JPanel {

    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;
    private static final Color BG_LIGHT    = new Color(245, 247, 252);
    private static final Color TAB_ACTIVE  = new Color(235, 242, 255);

    private static final Font F_TITLE  = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL  = new Font("Segoe UI", Font.BOLD,  10);

    // ── Widgets công khai ─────────────────────────────────────
    public JButton btnTaiLen;
    public JButton btnTabAnh;
    public JButton btnTabFile;
    public JButton btnTabLink;

    private CardLayout cardLayout;
    private JPanel     cardPanel;

    public TaiLieuUI() {
        setLayout(new BorderLayout());
        setBackground(BG_WHITE);
        add(buildHeader(),  BorderLayout.NORTH);
        add(buildTabBar(),  BorderLayout.CENTER);
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
        JLabel t = new JLabel("Tài liệu");
        t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
        JLabel s = new JLabel("File, ảnh và link từ nhóm của bạn");
        s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
        left.add(t); left.add(Box.createVerticalStrut(2)); left.add(s);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        right.setOpaque(false);
        btnTaiLen = accentBtn("+ Tải lên");
        right.add(btnTaiLen);

        h.add(left,  BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ── Tab bar + content ─────────────────────────────────────
    private JPanel buildTabBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);

        // Tab buttons
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabRow.setBackground(BG_WHITE);
        tabRow.setBorder(new CompoundBorder(
            new MatteBorder(0,0,1,0,BORDER_CLR),
            new EmptyBorder(0,16,0,16)
        ));

        btnTabAnh  = buildTabBtn("🖼  Ảnh / Video", true);
        btnTabFile = buildTabBtn("📄  File",        false);
        btnTabLink = buildTabBtn("🔗  Link",         false);

        tabRow.add(btnTabAnh);
        tabRow.add(btnTabFile);
        tabRow.add(btnTabLink);
        outer.add(tabRow, BorderLayout.NORTH);

        // Card content
        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_LIGHT);
        cardPanel.add(buildAnhPanel(),  "ANH");
        cardPanel.add(buildFilePanel(), "FILE");
        cardPanel.add(buildLinkPanel(), "LINK");
        outer.add(cardPanel, BorderLayout.CENTER);

        // Wire tab events
        btnTabAnh.addActionListener(e  -> { setActiveTab(btnTabAnh,btnTabFile,btnTabLink); cardLayout.show(cardPanel,"ANH"); });
        btnTabFile.addActionListener(e -> { setActiveTab(btnTabFile,btnTabAnh,btnTabLink); cardLayout.show(cardPanel,"FILE"); });
        btnTabLink.addActionListener(e -> { setActiveTab(btnTabLink,btnTabAnh,btnTabFile); cardLayout.show(cardPanel,"LINK"); });

        return outer;
    }

    // ── Tab: Ảnh / Video ─────────────────────────────────────
    private JPanel buildAnhPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new EmptyBorder(16,20,16,20));

        JPanel grid = new JPanel(new GridLayout(0, 4, 12, 12));
        grid.setOpaque(false);


        JScrollPane scroll = new JScrollPane(grid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }
    private JPanel buildMediaCard(String name) {
        JPanel card = new JPanel(new BorderLayout(0,8)) {
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
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12,12,12,12));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Preview area
        JPanel preview = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230,236,250));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28));
                FontMetrics fm=g2.getFontMetrics();
                String icon = name.startsWith("🎬") ? "🎬" : "🖼";
                g2.drawString(icon, getWidth()/2-fm.stringWidth(icon)/2, getHeight()/2+fm.getAscent()/2-6);
                g2.dispose();
            }
        };
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(0,80));

        String shortName = name.substring(2);
        JLabel lbl = new JLabel("<html><body style='width:80px;font-size:10px'>" + shortName + "</body></html>");
        lbl.setFont(F_SMALL); lbl.setForeground(TEXT_DARK);

        card.add(preview, BorderLayout.CENTER);
        card.add(lbl,     BorderLayout.SOUTH);
        return card;
    }

    // ── Tab: File ─────────────────────────────────────────────
    private JPanel buildFilePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new EmptyBorder(16,20,16,20));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));


        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFileRow(String icon, String name, String type, String size, String date, String uploader) {
        JPanel card = new JPanel(new BorderLayout(14,0)) {
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
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12,16,12,16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lIcon = new JLabel(icon);
        lIcon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,24));
        lIcon.setPreferredSize(new Dimension(32,0));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lName = new JLabel(name); lName.setFont(F_BOLD); lName.setForeground(TEXT_DARK);
        JLabel lMeta = new JLabel(type + " · " + size + " · " + uploader);
        lMeta.setFont(F_SMALL); lMeta.setForeground(TEXT_LIGHT);
        info.add(lName); info.add(Box.createVerticalStrut(3)); info.add(lMeta);

        JLabel lDate = new JLabel(date);
        lDate.setFont(F_SMALL); lDate.setForeground(TEXT_LIGHT);
        lDate.setVerticalAlignment(SwingConstants.CENTER);

        card.add(lIcon, BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);
        card.add(lDate, BorderLayout.EAST);
        return card;
    }

    // ── Tab: Link ─────────────────────────────────────────────
    private JPanel buildLinkPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG_LIGHT);
        p.setBorder(new EmptyBorder(16,20,16,20));

        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));


        JScrollPane scroll = new JScrollPane(list);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);

        p.add(scroll, BorderLayout.CENTER);
        return p;
    }
    private JPanel buildLinkRow(String title, String url, String uploader, String date) {
        JPanel card = new JPanel(new BorderLayout(12,0)) {
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
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14,16,14,16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,72));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Icon link
        JLabel lIcon = new JLabel("🔗");
        lIcon.setFont(new Font("Segoe UI Emoji",Font.PLAIN,22));
        lIcon.setPreferredSize(new Dimension(32,0));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lTitle = new JLabel(title); lTitle.setFont(F_BOLD); lTitle.setForeground(TEXT_DARK);
        JLabel lUrl   = new JLabel(url);
        lUrl.setFont(F_SMALL); lUrl.setForeground(ACCENT);
        JLabel lMeta  = new JLabel(uploader + " · " + date);
        lMeta.setFont(F_SMALL); lMeta.setForeground(TEXT_LIGHT);
        info.add(lTitle); info.add(Box.createVerticalStrut(2));
        info.add(lUrl);   info.add(Box.createVerticalStrut(2));
        info.add(lMeta);

        card.add(lIcon, BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);
        return card;
    }

    // ── Helpers ───────────────────────────────────────────────
    private JButton buildTabBtn(String text, boolean active) {
        JButton b = new JButton(text) {
            boolean isActive = active;
            @Override public void setBackground(Color c) { /* skip */}
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isActive) {
                    g.setColor(ACCENT);
                    g.fillRect(0, getHeight()-2, getWidth(), 2);
                }
            }
        };
        b.setFont(active ? F_BOLD : F_BODY);
        b.setForeground(active ? ACCENT : TEXT_MID);
        b.setBackground(BG_WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10,16,10,16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setActiveTab(JButton active, JButton... others) {
        active.setFont(F_BOLD); active.setForeground(ACCENT);
        for (JButton o : others) { o.setFont(F_BODY); o.setForeground(TEXT_MID); }
        repaint();
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
}

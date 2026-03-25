package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.*;

/**
 * TaiLieuUI – View màn hình Tài liệu.
 * Hiển thị 3 tab: Ảnh/Video | File | Link
 * Mỗi item có nút Download và Xóa.
 * Controller gọi các phương thức public để thêm/xóa item và điều hướng.
 */
public class TaiLieuUI extends JPanel {

    // ── Palette ───────────────────────────────────────────────
    static final Color ACCENT       = new Color(0x5B8DEF);
    static final Color ACCENT_DARK  = new Color(0x3D6FD4);
    static final Color ACCENT_LIGHT = new Color(0xEBF2FF);
    static final Color BORDER_CLR   = new Color(220, 226, 240);
    static final Color TEXT_DARK    = new Color(0x1E293B);
    static final Color TEXT_MID     = new Color(0x475569);
    static final Color TEXT_LIGHT   = new Color(0x94A3B8);
    static final Color BG_WHITE     = Color.WHITE;
    static final Color BG_LIGHT     = new Color(246, 248, 253);
    static final Color RED_SOFT     = new Color(0xFFEEEE);
    static final Color RED_MID      = new Color(0xFF6B6B);
    static final Color GREEN_SOFT   = new Color(0xE8F8F0);
    static final Color GREEN_MID    = new Color(0x34C77B);

    // ── Fonts ─────────────────────────────────────────────────
    static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  15);
    static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  10);
    static final Font F_MICRO = new Font("Segoe UI", Font.PLAIN, 10);

    // ── Widgets công khai ─────────────────────────────────────
    public JButton btnTaiLen;
    public JButton btnTabAnh;
    public JButton btnTabFile;
    public JButton btnTabLink;

    // ── Panels nội dung ───────────────────────────────────────
    private JPanel pnlAnhGrid;
    private JPanel pnlFileList;
    private JPanel pnlLinkList;
    private JLabel lblAnhEmpty;
    private JLabel lblFileEmpty;
    private JLabel lblLinkEmpty;

    private CardLayout cardLayout;
    private JPanel     cardPanel;

    // ── Đếm item (để ẩn/hiện empty state) ────────────────────
    private int cntAnh  = 0;
    private int cntFile = 0;
    private int cntLink = 0;

    // ─────────────────────────────────────────────────────────
    public TaiLieuUI() {
        setLayout(new BorderLayout());
        setBackground(BG_WHITE);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabBar(), BorderLayout.CENTER);
    }

    // ══════════════════════════════════════════════════════════
    // HEADER
    // ══════════════════════════════════════════════════════════
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
        JLabel lTitle = new JLabel("Tài liệu");
        lTitle.setFont(F_TITLE); lTitle.setForeground(TEXT_DARK);
        JLabel lSub = new JLabel("Ảnh, file và link chia sẻ trong nhóm");
        lSub.setFont(F_SMALL);   lSub.setForeground(TEXT_LIGHT);
        left.add(lTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(lSub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        btnTaiLen = accentBtn("+ Tải lên");
        right.add(btnTaiLen);

        h.add(left,  BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    // ══════════════════════════════════════════════════════════
    // TAB BAR + CARDS
    // ══════════════════════════════════════════════════════════
    private JPanel buildTabBar() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_WHITE);

        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabRow.setBackground(BG_WHITE);
        tabRow.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            new EmptyBorder(0, 16, 0, 16)
        ));
        btnTabAnh  = buildTabBtn("🖼  Ảnh / Video", true);
        btnTabFile = buildTabBtn("📄  File",        false);
        btnTabLink = buildTabBtn("🔗  Link",         false);
        tabRow.add(btnTabAnh);
        tabRow.add(btnTabFile);
        tabRow.add(btnTabLink);
        outer.add(tabRow, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout);
        cardPanel.setBackground(BG_LIGHT);
        cardPanel.add(buildAnhPanel(),  "ANH");
        cardPanel.add(buildFilePanel(), "FILE");
        cardPanel.add(buildLinkPanel(), "LINK");
        outer.add(cardPanel, BorderLayout.CENTER);
        return outer;
    }

    // ══════════════════════════════════════════════════════════
    // TAB ANH
    // ══════════════════════════════════════════════════════════
    private JPanel buildAnhPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_LIGHT);
        wrap.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblAnhEmpty = emptyLabel("🖼", "Chưa có ảnh hay video nào", "Nhấn \"+ Tải lên\" để thêm");
        wrap.add(lblAnhEmpty, BorderLayout.NORTH);

        pnlAnhGrid = new JPanel(new GridLayout(0, 4, 12, 12));
        pnlAnhGrid.setOpaque(false);
        pnlAnhGrid.setVisible(false);

        JScrollPane scroll = new JScrollPane(pnlAnhGrid);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.setBorder(null);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Thêm card ảnh/video. Trả về panel để Controller giữ ref (dùng khi xóa).
     */
    public JPanel themCardAnh(boolean isVideo, String tenFile, String kichThuoc,
                               Runnable onDownload, Runnable onDelete) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? ACCENT_LIGHT : BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Preview
        String icon = isVideo ? "🎬" : "🖼";
        JPanel preview = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(225, 233, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(icon, getWidth()/2 - fm.stringWidth(icon)/2,
                              getHeight()/2 + fm.getAscent()/2 - 8);
                g2.dispose();
            }
        };
        preview.setOpaque(false);
        preview.setPreferredSize(new Dimension(0, 80));
        card.add(preview, BorderLayout.CENTER);

        // Info + actions
        JPanel bottom = new JPanel(new BorderLayout(0, 2));
        bottom.setOpaque(false);

        JLabel lName = new JLabel(
            "<html><body style='width:80px;font-size:10px;color:#1E293B'><b>" + truncate(tenFile, 18) + "</b></body></html>");
        JLabel lSize = new JLabel(kichThuoc);
        lSize.setFont(F_MICRO); lSize.setForeground(TEXT_LIGHT);

        JPanel actions = new JPanel(new GridLayout(1, 2, 4, 0));
        actions.setOpaque(false);
        JButton btnDl  = iconBtn("⬇", GREEN_SOFT, GREEN_MID, "Tải xuống");
        JButton btnDel = iconBtn("🗑", RED_SOFT,   RED_MID,   "Xóa");
        btnDl.addActionListener(e  -> onDownload.run());
        btnDel.addActionListener(e -> onDelete.run());
        actions.add(btnDl); actions.add(btnDel);

        bottom.add(lName,    BorderLayout.NORTH);
        bottom.add(lSize,    BorderLayout.CENTER);
        bottom.add(actions,  BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);

        cntAnh++;
        syncEmpty("ANH");
        pnlAnhGrid.add(card);
        pnlAnhGrid.revalidate();
        pnlAnhGrid.repaint();
        return card;
    }

    /** Xóa card khỏi tab Ảnh */
    public void xoaCardAnh(JPanel card) {
        pnlAnhGrid.remove(card);
        cntAnh = Math.max(0, cntAnh - 1);
        syncEmpty("ANH");
        pnlAnhGrid.revalidate();
        pnlAnhGrid.repaint();
    }

    // ══════════════════════════════════════════════════════════
    // TAB FILE
    // ══════════════════════════════════════════════════════════
    private JPanel buildFilePanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_LIGHT);
        wrap.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblFileEmpty = emptyLabel("📄", "Chưa có tài liệu nào", "Nhấn \"+ Tải lên\" để thêm file");
        wrap.add(lblFileEmpty, BorderLayout.NORTH);

        pnlFileList = new JPanel();
        pnlFileList.setOpaque(false);
        pnlFileList.setLayout(new BoxLayout(pnlFileList, BoxLayout.Y_AXIS));
        pnlFileList.setVisible(false);

        JScrollPane scroll = new JScrollPane(pnlFileList);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.setBorder(null);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Thêm hàng file. Trả về panel để Controller giữ ref.
     */
    public JPanel themFileRow(String iconEmoji, String tenFile, String loai,
                               String kichThuoc, String ngay, String nguoiGui,
                               Runnable onDownload, Runnable onDelete) {
        JPanel card = buildHoverCard(14, 0);
        card.setBorder(new EmptyBorder(12, 16, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lIcon = new JLabel(iconEmoji);
        lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        lIcon.setPreferredSize(new Dimension(38, 0));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lName = new JLabel(truncate(tenFile, 40));
        lName.setFont(F_BOLD); lName.setForeground(TEXT_DARK);
        JLabel lMeta = new JLabel(loai + "  ·  " + kichThuoc + "  ·  " + nguoiGui + "  ·  " + ngay);
        lMeta.setFont(F_MICRO); lMeta.setForeground(TEXT_LIGHT);
        info.add(lName); info.add(Box.createVerticalStrut(3)); info.add(lMeta);

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acts.setOpaque(false);
        JButton btnDl  = iconBtn("⬇", GREEN_SOFT, GREEN_MID, "Tải xuống");
        JButton btnDel = iconBtn("🗑", RED_SOFT,   RED_MID,   "Xóa");
        btnDl.addActionListener(e  -> onDownload.run());
        btnDel.addActionListener(e -> onDelete.run());
        acts.add(btnDl); acts.add(btnDel);

        card.add(lIcon, BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);
        card.add(acts,  BorderLayout.EAST);

        cntFile++;
        syncEmpty("FILE");
        pnlFileList.add(card);
        pnlFileList.add(Box.createVerticalStrut(8));
        pnlFileList.revalidate();
        pnlFileList.repaint();
        return card;
    }

    /** Xóa hàng file khỏi tab File */
    public void xoaFileRow(JPanel card) {
        // Tìm và xóa cả spacer phía dưới
        Component[] comps = pnlFileList.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == card) {
                pnlFileList.remove(card);
                if (i < pnlFileList.getComponentCount()) {
                    pnlFileList.remove(i); // spacer
                }
                break;
            }
        }
        cntFile = Math.max(0, cntFile - 1);
        syncEmpty("FILE");
        pnlFileList.revalidate();
        pnlFileList.repaint();
    }

    // ══════════════════════════════════════════════════════════
    // TAB LINK
    // ══════════════════════════════════════════════════════════
    private JPanel buildLinkPanel() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(BG_LIGHT);
        wrap.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblLinkEmpty = emptyLabel("🔗", "Chưa có link nào", "Nhấn \"+ Thêm link\" để chia sẻ URL");
        wrap.add(lblLinkEmpty, BorderLayout.NORTH);

        pnlLinkList = new JPanel();
        pnlLinkList.setOpaque(false);
        pnlLinkList.setLayout(new BoxLayout(pnlLinkList, BoxLayout.Y_AXIS));
        pnlLinkList.setVisible(false);

        JScrollPane scroll = new JScrollPane(pnlLinkList);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.setBorder(null);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    /**
     * Thêm hàng link. Trả về panel để Controller giữ ref.
     */
    public JPanel themLinkRow(String tieuDe, String url, String nguoiGui, String ngay,
                               Runnable onOpen, Runnable onDelete) {
        JPanel card = buildHoverCard(12, 0);
        card.setBorder(new EmptyBorder(14, 16, 14, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lIcon = new JLabel("🔗");
        lIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lIcon.setPreferredSize(new Dimension(36, 0));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lTitle = new JLabel(truncate(tieuDe, 45));
        lTitle.setFont(F_BOLD); lTitle.setForeground(TEXT_DARK);
        JLabel lUrl  = new JLabel(truncate(url, 55));
        lUrl.setFont(F_MICRO);  lUrl.setForeground(ACCENT);
        lUrl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel lMeta = new JLabel(nguoiGui + "  ·  " + ngay);
        lMeta.setFont(F_MICRO); lMeta.setForeground(TEXT_LIGHT);
        info.add(lTitle); info.add(Box.createVerticalStrut(2));
        info.add(lUrl);   info.add(Box.createVerticalStrut(2));
        info.add(lMeta);

        lUrl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { onOpen.run(); }
            public void mouseEntered(MouseEvent e) {
                lUrl.setText("<html><u>" + truncate(url, 55) + "</u></html>");
            }
            public void mouseExited(MouseEvent e) {
                lUrl.setText(truncate(url, 55));
            }
        });

        JPanel acts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        acts.setOpaque(false);
        JButton btnOpen = iconBtn("↗", ACCENT_LIGHT, ACCENT, "Mở link");
        JButton btnDel  = iconBtn("🗑", RED_SOFT,    RED_MID, "Xóa");
        btnOpen.addActionListener(e -> onOpen.run());
        btnDel.addActionListener(e  -> onDelete.run());
        acts.add(btnOpen); acts.add(btnDel);

        card.add(lIcon, BorderLayout.WEST);
        card.add(info,  BorderLayout.CENTER);
        card.add(acts,  BorderLayout.EAST);

        cntLink++;
        syncEmpty("LINK");
        pnlLinkList.add(card);
        pnlLinkList.add(Box.createVerticalStrut(8));
        pnlLinkList.revalidate();
        pnlLinkList.repaint();
        return card;
    }

    /** Xóa hàng link khỏi tab Link */
    public void xoaLinkRow(JPanel card) {
        Component[] comps = pnlLinkList.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] == card) {
                pnlLinkList.remove(card);
                if (i < pnlLinkList.getComponentCount()) {
                    pnlLinkList.remove(i);
                }
                break;
            }
        }
        cntLink = Math.max(0, cntLink - 1);
        syncEmpty("LINK");
        pnlLinkList.revalidate();
        pnlLinkList.repaint();
    }

    // ══════════════════════════════════════════════════════════
    // PUBLIC API – Controller điều khiển
    // ══════════════════════════════════════════════════════════

    public void hienThiCard(String card) { cardLayout.show(cardPanel, card); }
    public void setTextNutTaiLen(String text) { btnTaiLen.setText(text); }

    public void setActiveTab(JButton active, JButton... others) {
        active.setFont(F_BOLD); active.setForeground(ACCENT);
        for (JButton o : others) { o.setFont(F_BODY); o.setForeground(TEXT_MID); }
        repaint();
    }

    /** Mở dialog Thêm link, trả về [tenHienThi, url] hoặc null nếu hủy */
    public String[] moDialogThemLink() {
        ThemLinkDialog dlg = new ThemLinkDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
        if (!dlg.isConfirmed()) return null;
        return new String[]{ dlg.getTenHienThi(), dlg.getUrl() };
    }

    /** Hỏi xác nhận xóa, trả về true nếu người dùng đồng ý */
    public boolean xacNhanXoa(String tenFile) {
        int opt = JOptionPane.showConfirmDialog(this,
            "<html>Bạn có chắc muốn xóa <b>" + tenFile + "</b>?<br>"
            + "<span style='color:#94A3B8;font-size:11px'>Hành động này không thể hoàn tác.</span></html>",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return opt == JOptionPane.YES_OPTION;
    }

    /** Hiển thị thông báo download */
    public void hienThiThongBao(String msg, String title, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }

    /** Hiển thị thông báo tiến trình (non-blocking) */
    public JDialog taoDialogTienTrinh(String ten) {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
            "Đang tải xuống…", false);
        dlg.setSize(320, 100);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(new EmptyBorder(16, 20, 16, 20));
        p.setBackground(BG_WHITE);
        JLabel lbl = new JLabel("⬇  Đang tải: " + truncate(ten, 28));
        lbl.setFont(F_SMALL); lbl.setForeground(TEXT_DARK);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setForeground(ACCENT);
        p.add(lbl, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        dlg.setContentPane(p);
        return dlg;
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    /** Hiện empty-state hoặc list tuỳ theo số lượng item */
    private void syncEmpty(String tab) {
    	switch (tab) {
	        case "ANH": {
	            boolean has = cntAnh > 0;
	            lblAnhEmpty.setVisible(!has);
	            pnlAnhGrid.setVisible(has);
	            break;
	        }
	        case "FILE": {
	            boolean has = cntFile > 0;
	            lblFileEmpty.setVisible(!has);
	            pnlFileList.setVisible(has);
	            break;
	        }
	        case "LINK": {
	            boolean has = cntLink > 0;
	            lblLinkEmpty.setVisible(!has);
	            pnlLinkList.setVisible(has);
	            break;
	        }
	    }
    }

    private JLabel emptyLabel(String emoji, String line1, String line2) {
        JLabel l = new JLabel(
            "<html><center><span style='font-size:30px'>" + emoji + "</span><br><br>"
            + "<b style='color:#475569;font-size:13px'>" + line1 + "</b><br>"
            + "<span style='color:#94A3B8;font-size:11px'>" + line2 + "</span></center></html>",
            SwingConstants.CENTER);
        l.setBorder(new EmptyBorder(40, 0, 0, 0));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    /** Card có hover tint */
    private JPanel buildHoverCard(int hGap, int vGap) {
        return new JPanel(new BorderLayout(hGap, vGap)) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? ACCENT_LIGHT : BG_WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose(); super.paintComponent(g);
            }
        };
    }

    private JButton buildTabBtn(String text, boolean active) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getForeground().equals(ACCENT)) {
                    g.setColor(ACCENT);
                    g.fillRect(0, getHeight()-2, getWidth(), 2);
                }
            }
        };
        b.setFont(active ? F_BOLD : F_BODY);
        b.setForeground(active ? ACCENT : TEXT_MID);
        b.setBackground(BG_WHITE);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 16, 10, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Nút icon nhỏ với màu bg/fg tùy loại */
    private JButton iconBtn(String icon, Color bg, Color fg, String tooltip) {
        JButton b = new JButton(icon) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hov ? fg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        b.setForeground(fg);
        b.setPreferredSize(new Dimension(30, 26));
        b.setToolTipText(tooltip);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(2, 4, 2, 4));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    static JButton accentBtn(String text) {
        JButton b = new JButton(text) {
            boolean hov;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0,
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

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    // ══════════════════════════════════════════════════════════
    // INNER: ThemLinkDialog
    // ══════════════════════════════════════════════════════════
    private static class ThemLinkDialog extends JDialog {
        private boolean    confirmed = false;
        private JTextField txtTenHienThi;
        private JTextField txtUrl;

        ThemLinkDialog(java.awt.Frame owner) {
            super(owner, "Thêm link chia sẻ", true);
            setSize(460, 310);
            setMinimumSize(new Dimension(400, 280));
            setResizable(false);
            setLocationRelativeTo(owner);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(BG_WHITE);
            setContentPane(root);
            root.add(buildHeader(), BorderLayout.NORTH);
            root.add(buildForm(),   BorderLayout.CENTER);
            root.add(buildBtnBar(), BorderLayout.SOUTH);
            SwingUtilities.invokeLater(() -> txtUrl.requestFocusInWindow());
        }

        private JPanel buildHeader() {
            JPanel h = new JPanel(new BorderLayout());
            h.setBackground(BG_WHITE);
            h.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(18, 24, 16, 24)
            ));
            JLabel t = new JLabel("Thêm link chia sẻ");
            t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
            JLabel s = new JLabel("Chia sẻ URL với các thành viên trong nhóm");
            s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.add(t); col.add(Box.createVerticalStrut(3)); col.add(s);
            h.add(col, BorderLayout.WEST);
            return h;
        }

        private JPanel buildForm() {
            JPanel form = new JPanel();
            form.setOpaque(false);
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(new EmptyBorder(20, 28, 16, 28));

            form.add(secLabel("ĐỊA CHỈ URL *"));
            form.add(Box.createVerticalStrut(5));
            txtUrl = field("https://example.com");
            form.add(txtUrl);
            form.add(Box.createVerticalStrut(14));

            form.add(secLabel("TÊN HIỂN THỊ  (để trống sẽ dùng URL)"));
            form.add(Box.createVerticalStrut(5));
            txtTenHienThi = field("Ví dụ: Google Drive nhóm…");
            form.add(txtTenHienThi);
            return form;
        }

        private JPanel buildBtnBar() {
            JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
            bar.setBackground(BG_WHITE);
            bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));
            JButton btnHuy  = outlineBtn("Hủy");
            JButton btnThem = accentBtn("+ Thêm link");
            btnHuy.addActionListener(e -> dispose());
            btnThem.addActionListener(e -> xuLyThem());
            txtUrl.addActionListener(e -> xuLyThem());
            bar.add(btnHuy); bar.add(btnThem);
            return bar;
        }

        private void xuLyThem() {
            String url = txtUrl.getText().trim();
            String ph  = "https://example.com";
            if (url.isEmpty() || url.equals(ph)) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập địa chỉ URL.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                txtUrl.requestFocus(); return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                int opt = JOptionPane.showConfirmDialog(this,
                    "URL không bắt đầu bằng \"https://\".\nTự động thêm?",
                    "Kiểm tra URL", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) { url = "https://" + url; txtUrl.setText(url); }
            }
            String ten = txtTenHienThi.getText().trim();
            if (ten.isEmpty() || ten.startsWith("Ví dụ:")) txtTenHienThi.setText(url);
            confirmed = true; dispose();
        }

        boolean isConfirmed() { return confirmed; }
        String getUrl() {
            String v = txtUrl.getText().trim();
            return v.equals("https://example.com") ? "" : v;
        }
        String getTenHienThi() {
            String v = txtTenHienThi.getText().trim();
            return (v.isEmpty() || v.startsWith("Ví dụ:")) ? getUrl() : v;
        }

        private JLabel secLabel(String t) {
            JLabel l = new JLabel(t);
            l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
            l.setAlignmentX(Component.LEFT_ALIGNMENT); return l;
        }

        private JTextField field(String ph) {
            JTextField f = new JTextField();
            f.setFont(F_BODY); f.setCaretColor(ACCENT);
            f.setPreferredSize(new Dimension(0, 40));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0,12,0,12)));
            f.setText(ph); f.setForeground(TEXT_LIGHT);
            f.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (f.getText().equals(ph)) { f.setText(""); f.setForeground(TEXT_DARK); }
                    f.setBorder(new CompoundBorder(new LineBorder(ACCENT, 2, true), new EmptyBorder(0,12,0,12)));
                }
                public void focusLost(FocusEvent e) {
                    if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(TEXT_LIGHT); }
                    f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0,12,0,12)));
                }
            });
            return f;
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
                    g2.setColor(hov ? ACCENT_LIGHT : BG_WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.setColor(BORDER_CLR); g2.setStroke(new BasicStroke(1.5f));
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
}

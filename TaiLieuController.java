package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * TaiLieuController – xử lý toàn bộ logic màn hình Tài liệu.
 *
 * Tích hợp vào ManHinhChinh:
 *   Thay:  pnlCenter.add(new TaiLieuUI(), "TAI_LIEU");
 *   Bằng:  TaiLieuUI taiLieuUI = new TaiLieuUI();
 *          new TaiLieuController(taiLieuUI);
 *          pnlCenter.add(taiLieuUI, "TAI_LIEU");
 *
 * Luồng:
 *   Tab Ảnh/Video → btnTaiLen → JFileChooser (lọc ảnh & video)
 *   Tab File       → btnTaiLen → JFileChooser (lọc tài liệu)
 *   Tab Link       → btnTaiLen đổi thành "+ Thêm link" → nhập URL
 */
public class TaiLieuController {

    // ── Màu ───────────────────────────────────────────────────
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

    // ── State ─────────────────────────────────────────────────
    private final TaiLieuUI   view;
    private String            activeTab = "ANH"; // mặc định
    private final List<TaiLieu> danhSach = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────
    public TaiLieuController(TaiLieuUI view) {
        this.view = view;
        khoiTao();
    }

    // ── Wire sự kiện ─────────────────────────────────────────
    private void khoiTao() {
        // Theo dõi tab đang active để lọc file / đổi nút đúng loại
        view.btnTabAnh.addActionListener(e -> {
            activeTab = "ANH";
            capNhatNutTaiLen();
        });
        view.btnTabFile.addActionListener(e -> {
            activeTab = "FILE";
            capNhatNutTaiLen();
        });
        view.btnTabLink.addActionListener(e -> {
            activeTab = "LINK";
            capNhatNutTaiLen();
        });

        // Nút tải lên / thêm link
        view.btnTaiLen.addActionListener(e -> xuLyTaiLen());
    }

    /** Đổi text nút phù hợp với tab hiện tại */
    private void capNhatNutTaiLen() {
        view.btnTaiLen.setText("LINK".equals(activeTab) ? "+ Thêm link" : "+ Tải lên");
    }

    // ══════════════════════════════════════════════════════════
    // XỬ LÝ TẢI LÊN / THÊM LINK
    // ══════════════════════════════════════════════════════════

    private void xuLyTaiLen() {
        switch (activeTab) {
            case "ANH":  xuLyUploadAnh();  break;
            case "FILE": xuLyUploadFile(); break;
            case "LINK": xuLyThemLink();   break;
        }
    }

    // ── Tab Ảnh / Video ───────────────────────────────────────
    private void xuLyUploadAnh() {
        JFileChooser fc = taoFileChooser("Chọn ảnh hoặc video", true);
        fc.setFileFilter(new FileNameExtensionFilter(
            "Ảnh & Video (*.jpg, *.png, *.gif, *.mp4, *.avi, *.mov)",
            "jpg", "jpeg", "png", "gif", "bmp", "webp",
            "mp4", "avi", "mov", "mkv", "wmv"
        ));

        if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File[] files = layDanhSachFile(fc);
            for (File f : files) {
                danhSach.add(new TaiLieu(f.getName(), dinhDangFile(f), kichThuoc(f.length()), f.getAbsolutePath()));
            }
            hienThiUploadThanhCong(files, "ảnh/video");
        }
    }

    // ── Tab File ──────────────────────────────────────────────
    private void xuLyUploadFile() {
        JFileChooser fc = taoFileChooser("Chọn tài liệu", true);
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "PDF (*.pdf)", "pdf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "Word (*.docx, *.doc)", "docx", "doc"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "Excel (*.xlsx, *.xls)", "xlsx", "xls"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "PowerPoint (*.pptx, *.ppt)", "pptx", "ppt"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "Nén (*.zip, *.rar, *.7z)", "zip", "rar", "7z"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter(
            "Văn bản (*.txt)", "txt"));
        fc.setAcceptAllFileFilterUsed(true);

        if (fc.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File[] files = layDanhSachFile(fc);
            for (File f : files) {
                danhSach.add(new TaiLieu(f.getName(), dinhDangFile(f), kichThuoc(f.length()), f.getAbsolutePath()));
            }
            hienThiUploadThanhCong(files, "tài liệu");
        }
    }

    // ── Tab Link ──────────────────────────────────────────────
    private void xuLyThemLink() {
        ThemLinkDialog dialog = new ThemLinkDialog(
            (Frame) SwingUtilities.getWindowAncestor(view)
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            String tenHienThi = dialog.getTenHienThi();
            String url        = dialog.getUrl();
            danhSach.add(new TaiLieu(tenHienThi, "LINK", url, url));

            JOptionPane.showMessageDialog(
                view,
                "✓  Đã thêm link:\n\n"
                + "   Tên    : " + tenHienThi + "\n"
                + "   URL    : " + url,
                "Thêm link thành công",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    private JFileChooser taoFileChooser(String title, boolean multi) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setMultiSelectionEnabled(multi);
        try {
            fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        } catch (Exception ignored) {}
        return fc;
    }

    /** Lấy danh sách file đã chọn (hỗ trợ cả single và multi) */
    private File[] layDanhSachFile(JFileChooser fc) {
        File[] files = fc.getSelectedFiles();
        if (files == null || files.length == 0) {
            File single = fc.getSelectedFile();
            files = (single != null) ? new File[]{single} : new File[0];
        }
        return files;
    }

    private void hienThiUploadThanhCong(File[] files, String loai) {
        if (files.length == 0) return;

        StringBuilder sb = new StringBuilder();
        sb.append("✓  Đã tải lên ").append(files.length)
          .append(" ").append(loai).append(":\n\n");

        for (File f : files) {
            sb.append("   • ").append(f.getName())
              .append("  (").append(kichThuoc(f.length())).append(")\n");
        }

        JOptionPane.showMessageDialog(
            view,
            sb.toString(),
            "Tải lên thành công",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Định dạng kích thước file */
    private String kichThuoc(long bytes) {
        DecimalFormat df = new DecimalFormat("0.#");
        if (bytes < 1024L)             return bytes + " B";
        if (bytes < 1024L * 1024)      return df.format(bytes / 1024.0) + " KB";
        if (bytes < 1024L * 1024 * 1024) return df.format(bytes / (1024.0 * 1024)) + " MB";
        return df.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
    }

    /** Trích định dạng (extension) từ tên file */
    private String dinhDangFile(File f) {
        String name = f.getName();
        int dot = name.lastIndexOf('.');
        if (dot < 0) return "FILE";
        return name.substring(dot + 1).toUpperCase(Locale.ROOT);
    }

    /** Getter danh sách tài liệu đã tải lên (cho tầng khác nếu cần) */
    public List<TaiLieu> getDanhSach() {
        return Collections.unmodifiableList(danhSach);
    }

    // ══════════════════════════════════════════════════════════
    // DATA MODEL – TaiLieu
    // ══════════════════════════════════════════════════════════
    public static class TaiLieu {
        public final String ten;
        public final String dinhDang;
        public final String kichThuoc;
        public final String duongDan;

        public TaiLieu(String ten, String dinhDang, String kichThuoc, String duongDan) {
            this.ten       = ten;
            this.dinhDang  = dinhDang;
            this.kichThuoc = kichThuoc;
            this.duongDan  = duongDan;
        }
    }

    // ══════════════════════════════════════════════════════════
    // INNER DIALOG – Thêm link
    // ══════════════════════════════════════════════════════════
    private static class ThemLinkDialog extends JDialog {

        private boolean   confirmed    = false;
        private JTextField txtTenHienThi;
        private JTextField txtUrl;

        ThemLinkDialog(Frame owner) {
            super(owner, "Thêm link chia sẻ", true);
            setSize(440, 300);
            setMinimumSize(new Dimension(400, 270));
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
            form.setBorder(new EmptyBorder(22, 28, 18, 28));

            // URL
            form.add(secLabel("ĐỊA CHỈ URL  *"));
            form.add(Box.createVerticalStrut(6));
            txtUrl = field("https://...", true);
            form.add(txtUrl);
            form.add(Box.createVerticalStrut(16));

            // Tên hiển thị
            form.add(secLabel("TÊN HIỂN THỊ  (để trống để dùng URL)"));
            form.add(Box.createVerticalStrut(6));
            txtTenHienThi = field("Ví dụ: Google Drive nhóm...", true);
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

            bar.add(btnHuy);
            bar.add(btnThem);
            return bar;
        }

        private void xuLyThem() {
            String url = txtUrl.getText().trim();

            if (url.isEmpty() || url.equals("https://...")) {
                warn("Vui lòng nhập địa chỉ URL.");
                txtUrl.requestFocus(); return;
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                int opt = JOptionPane.showConfirmDialog(this,
                    "URL không bắt đầu bằng \"https://\".\nBạn có muốn tự động thêm không?",
                    "Kiểm tra URL", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    url = "https://" + url;
                    txtUrl.setText(url);
                }
            }

            // Tên hiển thị: nếu rỗng hoặc placeholder thì dùng URL
            String ten = txtTenHienThi.getText().trim();
            if (ten.isEmpty() || ten.startsWith("Ví dụ:")) ten = url;

            // Đặt giá trị đã xử lý để getter lấy đúng
            txtUrl.setText(url);
            txtTenHienThi.setText(ten);

            confirmed = true;
            dispose();
        }

        private void warn(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
        }

        boolean isConfirmed()    { return confirmed; }
        String  getUrl()         {
            String v = txtUrl.getText().trim();
            return v.equals("https://...") ? "" : v;
        }
        String  getTenHienThi()  {
            String v = txtTenHienThi.getText().trim();
            return (v.isEmpty() || v.startsWith("Ví dụ:")) ? getUrl() : v;
        }

        // ── Widget helpers ─────────────────────────────────────
        private JLabel secLabel(String t) {
            JLabel l = new JLabel(t);
            l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private JTextField field(String ph, boolean usePlaceholder) {
            JTextField f = new JTextField();
            f.setFont(F_BODY);
            f.setCaretColor(ACCENT);
            f.setPreferredSize(new Dimension(0, 40));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0, 12, 0, 12)));

            if (usePlaceholder) {
                f.setText(ph); f.setForeground(TEXT_LIGHT);
                f.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (f.getText().equals(ph)) { f.setText(""); f.setForeground(TEXT_DARK); }
                        f.setBorder(new CompoundBorder(
                            new LineBorder(ACCENT, 2, true), new EmptyBorder(0, 12, 0, 12)));
                    }
                    public void focusLost(FocusEvent e) {
                        if (f.getText().isEmpty()) { f.setText(ph); f.setForeground(TEXT_LIGHT); }
                        f.setBorder(new CompoundBorder(
                            new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0, 12, 0, 12)));
                    }
                });
            }
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
                    g2.setPaint(new GradientPaint(0, 0, hov ? new Color(0x4A7EE8) : ACCENT,
                                                  getWidth(), getHeight(), ACCENT_DARK));
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
                    g2.setStroke(new BasicStroke(1.5f));
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
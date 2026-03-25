package BaoCaoCuoiKi;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

/**
 * LichController – xử lý toàn bộ logic màn hình Lịch.
 *
 * Tích hợp vào ManHinhChinh:
 *   LichUI lichUI = new LichUI();
 *   new LichController(lichUI, nguoiDung);
 *   pnlCenter.add(lichUI, "LICH");
 */
public class LichController {

    // ── Màu (đồng nhất toàn app) ──────────────────────────────
    private static final Color ACCENT      = new Color(0x5B8DEF);
    private static final Color ACCENT_DARK = new Color(0x3D6FD4);
    private static final Color BORDER_CLR  = new Color(220, 226, 240);
    private static final Color TEXT_DARK   = new Color(0x1E293B);
    private static final Color TEXT_MID    = new Color(0x475569);
    private static final Color TEXT_LIGHT  = new Color(0x94A3B8);
    private static final Color BG_WHITE    = Color.WHITE;

    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  15);
    private static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font F_LABEL = new Font("Segoe UI", Font.BOLD,  10);

    // ── State ─────────────────────────────────────────────────
    private final LichUI     view;
    private final NguoiDung  nguoiDung;
    private final List<SuKien> suKienList = new ArrayList<>();

    // ── Constructor ───────────────────────────────────────────
    public LichController(LichUI view, NguoiDung nguoiDung) {
        this.view      = view;
        this.nguoiDung = nguoiDung;
        khoiTao();
    }

    // ── Wire sự kiện ─────────────────────────────────────────
    private void khoiTao() {
        view.btnThemSuKien.addActionListener(e -> moDialogThemSuKien());
    }

    // ── Mở dialog thêm sự kiện ────────────────────────────────
    private void moDialogThemSuKien() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(view);
        ThemSuKienDialog dialog = new ThemSuKienDialog(owner);
        dialog.setVisible(true);                // blocking

        if (dialog.isConfirmed()) {
            SuKien sk = dialog.getSuKien();
            suKienList.add(sk);
            hienThiThanhCong(sk);
            view.refreshEventList(suKienList);  // ← CẬP NHẬT UI
        }
    }

    private void hienThiThanhCong(SuKien sk) {
        JOptionPane.showMessageDialog(
            view,
            "✓  Đã thêm sự kiện mới:\n\n"
            + "   Tiêu đề   : " + sk.tieuDe       + "\n"
            + "   Ngày      : " + sk.ngay          + "\n"
            + "   Thời gian : " + sk.gioBatDau + " – " + sk.gioKetThuc + "\n"
            + "   Loại      : " + sk.loai + "\n"
            + "   Tạo bởi   : " + (sk.isPersonal ? "Bạn" : sk.tenNhom),
            "Thêm sự kiện thành công",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Trả về danh sách sự kiện */
    public List<SuKien> getSuKienList() {
        return Collections.unmodifiableList(suKienList);
    }

    // ══════════════════════════════════════════════════════════
    // DATA MODEL – SuKien
    // ══════════════════════════════════════════════════════════
    public static class SuKien {
        public final String tieuDe;
        public final String ngay;
        public final String gioBatDau;
        public final String gioKetThuc;
        public final String loai;
        public final Color  mauSac;
        public final boolean isPersonal;    // ← PHÂN BIỆT CÁ NHÂN vs NHÓM
        public final String tenNhom;        // ← TÊN NHÓM (nếu isPersonal=false)

        public SuKien(String tieuDe, String ngay, String gioBatDau,
                      String gioKetThuc, String loai, Color mauSac,
                      boolean isPersonal, String tenNhom) {
            this.tieuDe     = tieuDe;
            this.ngay       = ngay;
            this.gioBatDau  = gioBatDau;
            this.gioKetThuc = gioKetThuc;
            this.loai       = loai;
            this.mauSac     = mauSac;
            this.isPersonal = isPersonal;
            this.tenNhom    = tenNhom;
        }
    }

    // ══════════════════════════════════════════════════════════
    // INNER DIALOG – Thêm sự kiện
    // ══════════════════════════════════════════════════════════
    private static class ThemSuKienDialog extends JDialog {

        private boolean confirmed = false;
        private SuKien  result    = null;

        // Fields
        private JTextField        txtTieuDe;
        private JTextField        txtNgay;
        private JTextField        txtGioBD;
        private JTextField        txtGioKT;
        private JComboBox<String> cmbLoai;
        private JComboBox<String> cmbMau;
        private JRadioButton      radPersonal;
        private JRadioButton      radGroup;
        private JComboBox<String> cmbNhom;

        private static final String[] MAU_TEN = {
            "Xanh dương", "Xanh lá", "Vàng", "Đỏ", "Tím"
        };
        private static final Color[] MAU_CLR = {
            new Color(0x5B8DEF), new Color(0x10B981),
            new Color(0xF59E0B), new Color(0xEF4444), new Color(0x8B5CF6)
        };

        ThemSuKienDialog(Frame owner) {
            super(owner, "Thêm sự kiện mới", true);
            setSize(480, 500);
            setMinimumSize(new Dimension(440, 450));
            setResizable(false);
            setLocationRelativeTo(owner);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(BG_WHITE);
            setContentPane(root);

            root.add(buildHeader(), BorderLayout.NORTH);
            root.add(buildForm(),   BorderLayout.CENTER);
            root.add(buildBtnBar(), BorderLayout.SOUTH);

            // Enter navigation
            txtNgay.addActionListener(e -> txtGioBD.requestFocus());
            txtGioBD.addActionListener(e -> txtGioKT.requestFocus());
            txtGioKT.addActionListener(e -> xuLyThem());

            SwingUtilities.invokeLater(() -> txtTieuDe.requestFocusInWindow());
        }

        private JPanel buildHeader() {
            JPanel h = new JPanel(new BorderLayout());
            h.setBackground(BG_WHITE);
            h.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_CLR),
                new EmptyBorder(18, 24, 16, 24)
            ));
            JLabel t = new JLabel("Thêm sự kiện mới");
            t.setFont(F_TITLE); t.setForeground(TEXT_DARK);
            JLabel s = new JLabel("Điền thông tin sự kiện bên dưới");
            s.setFont(F_SMALL); s.setForeground(TEXT_LIGHT);
            JPanel col = new JPanel();
            col.setOpaque(false);
            col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
            col.add(t); col.add(Box.createVerticalStrut(3)); col.add(s);
            h.add(col, BorderLayout.WEST);
            return h;
        }

        private JScrollPane buildForm() {
            JPanel form = new JPanel();
            form.setOpaque(false);
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(new EmptyBorder(20, 28, 16, 28));

            // Tiêu đề
            form.add(secLabel("TIÊU ĐỀ SỰ KIỆN"));
            form.add(Box.createVerticalStrut(6));
            txtTieuDe = field("Nhập tiêu đề...", true);
            form.add(txtTieuDe);
            form.add(Box.createVerticalStrut(16));

            // Ngày
            LocalDate today = LocalDate.now();
            String todayStr = String.format("%02d/%02d/%d",
                today.getDayOfMonth(), today.getMonthValue(), today.getYear());

            form.add(secLabel("NGÀY  (DD/MM/YYYY)"));
            form.add(Box.createVerticalStrut(6));
            txtNgay = field(todayStr, false);
            txtNgay.setText(todayStr);
            txtNgay.setForeground(TEXT_DARK);
            form.add(txtNgay);
            form.add(Box.createVerticalStrut(16));

            // Giờ bắt đầu / kết thúc – 2 cột
            JPanel timeRow = new JPanel(new GridLayout(1, 2, 14, 0));
            timeRow.setOpaque(false);
            timeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            timeRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));

            JPanel bdCol = new JPanel();
            bdCol.setOpaque(false);
            bdCol.setLayout(new BoxLayout(bdCol, BoxLayout.Y_AXIS));
            bdCol.add(secLabel("GIỜ BẮT ĐẦU  (HH:MM)"));
            bdCol.add(Box.createVerticalStrut(6));
            txtGioBD = field("08:00", false);
            txtGioBD.setText("08:00"); txtGioBD.setForeground(TEXT_DARK);
            bdCol.add(txtGioBD);

            JPanel ktCol = new JPanel();
            ktCol.setOpaque(false);
            ktCol.setLayout(new BoxLayout(ktCol, BoxLayout.Y_AXIS));
            ktCol.add(secLabel("GIỜ KẾT THÚC  (HH:MM)"));
            ktCol.add(Box.createVerticalStrut(6));
            txtGioKT = field("09:00", false);
            txtGioKT.setText("09:00"); txtGioKT.setForeground(TEXT_DARK);
            ktCol.add(txtGioKT);

            timeRow.add(bdCol);
            timeRow.add(ktCol);
            form.add(timeRow);
            form.add(Box.createVerticalStrut(16));

            // Loại sự kiện
            form.add(secLabel("LOẠI SỰ KIỆN"));
            form.add(Box.createVerticalStrut(6));
            cmbLoai = new JComboBox<>(new String[]{"Cá nhân", "Nhóm học", "Công việc", "Thi cử", "Khác"});
            cmbLoai.setFont(F_BODY);
            cmbLoai.setAlignmentX(Component.LEFT_ALIGNMENT);
            cmbLoai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            form.add(cmbLoai);
            form.add(Box.createVerticalStrut(16));

            // Phân loại: Cá nhân vs Nhóm
            form.add(secLabel("CÓ PHẢI LỊC CÁ NHÂN KHÔNG?"));
            form.add(Box.createVerticalStrut(6));
            JPanel typePanel = new JPanel();
            typePanel.setOpaque(false);
            typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.X_AXIS));
            typePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            typePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            ButtonGroup typeGroup = new ButtonGroup();
            radPersonal = new JRadioButton("Cá nhân");
            radGroup = new JRadioButton("Nhóm");
            radPersonal.setSelected(true);
            radPersonal.setOpaque(false); radPersonal.setFont(F_BODY);
            radGroup.setOpaque(false); radGroup.setFont(F_BODY);
            typeGroup.add(radPersonal);
            typeGroup.add(radGroup);

            typePanel.add(radPersonal);
            typePanel.add(Box.createHorizontalStrut(20));
            typePanel.add(radGroup);
            typePanel.add(Box.createHorizontalGlue());
            form.add(typePanel);
            form.add(Box.createVerticalStrut(16));

            // Chọn nhóm (nếu là nhóm)
            form.add(secLabel("CHỌN NHÓM"));
            form.add(Box.createVerticalStrut(6));
            cmbNhom = new JComboBox<>(new String[]{"Nhóm 1", "Nhóm 2", "Nhóm 3", "Nhóm 4"});
            cmbNhom.setFont(F_BODY);
            cmbNhom.setAlignmentX(Component.LEFT_ALIGNMENT);
            cmbNhom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            cmbNhom.setEnabled(false);  // ← Tắt khi personal được chọn
            form.add(cmbNhom);
            form.add(Box.createVerticalStrut(16));

            // Toggle group combobox
            radPersonal.addActionListener(e -> cmbNhom.setEnabled(false));
            radGroup.addActionListener(e -> cmbNhom.setEnabled(true));

            // Màu sắc
            form.add(secLabel("MÀU SẮC"));
            form.add(Box.createVerticalStrut(6));
            cmbMau = new JComboBox<>(MAU_TEN);
            cmbMau.setFont(F_BODY);
            cmbMau.setAlignmentX(Component.LEFT_ALIGNMENT);
            cmbMau.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            form.add(cmbMau);

            JScrollPane scroll = new JScrollPane(form);
            scroll.setBorder(null);
            scroll.getViewport().setBackground(BG_WHITE);
            return scroll;
        }

        private JPanel buildBtnBar() {
            JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
            bar.setBackground(BG_WHITE);
            bar.setBorder(new MatteBorder(1, 0, 0, 0, BORDER_CLR));

            JButton btnHuy  = outlineBtn("Hủy");
            JButton btnThem = accentBtn("+ Thêm sự kiện");

            btnHuy.addActionListener(e -> dispose());
            btnThem.addActionListener(e -> xuLyThem());

            bar.add(btnHuy);
            bar.add(btnThem);
            return bar;
        }

        private void xuLyThem() {
            String tieuDe = txtTieuDe.getText().trim();
            String ngay   = txtNgay.getText().trim();
            String gioBD  = txtGioBD.getText().trim();
            String gioKT  = txtGioKT.getText().trim();

            // Validate tiêu đề
            if (tieuDe.isEmpty() || tieuDe.equals("Nhập tiêu đề...")) {
                warn("Vui lòng nhập tiêu đề sự kiện.");
                txtTieuDe.requestFocus(); return;
            }
            if (tieuDe.length() > 100) {
                warn("Tiêu đề tối đa 100 ký tự."); return;
            }

            // Validate ngày
            if (!ngay.matches("\\d{2}/\\d{2}/\\d{4}")) {
                warn("Ngày không đúng định dạng.\nVui lòng nhập theo dạng DD/MM/YYYY.");
                txtNgay.requestFocus(); return;
            }
            try {
                String[] p = ngay.split("/");
                int d = Integer.parseInt(p[0]),
                    m = Integer.parseInt(p[1]),
                    y = Integer.parseInt(p[2]);
                if (d < 1 || d > 31 || m < 1 || m > 12 || y < 2000 || y > 2100)
                    throw new NumberFormatException();
            } catch (Exception ex) {
                warn("Ngày không hợp lệ.\nVui lòng kiểm tra lại.");
                txtNgay.requestFocus(); return;
            }

            // Validate giờ bắt đầu
            if (!gioBD.matches("\\d{2}:\\d{2}")) {
                warn("Giờ bắt đầu không đúng định dạng.\nVui lòng nhập theo dạng HH:MM.");
                txtGioBD.requestFocus(); return;
            }
            // Validate giờ kết thúc
            if (!gioKT.matches("\\d{2}:\\d{2}")) {
                warn("Giờ kết thúc không đúng định dạng.\nVui lòng nhập theo dạng HH:MM.");
                txtGioKT.requestFocus(); return;
            }
            // Giờ kết thúc phải sau giờ bắt đầu
            if (gioBD.compareTo(gioKT) >= 0) {
                warn("Giờ kết thúc phải sau giờ bắt đầu."); return;
            }

            Color mau = MAU_CLR[cmbMau.getSelectedIndex()];
            boolean isPersonal = radPersonal.isSelected();
            String tenNhom = isPersonal ? "Cá nhân" : (String) cmbNhom.getSelectedItem();

            result    = new SuKien(
                tieuDe, ngay, gioBD, gioKT,
                (String) cmbLoai.getSelectedItem(), mau, isPersonal, tenNhom
            );
            confirmed = true;
            dispose();
        }

        private void warn(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.WARNING_MESSAGE);
        }

        boolean isConfirmed() { return confirmed; }
        SuKien  getSuKien()   { return result;    }

        private JLabel secLabel(String t) {
            JLabel l = new JLabel(t);
            l.setFont(F_LABEL); l.setForeground(TEXT_LIGHT);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            return l;
        }

        private JTextField field(String placeholder, boolean usePlaceholder) {
            JTextField f = new JTextField();
            f.setFont(F_BODY);
            f.setCaretColor(ACCENT);
            f.setPreferredSize(new Dimension(0, 40));
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            f.setAlignmentX(Component.LEFT_ALIGNMENT);
            f.setBorder(new CompoundBorder(
                new LineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 12, 0, 12)
            ));

            if (usePlaceholder) {
                f.setText(placeholder); f.setForeground(TEXT_LIGHT);
                f.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        if (f.getText().equals(placeholder)) {
                            f.setText(""); f.setForeground(TEXT_DARK);
                        }
                        f.setBorder(new CompoundBorder(
                            new LineBorder(ACCENT, 2, true), new EmptyBorder(0, 12, 0, 12)));
                    }
                    public void focusLost(FocusEvent e) {
                        if (f.getText().isEmpty()) {
                            f.setText(placeholder); f.setForeground(TEXT_LIGHT);
                        }
                        f.setBorder(new CompoundBorder(
                            new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(0, 12, 0, 12)));
                    }
                });
            } else {
                f.setForeground(TEXT_DARK);
                f.addFocusListener(new FocusAdapter() {
                    public void focusGained(FocusEvent e) {
                        f.setBorder(new CompoundBorder(
                            new LineBorder(ACCENT, 2, true), new EmptyBorder(0, 12, 0, 12)));
                    }
                    public void focusLost(FocusEvent e) {
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

package BaoCaoCuoiKi;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * TaiLieuController – Controller màn hình Tài liệu.
 *
 * Trách nhiệm:
 *   1. Wire sự kiện từ View
 *   2. Xử lý nghiệp vụ: chọn file, upload (copy local), download, xóa, mở link
 *   3. Điều phối tab: upload luôn mở đúng tab (Ảnh → ANH, File → FILE, Link → LINK)
 *   4. Download dùng socket/HTTP (SwingWorker nền, không block UI)
 *
 * KHÔNG chứa bất kỳ code tạo UI (JPanel, JLabel, layout…).
 *
 * Tích hợp:
 *   TaiLieuUI ui = new TaiLieuUI();
 *   new TaiLieuController(ui, "uploads/"); // thư mục lưu file upload
 *   pnlCenter.add(ui, "TAI_LIEU");
 */
public class TaiLieuController {

    // ── Extension sets ─────────────────────────────────────────
    private static final Set<String> EXT_VIDEO = Set.of(
        "mp4","avi","mov","mkv","wmv","flv","webm"
    );

    // ── State ─────────────────────────────────────────────────
    private final TaiLieuUI view;
    private final Path      khoLuu;          // thư mục lưu file upload local
    private String          activeTab = "ANH";

    // Danh sách model tài liệu
    private final List<TaiLieu> danhSach = new ArrayList<>();

    // Thread pool cho download bất đồng bộ
    private final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "TaiLieu-Download");
        t.setDaemon(true);
        return t;
    });

    // ── Constructor ───────────────────────────────────────────
    public TaiLieuController(TaiLieuUI view, String khoLuuDir) {
        this.view   = view;
        this.khoLuu = Path.of(khoLuuDir);
        khoiTaoKho();
        khoiTao();
    }

    /** Tạo thư mục lưu trữ nếu chưa có */
    private void khoiTaoKho() {
        try { Files.createDirectories(khoLuu); }
        catch (IOException e) { e.printStackTrace(); }
    }

    // ══════════════════════════════════════════════════════════
    // WIRE SỰ KIỆN
    // ══════════════════════════════════════════════════════════
    private void khoiTao() {
        view.btnTabAnh.addActionListener(e  -> chuyenTab("ANH"));
        view.btnTabFile.addActionListener(e -> chuyenTab("FILE"));
        view.btnTabLink.addActionListener(e -> chuyenTab("LINK"));
        view.btnTaiLen.addActionListener(e  -> xuLyTaiLen());
    }

    // ── Chuyển tab ────────────────────────────────────────────
    private void chuyenTab(String tab) {
        activeTab = tab;
        switch (tab) {
	        case "ANH":
	            view.setActiveTab(view.btnTabAnh, view.btnTabFile, view.btnTabLink);
	            break;
	        case "FILE":
	            view.setActiveTab(view.btnTabFile, view.btnTabAnh, view.btnTabLink);
	            break;
	        case "LINK":
	            view.setActiveTab(view.btnTabLink, view.btnTabAnh, view.btnTabFile);
	            break;
	    }
        view.hienThiCard(tab);
        view.setTextNutTaiLen("LINK".equals(tab) ? "+ Thêm link" : "+ Tải lên");
    }

    // ══════════════════════════════════════════════════════════
    // XỬ LÝ NÚT TẢI LÊN / THÊM LINK
    // ══════════════════════════════════════════════════════════
    private void xuLyTaiLen() {
    	switch (activeTab) {
        case "ANH":
            xuLyUploadAnh();
            break;
        case "FILE":
            xuLyUploadFile();
            break;
        case "LINK":
            xuLyThemLink();
            break;
    }
    }

    // ── Tab Ảnh / Video ───────────────────────────────────────
    private void xuLyUploadAnh() {
        JFileChooser fc = taoFileChooser("Chọn ảnh hoặc video", true);
        fc.setFileFilter(new FileNameExtensionFilter(
            "Ảnh & Video (*.jpg, *.png, *.gif, *.mp4, *.avi, *.mov)",
            "jpg","jpeg","png","gif","bmp","webp","mp4","avi","mov","mkv","wmv"
        ));
        if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;

        File[] files = layDanhSachFile(fc);
        String ngay  = ngayHomNay();

        for (File f : files) {
            Path dest = copyToKho(f);
            if (dest == null) continue;

            String ext     = layExt(f);
            boolean isVideo = EXT_VIDEO.contains(ext.toLowerCase(Locale.ROOT));
            String size    = kichThuoc(f.length());

            TaiLieu tl = new TaiLieu(f.getName(), ext.toUpperCase(Locale.ROOT),
                                     size, dest.toString(), TaiLieu.Loai.ANH);
            danhSach.add(tl);

            // Thêm card lên View — callback xóa / download được truyền vào đây
            JPanel[] ref = new JPanel[1];
            ref[0] = view.themCardAnh(isVideo, f.getName(), size,
                () -> xuLyDownload(tl),            // onDownload
                () -> xuLyXoaAnh(tl, ref[0])       // onDelete
            );
        }

        // Tự động chuyển về tab Ảnh sau upload
        chuyenTab("ANH");
    }

    // ── Tab File ──────────────────────────────────────────────
    private void xuLyUploadFile() {
        JFileChooser fc = taoFileChooser("Chọn tài liệu", true);
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PDF",                    "pdf"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Word",                   "docx","doc"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Excel",                  "xlsx","xls"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("PowerPoint",             "pptx","ppt"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Nén (zip/rar/7z)",       "zip","rar","7z"));
        fc.addChoosableFileFilter(new FileNameExtensionFilter("Văn bản (*.txt)",         "txt"));
        fc.setAcceptAllFileFilterUsed(true);
        if (fc.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) return;

        File[] files = layDanhSachFile(fc);
        String ngay  = ngayHomNay();

        for (File f : files) {
            Path dest = copyToKho(f);
            if (dest == null) continue;

            String ext  = layExt(f).toUpperCase(Locale.ROOT);
            String size = kichThuoc(f.length());

            TaiLieu tl = new TaiLieu(f.getName(), ext, size, dest.toString(), TaiLieu.Loai.FILE);
            danhSach.add(tl);

            JPanel[] ref = new JPanel[1];
            ref[0] = view.themFileRow(
                iconChoFile(ext), f.getName(), ext, size, ngay, "Bạn",
                () -> xuLyDownload(tl),
                () -> xuLyXoaFile(tl, ref[0])
            );
        }

        // Tự động chuyển về tab File
        chuyenTab("FILE");
    }

    // ── Tab Link ──────────────────────────────────────────────
    private void xuLyThemLink() {
        String[] ketQua = view.moDialogThemLink();
        if (ketQua == null) return;

        String ten = ketQua[0];
        String url = ketQua[1];
        if (url == null || url.isEmpty()) return;

        TaiLieu tl = new TaiLieu(ten, "LINK", url, url, TaiLieu.Loai.LINK);
        danhSach.add(tl);

        String ngay = ngayHomNay();
        JPanel[] ref = new JPanel[1];
        ref[0] = view.themLinkRow(ten, url, "Bạn", ngay,
            () -> moLink(url),
            () -> xuLyXoaLink(tl, ref[0])
        );

        chuyenTab("LINK");
    }

    // ══════════════════════════════════════════════════════════
    // XÓA
    // ══════════════════════════════════════════════════════════

    private void xuLyXoaAnh(TaiLieu tl, JPanel card) {
        if (!view.xacNhanXoa(tl.ten)) return;
        xoaFileVatLy(tl);
        danhSach.remove(tl);
        view.xoaCardAnh(card);
    }

    private void xuLyXoaFile(TaiLieu tl, JPanel card) {
        if (!view.xacNhanXoa(tl.ten)) return;
        xoaFileVatLy(tl);
        danhSach.remove(tl);
        view.xoaFileRow(card);
    }

    private void xuLyXoaLink(TaiLieu tl, JPanel card) {
        if (!view.xacNhanXoa(tl.ten)) return;
        danhSach.remove(tl);
        view.xoaLinkRow(card);
    }

    /** Xóa file vật lý trong kho lưu trữ */
    private void xoaFileVatLy(TaiLieu tl) {
        try {
            Path p = Path.of(tl.duongDan);
            if (Files.exists(p)) Files.delete(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════
    // DOWNLOAD – hỗ trợ file local VÀ HTTP/HTTPS qua Socket
    // ══════════════════════════════════════════════════════════

    private void xuLyDownload(TaiLieu tl) {
        // Chọn nơi lưu
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu file về máy");
        fc.setSelectedFile(new File(tl.ten));
        if (fc.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) return;

        File dest = fc.getSelectedFile();

        if (tl.loai == TaiLieu.Loai.LINK) {
            // Với link, không download mà mở browser
            moLink(tl.duongDan);
            return;
        }

        // Kiểm tra nguồn
        boolean isHttp = tl.duongDan.startsWith("http://") || tl.duongDan.startsWith("https://");

        JDialog dlgProgress = view.taoDialogTienTrinh(tl.ten);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            String errorMsg = null;

            @Override protected Void doInBackground() {
                try {
                    if (isHttp) {
                        downloadViaSocket(tl.duongDan, dest);
                    } else {
                        copyLocalFile(tl.duongDan, dest);
                    }
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                }
                return null;
            }

            @Override protected void done() {
                dlgProgress.dispose();
                if (errorMsg != null) {
                    view.hienThiThongBao(
                        "Lỗi tải xuống:\n" + errorMsg,
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    view.hienThiThongBao(
                        "✓  Đã lưu tại:\n" + dest.getAbsolutePath(),
                        "Tải xuống thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Mở thư mục chứa file (nếu Desktop hỗ trợ)
                    try {
                        if (Desktop.isDesktopSupported())
                            Desktop.getDesktop().open(dest.getParentFile());
                    } catch (Exception ignored) {}
                }
            }
        };

        dlgProgress.setVisible(true);
        worker.execute();
    }

    /**
     * Download file từ URL HTTP/HTTPS qua Socket (không dùng thư viện ngoài).
     * Hỗ trợ redirect (301/302) tối đa 5 lần.
     */
    private void downloadViaSocket(String urlStr, File dest) throws IOException {
        int maxRedirect = 5;
        String currentUrl = urlStr;

        for (int i = 0; i < maxRedirect; i++) {
            URL url = new URL(currentUrl);
            String host = url.getHost();
            int port = url.getPort() == -1
                    ? ("https".equalsIgnoreCase(url.getProtocol()) ? 443 : 80)
                    : url.getPort();

            String path = url.getPath().isEmpty() ? "/" : url.getPath();
            if (url.getQuery() != null) path += "?" + url.getQuery();

            boolean isHttps = "https".equalsIgnoreCase(url.getProtocol());

            try (
                Socket socket = isHttps
                        ? javax.net.ssl.SSLSocketFactory.getDefault().createSocket(host, port)
                        : new Socket(host, port)
            ) {
                socket.setSoTimeout(15000);

                OutputStream rawOut = socket.getOutputStream();
                InputStream rawIn   = socket.getInputStream();

                PrintWriter out = new PrintWriter(new OutputStreamWriter(rawOut, "UTF-8"), true);

                // Gửi request
                out.print("GET " + path + " HTTP/1.1\r\n");
                out.print("Host: " + host + "\r\n");
                out.print("User-Agent: TaiLieuApp/1.0\r\n");
                out.print("Connection: close\r\n");
                out.print("\r\n");
                out.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(rawIn));

                // Status line
                String statusLine = br.readLine();
                if (statusLine == null) throw new IOException("Không có phản hồi");

                int statusCode = Integer.parseInt(statusLine.split(" ")[1]);

                String location = null;
                int contentLength = -1;

                // Headers
                String line;
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    String lower = line.toLowerCase(Locale.ROOT);
                    if (lower.startsWith("location:"))
                        location = line.substring(line.indexOf(":") + 1).trim();
                    if (lower.startsWith("content-length:"))
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                }

                // Redirect
                if ((statusCode == 301 || statusCode == 302 || statusCode == 307) && location != null) {
                    currentUrl = location.startsWith("http")
                            ? location
                            : url.getProtocol() + "://" + host + location;
                    continue;
                }

                if (statusCode < 200 || statusCode >= 300) {
                    throw new IOException("HTTP lỗi: " + statusCode);
                }

                // ❗ QUAN TRỌNG: đọc binary đúng
                try (FileOutputStream fos = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;

                    // KHÔNG dùng br nữa (vì corrupt binary)
                    while ((bytesRead = rawIn.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                return; // thành công
            }
        }

        throw new IOException("Quá nhiều redirect");
    }
    /** Copy file local vào dest */
    private void copyLocalFile(String srcPath, File dest) throws IOException {
        Path src = Path.of(srcPath);
        if (!Files.exists(src)) throw new IOException("File gốc không tồn tại: " + srcPath);
        Files.copy(src, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // ══════════════════════════════════════════════════════════
    // MỞ LINK
    // ══════════════════════════════════════════════════════════
    private void moLink(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback: copy URL vào clipboard
                java.awt.datatransfer.StringSelection sel =
                    new java.awt.datatransfer.StringSelection(url);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
                view.hienThiThongBao(
                    "Trình duyệt không khả dụng.\nĐã copy URL vào clipboard:\n" + url,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            view.hienThiThongBao("Không thể mở link:\n" + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ══════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════

    /** Copy file vào kho lưu trữ local, trả về đường dẫn mới */
    private Path copyToKho(File f) {
        try {
            Path dest = khoLuu.resolve(f.getName());
            // Nếu trùng tên, thêm timestamp
            if (Files.exists(dest)) {
                String ts   = String.valueOf(System.currentTimeMillis());
                String name = f.getName();
                int dot = name.lastIndexOf('.');
                String base = dot < 0 ? name : name.substring(0, dot);
                String ext  = dot < 0 ? ""   : name.substring(dot);
                dest = khoLuu.resolve(base + "_" + ts + ext);
            }
            Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest;
        } catch (IOException e) {
            view.hienThiThongBao("Không thể lưu file:\n" + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private JFileChooser taoFileChooser(String title, boolean multi) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle(title);
        fc.setMultiSelectionEnabled(multi);
        try { fc.setCurrentDirectory(new File(System.getProperty("user.home"))); }
        catch (Exception ignored) {}
        return fc;
    }

    private File[] layDanhSachFile(JFileChooser fc) {
        File[] files = fc.getSelectedFiles();
        if (files == null || files.length == 0) {
            File single = fc.getSelectedFile();
            files = (single != null) ? new File[]{single} : new File[0];
        }
        return files;
    }

    private String kichThuoc(long bytes) {
        DecimalFormat df = new DecimalFormat("0.#");
        if (bytes < 1024L)               return bytes + " B";
        if (bytes < 1024L * 1024)        return df.format(bytes / 1024.0) + " KB";
        if (bytes < 1024L * 1024 * 1024) return df.format(bytes / (1024.0 * 1024)) + " MB";
        return df.format(bytes / (1024.0 * 1024 * 1024)) + " GB";
    }

    private String layExt(File f) {
        String name = f.getName();
        int dot = name.lastIndexOf('.');
        return (dot < 0) ? "FILE" : name.substring(dot + 1);
    }

    private String iconChoFile(String ext) {
        if (ext == null) return "📁";

        switch (ext.toUpperCase(Locale.ROOT)) {
            case "PDF":
                return "📕";
            case "DOC":
            case "DOCX":
                return "📝";
            case "XLS":
            case "XLSX":
                return "📊";
            case "PPT":
            case "PPTX":
                return "📋";
            case "ZIP":
            case "RAR":
            case "7Z":
                return "🗜";
            case "TXT":
                return "📄";
            default:
                return "📁";
        }
    }

    private String ngayHomNay() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
    }

    // ── Getter ────────────────────────────────────────────────
    public List<TaiLieu> getDanhSach() { return Collections.unmodifiableList(danhSach); }

    // ══════════════════════════════════════════════════════════
    // DATA MODEL
    // ══════════════════════════════════════════════════════════
    public static class TaiLieu {
        public enum Loai { ANH, FILE, LINK }

        public final String ten;
        public final String dinhDang;
        public final String kichThuoc;
        public final String duongDan;   // path local hoặc URL
        public final Loai   loai;

        public TaiLieu(String ten, String dinhDang, String kichThuoc, String duongDan, Loai loai) {
            this.ten       = ten;
            this.dinhDang  = dinhDang;
            this.kichThuoc = kichThuoc;
            this.duongDan  = duongDan;
            this.loai      = loai;
        }
    }
}

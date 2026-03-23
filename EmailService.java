package BaoCaoCuoiKi;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Dịch vụ gửi email qua SMTP Gmail.
 *
 * Chuẩn bị:
 * 1. Bật "2-Step Verification": myaccount.google.com/security
 * 2. Tạo App Password:          myaccount.google.com/apppasswords
 *    → Select app: Mail
 *    → Select device: Windows Computer
 *    → Copy mã 16 ký tự → điền vào SENDER_PASS
 */
public class EmailService {

    // ── Cấu hình SMTP ────────────────────────────────────────
    private static final String SMTP_HOST     = "smtp.gmail.com";
    private static final int    SMTP_PORT     = 587;
    private static final String SENDER_EMAIL  = "baamntk@gmail.com";   // ← email gửi
    private static final String SENDER_PASS   = "mtsy esqp vnlf bjxt"; // ← App Password 16 ký tự
    private static final String SENDER_NAME   = "BAAM App";

    private static final int HET_HAN_PHUT = 10;

    // ── Tạo mã OTP 6 chữ số ──────────────────────────────────
    public static String taoMa() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public static int getHetHanPhut() { return HET_HAN_PHUT; }

    // ══════════════════════════════════════════════════════════
    // Gửi OTP
    // ══════════════════════════════════════════════════════════
    public static void guiMaOTP(String toEmail, String ma, String loai)
            throws MessagingException, UnsupportedEncodingException {

        String tieuDe = "DANG_KI".equals(loai)
            ? "[BAAM] Ma xac thuc dang ky tai khoan"
            : "[BAAM] Ma dat lai mat khau";

        String noiDung = "DANG_KI".equals(loai)
            ? buildHtmlDangKi(ma)
            : buildHtmlQuenMK(ma);

        Session session = taoSession();

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME, "UTF-8"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(MimeUtility.encodeText(tieuDe, "UTF-8", "B")); // encode subject UTF-8
        msg.setContent(noiDung, "text/html; charset=UTF-8");

        Transport.send(msg);
    }

    // ══════════════════════════════════════════════════════════
    // Session SMTP
    // ══════════════════════════════════════════════════════════
    private static Session taoSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth",               "true");
        props.put("mail.smtp.starttls.enable",    "true");
        props.put("mail.smtp.starttls.required",  "true");
        props.put("mail.smtp.host",               SMTP_HOST);
        props.put("mail.smtp.port",               String.valueOf(SMTP_PORT));
        props.put("mail.smtp.ssl.protocols",      "TLSv1.2");
        props.put("mail.smtp.connectiontimeout",  "10000"); // 10 giây
        props.put("mail.smtp.timeout",            "10000");
        props.put("mail.smtp.writetimeout",       "10000");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASS);
            }
        });
    }

    // ══════════════════════════════════════════════════════════
    // HTML Templates
    // ══════════════════════════════════════════════════════════
    private static String buildHtmlDangKi(String ma) {
        return "<!DOCTYPE html><html><body>" +
               "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:480px;margin:auto;" +
               "border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,.12)'>" +

               // Header
               "<div style='background:linear-gradient(135deg,#5B8DEF,#3D6FD4);" +
               "padding:36px 32px;text-align:center'>" +
               "<h1 style='color:#fff;margin:0;font-size:32px;letter-spacing:2px'>BAAM</h1>" +
               "<p style='color:rgba(255,255,255,.85);margin:8px 0 0;font-size:14px'>" +
               "Xac thuc tai khoan</p></div>" +

               // Body
               "<div style='background:#ffffff;padding:36px 32px;text-align:center'>" +
               "<p style='color:#374151;font-size:15px;margin-bottom:8px'>" +
               "Ma xac thuc dang ky cua ban la:</p>" +
               "<div style='background:#EFF6FF;border-radius:12px;padding:24px;margin:20px 0;" +
               "border:1px solid #BFDBFE'>" +
               "<span style='font-size:40px;font-weight:bold;letter-spacing:12px;color:#3D6FD4;" +
               "font-family:monospace'>" + ma + "</span></div>" +
               "<p style='color:#6B7280;font-size:13px;line-height:1.6'>" +
               "Ma co hieu luc trong <strong>" + HET_HAN_PHUT + " phut</strong>.<br>" +
               "Khong chia se ma nay voi bat ky ai.</p>" +
               "</div>" +

               // Footer
               "<div style='background:#F9FAFB;padding:16px;text-align:center;" +
               "border-top:1px solid #E5E7EB'>" +
               "<p style='color:#9CA3AF;font-size:12px;margin:0'>" +
               "Email nay duoc gui tu BAAM App. Vui long khong reply.</p>" +
               "</div></div></body></html>";
    }

    private static String buildHtmlQuenMK(String ma) {
        return "<!DOCTYPE html><html><body>" +
               "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:480px;margin:auto;" +
               "border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,.12)'>" +

               // Header
               "<div style='background:linear-gradient(135deg,#5B8DEF,#3D6FD4);" +
               "padding:36px 32px;text-align:center'>" +
               "<h1 style='color:#fff;margin:0;font-size:32px;letter-spacing:2px'>BAAM</h1>" +
               "<p style='color:rgba(255,255,255,.85);margin:8px 0 0;font-size:14px'>" +
               "Dat lai mat khau</p></div>" +

               // Body
               "<div style='background:#ffffff;padding:36px 32px;text-align:center'>" +
               "<p style='color:#374151;font-size:15px;margin-bottom:8px'>" +
               "Ma dat lai mat khau cua ban la:</p>" +
               "<div style='background:#FFF7ED;border-radius:12px;padding:24px;margin:20px 0;" +
               "border:1px solid #FED7AA'>" +
               "<span style='font-size:40px;font-weight:bold;letter-spacing:12px;color:#EA580C;" +
               "font-family:monospace'>" + ma + "</span></div>" +
               "<p style='color:#6B7280;font-size:13px;line-height:1.6'>" +
               "Ma co hieu luc trong <strong>" + HET_HAN_PHUT + " phut</strong>.<br>" +
               "Neu ban khong yeu cau, hay bo qua email nay.</p>" +
               "</div>" +

               // Footer
               "<div style='background:#F9FAFB;padding:16px;text-align:center;" +
               "border-top:1px solid #E5E7EB'>" +
               "<p style='color:#9CA3AF;font-size:12px;margin:0'>" +
               "Email nay duoc gui tu BAAM App. Vui long khong reply.</p>" +
               "</div></div></body></html>";
    }

//    // ══════════════════════════════════════════════════════════
//    // Test gửi mail độc lập
//    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        System.out.println("════════════════════════════════");
        System.out.println("  Test gửi email BAAM App");
        System.out.println("════════════════════════════════");
        System.out.println("Từ   : " + SENDER_EMAIL);
        System.out.println("Đến  : nguyentuankhoi2603@gmail.com");
        System.out.println("Mã   : 123456");
        System.out.println("Loại : DANG_KI");
        System.out.println("────────────────────────────────");
        System.out.println("Đang kết nối SMTP...");

        try {
            guiMaOTP("nguyentuankhoi2603@gmail.com", "123456", "DANG_KI");
            System.out.println("✓ Gửi thành công!");
            System.out.println("  Kiểm tra hộp thư (kể cả Spam)");
        } catch (AuthenticationFailedException e) {
            System.out.println("✗ Sai App Password!");
            System.out.println("  → Vào myaccount.google.com/apppasswords");
            System.out.println("  → Tạo App Password mới và điền vào SENDER_PASS");
        } catch (MessagingException e) {
            System.out.println("✗ Lỗi kết nối SMTP: " + e.getMessage());
            System.out.println("  → Kiểm tra internet");
            System.out.println("  → Kiểm tra firewall không chặn port 587");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("✗ Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("════════════════════════════════");
    }
}
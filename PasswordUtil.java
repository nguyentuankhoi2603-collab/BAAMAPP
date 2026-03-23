package BaoCaoCuoiKi;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Tiện ích hash mật khẩu dùng SHA-256 + Salt ngẫu nhiên.
 * (Nếu project đã có thư viện BCrypt thì thay thế hoàn toàn class này.)
 *
 * Format lưu DB: "SALT:HASH"  (cả hai đều Base64)
 */
public class PasswordUtil {

    private static final int SALT_LENGTH = 16; // bytes

    /** Hash mật khẩu plaintext → chuỗi "SALT:HASH" lưu DB */
    public static String hash(String plainText) {
        try {
            SecureRandom sr = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            sr.nextBytes(salt);
            byte[] hash = sha256(salt, plainText);
            return Base64.getEncoder().encodeToString(salt) + ":"
                 + Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hash mật khẩu", e);
        }
    }

    /** So sánh plainText với chuỗi đã lưu trong DB */
    public static boolean verify(String plainText, String stored) {
        try {
            String[] parts = stored.split(":", 2);
            if (parts.length != 2) return false;
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            byte[] actualHash   = sha256(salt, plainText);
            // So sánh constant-time để chống timing attack
            if (expectedHash.length != actualHash.length) return false;
            int diff = 0;
            for (int i = 0; i < expectedHash.length; i++) {
                diff |= expectedHash[i] ^ actualHash[i];
            }
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] sha256(byte[] salt, String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        return md.digest(text.getBytes("UTF-8"));
    }
}

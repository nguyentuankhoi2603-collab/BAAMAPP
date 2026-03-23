package BaoCaoCuoiKi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton quản lý kết nối MySQL.
 * Chỉnh URL, USER, PASS cho đúng môi trường của bạn.
 */
public class DatabaseConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/baam_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "";  // ← đổi thành password của bạn

    private static Connection instance = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                instance = DriverManager.getConnection(URL, USER, PASS);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Không tìm thấy MySQL JDBC Driver: " + e.getMessage());
            }
        }
        return instance;
    }

    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                instance = null;
            }
        } catch (SQLException ignored) {}
    }
    public static void main(String[] args) {
        try {
            Connection con = getConnection();
            System.out.println("✓ Kết nối MySQL thành công!");
            System.out.println("  URL : " + con.getMetaData().getURL());
            System.out.println("  User: " + con.getMetaData().getUserName());
        } catch (SQLException e) {
            System.out.println("✗ Kết nối thất bại: " + e.getMessage());
        }
    }
}

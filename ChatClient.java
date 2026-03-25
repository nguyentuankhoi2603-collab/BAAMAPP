package BaoCaoCuoiKi;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
public class ChatClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ChatClient(String host, int port, MessageListener listener) throws Exception {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in  = new ObjectInputStream(socket.getInputStream());

        // Thread nhận tin
        new Thread(() -> {
            try {
                while (true) {
                    TinNhan tin = (TinNhan) in.readObject();
                    listener.onMessage(tin);
                }
            } catch (Exception e) {}
        }).start();
    }

    public void send(TinNhan tin) throws Exception {
        out.writeObject(tin);
        out.flush();
    }

    public interface MessageListener {
        void onMessage(TinNhan tin);
    }
    public void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package lab4.lab4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServerb5 {
    private static final int PORT = 5005;
    private static final int THREAD_POOL_SIZE = 20;
    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat Server đang khởi động tại port " + PORT + "...");
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                pool.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    public static void broadcast(String message, String senderNickname) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(senderNickname)) {
                entry.getValue().sendMessage(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter outWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
            ) {
                this.writer = outWriter;

                // 1. Bước chọn Nickname duy nhất
                writer.println("SUBMITNICK");
                while (true) {
                    String name = reader.readLine();
                    if (name == null) return;
                    name = name.trim();

                    if (name.isEmpty() || name.contains(" ")) {
                        writer.println("ERR_INVALID_NICKNAME");
                        continue;
                    }

                    // Kiểm tra tính duy nhất (Thread-safe với ConcurrentHashMap)
                    synchronized (clients) {
                        if (!clients.containsKey(name.toLowerCase())) {
                            this.nickname = name;
                            clients.put(name.toLowerCase(), this);
                            break;
                        }
                    }
                    writer.println("ERR_NICKNAME_EXISTS");
                }

                writer.println("NICK_ACCEPTED " + nickname);
                System.out.println("Client đã tham gia: " + nickname + " (" + socket.getRemoteSocketAddress() + ")");
                broadcast("[HỆ THỐNG] " + nickname + " đã tham gia phòng chat.", nickname);

                // 2. Vòng lặp xử lý lệnh từ Client
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.equalsIgnoreCase("QUIT")) {
                        writer.println("BYE");
                        break;
                    } else if (line.equalsIgnoreCase("USERS")) {
                        writer.println("ONLINE_USERS: " + String.join(", ", clients.keySet()));
                    } else if (line.toUpperCase().startsWith("MSG ")) {
                        String msgContent = line.substring(4).trim();
                        if (!msgContent.isEmpty()) {
                            broadcast("[" + nickname + "]: " + msgContent, nickname);
                        }
                    } else {
                        writer.println("ERR_UNKNOWN_COMMAND");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client " + (nickname != null ? nickname : "chưa đặt tên") + " ngắt đột ngột.");
            } finally {
                cleanup();
            }
        }

        public void sendMessage(String msg) {
            if (writer != null) {
                writer.println(msg);
            }
        }

        // Xóa Client khỏi danh sách và thông báo ngắt kết nối
        private void cleanup() {
            if (nickname != null) {
                clients.remove(nickname.toLowerCase());
                System.out.println("Client đã thoát: " + nickname);
                broadcast("[HỆ THỐNG] " + nickname + " đã rời phòng chat.", nickname);
            }
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }
}
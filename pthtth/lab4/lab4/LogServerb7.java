package lab4.lab4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class LogServerb7 {
    private static final int PORT = 5007;
    private static final String LOG_DIR = "data/logs/";
    // Regex kiểm tra clientId chỉ gồm chữ cái, chữ số, dấu gạch ngang (-) và gạch
    // dưới (_)
    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Tạo thư mục lưu log nếu chưa tồn tại
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("Log Server đang lắng nghe tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();

        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            // Bước 1: Xử lý lệnh HELLO clientId
            String firstLine = reader.readLine();
            if (firstLine == null)
                return;

            firstLine = firstLine.trim();
            if (!firstLine.toUpperCase().startsWith("HELLO ")) {
                writer.println("ERR INVALID_PROTOCOL");
                return;
            }

            String clientId = firstLine.substring(6).trim();

            // Kiểm tra clientId hợp lệ theo Regex
            if (!CLIENT_ID_PATTERN.matcher(clientId).matches()) {
                writer.println("ERR INVALID_CLIENT_ID");
                return;
            }

            writer.println("OK READY");
            System.out.println("Client [" + clientId + "] đã kết nối từ " + remoteAddress);

            // Đường dẫn file log: data/logs/clientId.txt
            File logFile = new File(LOG_DIR + clientId + ".txt");

            // Bước 2: Nhận tin nhắn và ghi vào file log (chế độ append)
            try (BufferedWriter logWriter = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase("QUIT")) {
                        writer.println("BYE");
                        break;
                    }

                    // Định dạng dòng nhật ký: [timestamp] [remoteAddress] nội dung
                    String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
                    String logEntry = String.format("[%s] [%s] %s", timestamp, remoteAddress, line);

                    logWriter.write(logEntry);
                    logWriter.newLine();
                    logWriter.flush(); // Đảm bảo ghi ngay ra file

                    writer.println("OK SAVED");
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi xử lý Client (" + remoteAddress + "): " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
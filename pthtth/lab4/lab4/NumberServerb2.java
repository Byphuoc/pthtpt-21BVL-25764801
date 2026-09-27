package lab4.lab4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NumberServerb2 {
    private static final int PORT = 5000;
    private static final String[] DIGIT_NAMES = {
            "không", "một", "hai", "ba", "bốn",
            "năm", "sáu", "bảy", "tám", "chín"
    };

    public static void main(String[] args) {
        System.out.println("Server đổi chữ số đang khởi động tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client đã kết nối: " + socket.getRemoteSocketAddress());

                // Xử lý mỗi client trên một luồng riêng
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Kiểm tra lệnh QUIT
                if (line.equalsIgnoreCase("QUIT")) {
                    writer.println("BYE");
                    break;
                }

                // Xử lý kiểm tra chữ số
                String result = convertDigitToText(line);
                writer.println(result);
            }
        } catch (IOException e) {
            System.err.println("Lỗi xử lý Client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static String convertDigitToText(String input) {
        // Kiểm tra đúng 1 ký tự và nằm trong khoảng '0' -> '9'
        if (input != null && input.length() == 1 && Character.isDigit(input.charAt(0))) {
            int digit = input.charAt(0) - '0';
            return DIGIT_NAMES[digit];
        }
        return "ERR INVALID_DIGIT";
    }
}
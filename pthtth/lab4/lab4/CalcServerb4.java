package lab4.lab4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CalcServerb4 {
    private static final int PORT = 5004;

    public static void main(String[] args) {
        System.out.println("Calc Server đang chạy tại port " + PORT + "...");

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
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("QUIT")) {
                    writer.println("BYE");
                    break;
                }

                String response = processCommand(line.trim());
                writer.println(response);
            }
        } catch (IOException e) {
            System.err.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static String processCommand(String input) {
        if (input.isEmpty()) {
            return "ERR INVALID_FORMAT";
        }

        // Tách câu lệnh theo khoảng trắng (xử lý cả trường hợp nhiều khoảng trắng)
        String[] parts = input.split("\\s+");

        // Kiểm tra đúng định dạng: CALC toán_tử toán_hạng_1 toán_hạng_2 (Đúng 4 thành
        // phần)
        if (parts.length != 4 || !parts[0].equalsIgnoreCase("CALC")) {
            return "ERR INVALID_FORMAT";
        }

        String operator = parts[1];
        double num1, num2;

        // Kiểm tra chuyển đổi toán hạng sang số
        try {
            num1 = Double.parseDouble(parts[2]);
            num2 = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            return "ERR INVALID_NUMBER";
        }

        // Xử lý các toán tử cộng, trừ, nhân, chia
        switch (operator) {
            case "+":
                return formatResult(num1 + num2);
            case "-":
                return formatResult(num1 - num2);
            case "*":
                return formatResult(num1 * num2);
            case "/":
                if (num2 == 0) {
                    return "ERR DIVIDE_BY_ZERO";
                }
                return formatResult(num1 / num2);
            default:
                return "ERR UNSUPPORTED_OPERATOR";
        }
    }

    // Định dạng kết quả: In ra số nguyên nếu không có phần thập phân, ngược lại in
    // số thực
    private static String formatResult(double result) {
        if (result == (long) result) {
            return String.format("OK %d", (long) result);
        } else {
            return String.format("OK %s", result);
        }
    }
}
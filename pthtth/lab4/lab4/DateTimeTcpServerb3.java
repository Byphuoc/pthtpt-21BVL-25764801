package lab4.lab4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTcpServerb3 {
    private static final int PORT = 5002;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        System.out.println("TCP Date/Time Server đang chạy tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            String command;
            while ((command = reader.readLine()) != null) {
                command = command.trim().toUpperCase();
                LocalDateTime now = LocalDateTime.now();

                if (command.equals("DATE")) {
                    writer.println(now.format(DATE_FMT));
                } else if (command.equals("TIME")) {
                    writer.println(now.format(TIME_FMT));
                } else if (command.equals("DATETIME")) {
                    writer.println(now.format(DATETIME_FMT));
                } else if (command.equals("QUIT")) {
                    writer.println("BYE");
                    break;
                } else {
                    writer.println("ERR UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Client TCP ngắt kết nối: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
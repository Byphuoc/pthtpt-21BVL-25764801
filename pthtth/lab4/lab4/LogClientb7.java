package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class LogClientb7 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5007;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Đã kết nối tới Log Server (" + host + ":" + port + ")");
            System.out.print("Nhập câu lệnh HELLO clientId (ví dụ: HELLO client-01): ");

            // Gửi câu lệnh bắt đầu HELLO
            String helloCmd = console.readLine();
            if (helloCmd == null)
                return;

            writer.println(helloCmd);
            String serverResponse = reader.readLine();
            System.out.println("Server: " + serverResponse);

            if (serverResponse == null || !serverResponse.startsWith("OK")) {
                System.out.println("Phiên làm việc bị từ chối.");
                return;
            }

            System.out.println(">>> Bắt đầu gửi nhật ký (Nhập 'QUIT' để thoát):");

            // Vòng lặp gửi tin nhắn
            String message;
            while ((message = console.readLine()) != null) {
                writer.println(message);

                String response = reader.readLine();
                if (response == null)
                    break;

                System.out.println("Server: " + response);

                if (message.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
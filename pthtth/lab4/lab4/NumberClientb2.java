package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class NumberClientb2 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5000;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Đã kết nối tới Server (" + host + ":" + port + ")");
            System.out.println("Nhập một chữ số (0-9) hoặc gõ 'QUIT' để thoát:");

            String line;
            while ((line = console.readLine()) != null) {
                writer.println(line);

                String response = reader.readLine();
                if (response == null) {
                    System.out.println("Server đã đóng kết nối.");
                    break;
                }

                System.out.println("Server: " + response);

                if (line.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
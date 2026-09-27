package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DateTimeTcpClientb3 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5002;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Đã kết nối tới TCP Server (" + host + ":" + port + ")");
            System.out.println("Nhập lệnh (DATE, TIME, DATETIME, QUIT):");

            String input;
            while ((input = console.readLine()) != null) {
                writer.println(input);
                String response = reader.readLine();
                if (response == null)
                    break;

                System.out.println("Server trả về: " + response);
                if (input.equalsIgnoreCase("QUIT"))
                    break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi TCP Client: " + e.getMessage());
        }
    }
}
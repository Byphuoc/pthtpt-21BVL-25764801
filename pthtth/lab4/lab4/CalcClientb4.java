package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class CalcClientb4 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5004;

        try (
                Socket socket = new Socket(host, port);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter writer = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
            System.out.println("Đã kết nối tới Calc Server (" + host + ":" + port + ")");
            System.out.println("Cú pháp: CALC toán_tử toán_hạng_1 toán_hạng_2 (Ví dụ: CALC + 100 200)");
            System.out.println("Gõ 'QUIT' để thoát chương trình.");

            String input;
            while ((input = console.readLine()) != null) {
                writer.println(input);

                String response = reader.readLine();
                if (response == null)
                    break;

                System.out.println("Server: " + response);

                if (input.trim().equalsIgnoreCase("QUIT"))
                    break;
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
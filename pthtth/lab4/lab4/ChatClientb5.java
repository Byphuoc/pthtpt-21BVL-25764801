package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClientb5 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5005;
        try (
            Socket socket = new Socket(host, port);
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = reader.readLine()) != null) {
                        if (serverMsg.equals("SUBMITNICK")) {
                            System.out.print(">>> Nhập Nickname của bạn: ");
                        } else if (serverMsg.equals("ERR_NICKNAME_EXISTS")) {
                            System.out.print(">>> Nickname đã tồn tại! Vui lòng chọn tên khác: ");
                        } else if (serverMsg.equals("ERR_INVALID_NICKNAME")) {
                            System.out.print(">>> Nickname không hợp lệ! Nhập lại: ");
                        } else {
                            System.out.println(serverMsg);
                        }

                        if (serverMsg.equals("BYE")) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("\nĐã ngắt kết nối tới Server.");
                }
            });

            receiveThread.setDaemon(true);
            receiveThread.start();

            // Luồng gửi dữ liệu từ bàn phím
            String input;
            while ((input = console.readLine()) != null) {
                writer.println(input);
                if (input.trim().equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }
}
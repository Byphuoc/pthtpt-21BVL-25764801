package lab4.lab4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UdpEchoClient {
    private static final int MAX_BUFFER_SIZE = 4096; // Giới hạn Buffer

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5001;
        String message = args.length > 2 ? args[2] : "xin chào UDP";

        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        InetAddress server = InetAddress.getByName(host);

        // =========================================================
        // THÊM ĐOẠN NÀY ĐỂ KIỂM TRA KÍCH THƯỚC TRƯỚC KHI GỬI
        // =========================================================
        System.out.println("Kích thước dữ liệu gửi: " + data.length + " bytes");
        if (data.length > MAX_BUFFER_SIZE) {
            System.err.println("LỖI: Kích thước dữ liệu (" + data.length
                    + " bytes) vượt quá giới hạn Buffer cho phép (" + MAX_BUFFER_SIZE + " bytes)!");
            return; // Dừng chương trình, không gửi gói tin
        }
        // =========================================================

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);

            socket.send(new DatagramPacket(data, data.length, server, port));

            byte[] buffer = new byte[MAX_BUFFER_SIZE];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);

            try {
                socket.receive(response);
                String text = new String(response.getData(), response.getOffset(), response.getLength(),
                        StandardCharsets.UTF_8);
                System.out.println("Server: " + text);
            } catch (SocketTimeoutException e) {
                System.err.println("Hết 3 giây nhưng chưa nhận được phản hồi");
            }
        }
    }
}
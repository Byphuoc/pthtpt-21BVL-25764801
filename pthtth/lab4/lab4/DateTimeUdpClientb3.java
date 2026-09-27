package lab4.lab4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class DateTimeUdpClientb3 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5003;

        try (
            DatagramSocket socket = new DatagramSocket();
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))
        ) {
            socket.setSoTimeout(3000);
            InetAddress serverAddress = InetAddress.getByName(host);
            System.out.println("Sẵn sàng gửi UDP tới (" + host + ":" + port + ")");
            System.out.println("Nhập lệnh (DATE, TIME, DATETIME):");

            String input;
            while ((input = console.readLine()) != null) {
                byte[] sendData = input.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
                socket.send(sendPacket);

                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

                try {
                    socket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server trả về: " + response);
                } catch (SocketTimeoutException e) {
                    System.err.println("Lỗi: Hết 3 giây không nhận được phản hồi.");
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP Client: " + e.getMessage());
        }
    }
}
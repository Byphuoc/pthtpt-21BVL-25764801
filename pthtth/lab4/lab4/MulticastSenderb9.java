package lab4.lab4;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MulticastSenderb9 {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5009;

    public static void main(String[] args) {
        System.out.println("Multicast Sender đang khởi động...");
        try (MulticastSocket socket = new MulticastSocket();
             Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            
            System.out.println("Nhập thông điệp cần gửi tới group (gõ 'QUIT' để thoát):");
            while (true) {
                System.out.print("Sender > ");
                String message = scanner.nextLine();
                
                byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
                socket.send(packet);
                
                if ("QUIT".equalsIgnoreCase(message.trim())) {
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi Sender: " + e.getMessage());
        }
    }
}
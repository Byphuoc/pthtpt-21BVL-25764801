package lab4.lab4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUdpServerb3 {
    private static final int PORT = 5003;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MM yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH mm ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd MM yyyy HH mm ss");

    public static void main(String[] args) {
        System.out.println("UDP Date/Time Server đang chạy tại port " + PORT + "...");

        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);

                String command = new String(receivePacket.getData(), 0, receivePacket.getLength(),
                        StandardCharsets.UTF_8).trim().toUpperCase();
                LocalDateTime now = LocalDateTime.now();
                String responseText;

                if (command.equals("DATE")) {
                    responseText = now.format(DATE_FMT);
                } else if (command.equals("TIME")) {
                    responseText = now.format(TIME_FMT);
                } else if (command.equals("DATETIME")) {
                    responseText = now.format(DATETIME_FMT);
                } else {
                    responseText = "ERR UNKNOWN_COMMAND";
                }

                byte[] sendData = responseText.getBytes(StandardCharsets.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length,
                        receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }
}
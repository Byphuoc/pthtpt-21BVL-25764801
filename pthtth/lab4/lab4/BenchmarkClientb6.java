package lab4.lab4;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public class BenchmarkClientb6 {
    private static final String HOST = "localhost";
    private static final int TCP_PORT = 6001;
    private static final int UDP_PORT = 6002;
    private static final int TOTAL_MESSAGES = 1000;
    private static final int MESSAGE_SIZE = 1024;
    private static final int TIMEOUT_MS = 1000;

    public static void main(String[] args) {
        int runs = 5;
        byte[] payload = new byte[MESSAGE_SIZE];
        Arrays.fill(payload, (byte) 'A');

        System.out.println("==========================================================");
        System.out.println(" THỰC NGHIỆM SO SÁNH TCP VÀ UDP (1000 THÔNG ĐIỆP x 1024 BYTES)");
        System.out.println("==========================================================");

        for (int run = 1; run <= runs; run++) {
            System.out.println("\n---> LẦN CHẠY THỨ " + run + " <---");
            long tcpTime = testTcp(payload);

            UdpResult udpResult = testUdp(payload);

            System.out.printf("[TCP] Tổng thời gian: %d ms | Phản hồi: %d/%d\n", tcpTime, TOTAL_MESSAGES,
                    TOTAL_MESSAGES);
            System.out.printf("[UDP] Tổng thời gian: %d ms | Phản hồi: %d/%d | Mất gói: %.2f%%\n",
                    udpResult.time, udpResult.received, TOTAL_MESSAGES,
                    ((TOTAL_MESSAGES - udpResult.received) / (double) TOTAL_MESSAGES) * 100);
        }
    }

    private static long testTcp(byte[] payload) {
        long startTime = System.currentTimeMillis();
        try (Socket socket = new Socket(HOST, TCP_PORT)) {
            socket.setTcpNoDelay(true);
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte[] responseBuffer = new byte[MESSAGE_SIZE];

            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                out.write(payload);
                out.flush();

                int totalRead = 0;
                while (totalRead < MESSAGE_SIZE) {
                    int read = in.read(responseBuffer, totalRead, MESSAGE_SIZE - totalRead);
                    if (read == -1)
                        break;
                    totalRead += read;
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi đo TCP: " + e.getMessage());
        }
        return System.currentTimeMillis() - startTime;
    }

    private static UdpResult testUdp(byte[] payload) {
        int receivedCount = 0;
        long startTime = System.currentTimeMillis();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress address = InetAddress.getByName(HOST);
            byte[] receiveBuffer = new byte[MESSAGE_SIZE];

            for (int i = 0; i < TOTAL_MESSAGES; i++) {
                DatagramPacket sendPacket = new DatagramPacket(payload, payload.length, address, UDP_PORT);
                socket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                try {
                    socket.receive(receivePacket);
                    receivedCount++;
                } catch (SocketTimeoutException ignored) {
                    // Mất gói hoặc timeout
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi đo UDP: " + e.getMessage());
        }

        long totalTime = System.currentTimeMillis() - startTime;
        return new UdpResult(totalTime, receivedCount);
    }

    private static class UdpResult {
        long time;
        int received;

        UdpResult(long time, int received) {
            this.time = time;
            this.received = received;
        }
    }
}
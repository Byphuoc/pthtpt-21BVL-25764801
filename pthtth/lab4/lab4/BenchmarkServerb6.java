package lab4.lab4;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class BenchmarkServerb6 {
    private static final int TCP_PORT = 6001;
    private static final int UDP_PORT = 6002;
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        System.out.println("Đang khởi chạy Benchmark Servers...");
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(() -> handleTcpClient(socket)).start();
                }
            } catch (Exception e) {
                System.err.println("Lỗi TCP Server: " + e.getMessage());
            }
        }).start();
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);
                    DatagramPacket sendPacket = new DatagramPacket(
                            receivePacket.getData(), receivePacket.getLength(),
                            receivePacket.getAddress(), receivePacket.getPort());
                    socket.send(sendPacket);
                }
            } catch (Exception e) {
                System.err.println("Lỗi UDP Server: " + e.getMessage());
            }
        }).start();

        System.out.println("TCP Echo Server sẵn sàng tại port " + TCP_PORT);
        System.out.println("UDP Echo Server sẵn sàng tại port " + UDP_PORT);
    }

    private static void handleTcpClient(Socket socket) {
        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream()) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (Exception ignored) {
        }
    }
}
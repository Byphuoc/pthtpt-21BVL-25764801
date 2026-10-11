package lab4.lab4;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class DiscoveryServerb10 {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int UDP_PORT = 8888;
    private static final int TCP_PORT = 5010;
    private static final String SERVICE_NAME = "LogService";
    private static final String VERSION = "v1.0";

    public static void main(String[] args) {
        // Khởi chạy dịch vụ TCP trong một luồng riêng biệt
        new Thread(() -> startTcpServer()).start();

        // Khởi động UDP Discovery Server lắng nghe gói tin tìm kiếm
        try (MulticastSocket udpSocket = new MulticastSocket(UDP_PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress groupAddress = new InetSocketAddress(group, UDP_PORT);

            // Tìm NetworkInterface thực sự hỗ trợ IPv4 và Multicast
            NetworkInterface netIf = getValidNetworkInterface();
            if (netIf != null) {
                udpSocket.joinGroup(groupAddress, netIf);
                System.out.println("UDP Discovery Server đang lắng nghe trên " + MULTICAST_IP + ":" + UDP_PORT
                        + " (Card: " + netIf.getDisplayName() + ")");
            } else {
                System.err.println("Không tìm thấy card mạng hỗ trợ IPv4 phù hợp cho UDP Multicast.");
                return;
            }

            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);

                String request = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8).trim();
                if ("DISCOVER_SERVICE".equalsIgnoreCase(request)) {
                    InetAddress clientAddress = packet.getAddress();
                    int clientPort = packet.getPort();

                    String responseMsg = String.format("SERVICE %s %d %s", SERVICE_NAME, TCP_PORT, VERSION);
                    byte[] respData = responseMsg.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket respPacket = new DatagramPacket(respData, respData.length, clientAddress,
                            clientPort);
                    udpSocket.send(respPacket);
                    System.out.println("Đã gửi phản hồi discovery tới Client: " + clientAddress.getHostAddress());
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi UDP Server: " + e.getMessage());
        }
    }

    private static void startTcpServer() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            System.out.println("TCP Service đang hoạt động tại port " + TCP_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> {
                    try (PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {
                        out.println("CHÀO MỪNG! Kết nối TCP thành công tới " + SERVICE_NAME);
                    } catch (IOException ignored) {
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi TCP Server: " + e.getMessage());
        }
    }

    private static NetworkInterface getValidNetworkInterface() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual() && ni.supportsMulticast()) {
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            return ni;
                        }
                    }
                }
            }
            // Fallback sang loopback nếu không tìm thấy card vật lý phù hợp
            return NetworkInterface.networkInterfaces()
                    .filter(ni -> {
                        try {
                            return ni.isLoopback() && ni.isUp();
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .findFirst()
                    .orElse(null);
        } catch (Exception ignored) {
        }
        return null;
    }
}
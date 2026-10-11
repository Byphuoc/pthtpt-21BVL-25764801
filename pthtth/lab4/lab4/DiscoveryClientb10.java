package lab4.lab4;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DiscoveryClientb10 {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int UDP_PORT = 8888;
    private static final int TIMEOUT_MS = 3000; // Thiết lập timeout chờ phản hồi trong 3 giây

    public static void main(String[] args) {
        System.out.println("Đang phát yêu cầu DISCOVER_SERVICE qua UDP...");

        Set<String> discoveredKeys = new HashSet<>(); // Dùng để loại bỏ phản hồi trùng lặp
        List<ServerInfo> serverList = new ArrayList<>();

        try (MulticastSocket socket = new MulticastSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            
            String request = "DISCOVER_SERVICE";
            byte[] data = request.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, group, UDP_PORT);
            
            // Gửi yêu cầu tìm kiếm
            socket.send(packet);

            // Cấu hình thời gian timeout cho socket
            socket.setSoTimeout(TIMEOUT_MS);

            byte[] buffer = new byte[1024];
            long endTime = System.currentTimeMillis() + TIMEOUT_MS;

            // Lòng vòng nhận phản hồi từ các server trong khoảng thời gian timeout
            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket respPacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(respPacket);

                    String response = new String(respPacket.getData(), 0, respPacket.getLength(), StandardCharsets.UTF_8).trim();
                    // Lấy địa chỉ IP nguồn từ gói tin phản hồi UDP
                    String serverIp = respPacket.getAddress().getHostAddress();

                    if (response.startsWith("SERVICE")) {
                        String[] parts = response.split("\\s+");
                        if (parts.length >= 4) {
                            String serviceName = parts[1];
                            int tcpPort = Integer.parseInt(parts[2]);
                            String version = parts[3];

                            // Khóa định danh duy nhất (IP + TCP Port) để lọc trùng lặp
                            String uniqueKey = serverIp + ":" + tcpPort;
                            if (!discoveredKeys.contains(uniqueKey)) {
                                discoveredKeys.add(uniqueKey);
                                serverList.add(new ServerInfo(serviceName, serverIp, tcpPort, version));
                                System.out.println("-> Phát hiện Server: [" + serviceName + "] | IP: " + serverIp + " | Port TCP: " + tcpPort + " | Ver: " + version);
                            }
                        }
                    }
                } catch (SocketTimeoutException e) {
                    break; // Hết thời gian chờ phản hồi
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi Client Discovery: " + e.getMessage());
        }

        // Hiển thị danh sách các server tìm được và kết nối TCP
        System.out.println("\n=== TỔNG KẾT: Tìm thấy " + serverList.size() + " server khả dụng ===");
        if (serverList.isEmpty()) {
            System.out.println("Không tìm thấy dịch vụ nào trong thời gian chờ.");
        } else {
            for (int i = 0; i < serverList.size(); i++) {
                ServerInfo info = serverList.get(i);
                System.out.println("\n[" + (i + 1) + "] Đang thiết lập kết nối TCP tới " + info.ip + ":" + info.tcpPort + " (" + info.name + ")...");
                connectTcpServer(info.ip, info.tcpPort);
            }
        }
    }

    private static void connectTcpServer(String ip, int port) {
        try (Socket tcpSocket = new Socket(ip, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream(), StandardCharsets.UTF_8))) {
            String msg = reader.readLine();
            System.out.println("   [Thành công] Nhận phản hồi từ TCP Server: " + msg);
        } catch (IOException e) {
            System.err.println("   [Thất bại] Không thể kết nối TCP: " + e.getMessage());
        }
    }

    static class ServerInfo {
        String name;
        String ip;
        int tcpPort;
        String version;

        public ServerInfo(String name, String ip, int tcpPort, String version) {
            this.name = name;
            this.ip = ip;
            this.tcpPort = tcpPort;
            this.version = version;
        }
    }
}
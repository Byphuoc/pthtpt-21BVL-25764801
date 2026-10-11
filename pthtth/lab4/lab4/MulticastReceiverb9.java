package lab4.lab4;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class MulticastReceiverb9 {
    private static final String MULTICAST_IP = "239.255.0.1";
    private static final int PORT = 5009;

    public static void main(String[] args) {
        String receiverName = args.length > 0 ? args[0] : "Receiver-1";
        System.out.println("[" + receiverName + "] Đang khởi động Receiver...");

        try {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            InetSocketAddress groupAddress = new InetSocketAddress(group, PORT);

            // Tìm NetworkInterface thực sự hỗ trợ IPv4 và Multicast
            NetworkInterface netIf = null;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isUp() && !ni.isLoopback() && !ni.isVirtual() && ni.supportsMulticast()) {
                    // Kiểm tra xem card mạng có chứa địa chỉ IPv4 hay không
                    Enumeration<InetAddress> addresses = ni.getInetAddresses();
                    boolean hasIPv4 = false;
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            hasIPv4 = true;
                            break;
                        }
                    }
                    if (hasIPv4) {
                        netIf = ni;
                        break;
                    }
                }
            }

            // Nếu không tìm thấy card mạng vật lý phù hợp, sử dụng Loopback (localhost) để
            // test an toàn
            if (netIf == null) {
                netIf = NetworkInterface.getByName("lo");
                if (netIf == null) {
                    netIf = NetworkInterface.networkInterfaces().filter(ni -> {
                        try {
                            return ni.isLoopback() && ni.isUp();
                        } catch (Exception e) {
                            return false;
                        }
                    }).findFirst().orElse(null);
                }
            }

            if (netIf != null) {
                System.out.println("[" + receiverName + "] Sử dụng NetworkInterface: " + netIf.getDisplayName());
            } else {
                throw new Exception("Không tìm thấy NetworkInterface hỗ trợ IPv4 phù hợp!");
            }

            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                socket.joinGroup(groupAddress, netIf);
                System.out.println("[" + receiverName + "] Đã tham gia nhóm multicast " + MULTICAST_IP + ":" + PORT);
                System.out.println("[" + receiverName + "] Đang chờ nhận thông điệp...");

                byte[] buffer = new byte[1024];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    System.out.println("[" + receiverName + "] Nhận được thông điệp: " + msg);

                    if ("QUIT".equalsIgnoreCase(msg.trim())) {
                        break;
                    }
                }

                socket.leaveGroup(groupAddress, netIf);
                System.out.println("[" + receiverName + "] Đã rời khỏi nhóm multicast thành công.");
            }
        } catch (Exception e) {
            System.err.println("[" + receiverName + "] Lỗi Receiver: " + e.getMessage());
        }
    }
}
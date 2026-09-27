package lab4.lab4;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class HostUriInspectorb1 {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Lỗi: Thiếu tham số truyền vào!");
            System.out.println("Cú pháp: java lab4.lab4.HostUriInspector <hostname> <URI>");
            return;
        }

        String hostname = args[0];
        String uriString = args[1];

        System.out.println("==================================================");
        System.out.println("=== THÔNG TIN HOSTNAME: " + hostname);
        System.out.println("==================================================");

        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            for (InetAddress addr : addresses) {
                System.out.println("- IP Address: " + addr.getHostAddress());

                if (addr instanceof Inet4Address) {
                    System.out.println("  Loại IP: IPv4");
                } else if (addr instanceof Inet6Address) {
                    System.out.println("  Loại IP: IPv6");
                }

                System.out.println("  Loopback: " + addr.isLoopbackAddress());
                System.out.println("  Site Local: " + addr.isSiteLocalAddress());
                System.out.println("--------------------------------------------------");
            }
        } catch (UnknownHostException e) {
            System.err.println("Lỗi: Hostname không phân giải được -> " + hostname);
        }

        System.out.println("\n==================================================");
        System.out.println("=== THÔNG TIN URI: " + uriString);
        System.out.println("==================================================");

        // 2. Phân tích URI bằng java.net.URI
        try {
            URI uri = new URI(uriString);
            System.out.println("- Scheme:   " + uri.getScheme());
            System.out.println("- Host:     " + uri.getHost());
            System.out.println("- Port:     " + (uri.getPort() == -1 ? "Mặc định (Không chỉ định)" : uri.getPort()));
            System.out.println("- Path:     " + uri.getPath());
            System.out.println("- Query:    " + uri.getQuery());
            System.out.println("- Fragment: " + uri.getFragment());
        } catch (URISyntaxException e) {
            System.err.println("Lỗi: URI sai định dạng cú pháp -> " + e.getMessage());
        }
    }
}
package lab4.lab4;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class FileClientb8 {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 5008;

        String filePathInput = args.length > 2 ? args[2] : "test_tiengviet.txt";

        File fileToSend = new File(filePathInput);
        if (!fileToSend.exists() || !fileToSend.isFile()) {
            System.err.println("Không tìm thấy file để gửi tại: " + fileToSend.getAbsolutePath());
            return;
        }

        try {
            String fileHash = calculateSHA256(fileToSend);
            long fileSize = fileToSend.length();
            String fileName = fileToSend.getName();

            System.out.println("Đang kết nối tới Server " + host + ":" + port + "...");
            try (Socket socket = new Socket(host, port);
                 OutputStream out = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
                 InputStream in = socket.getInputStream()) {

                System.out.println("Đang gửi thông tin file...");
                writer.println(fileName);
                writer.println(fileSize);
                writer.println(fileHash);

                System.out.println("Đang truyền dữ liệu nội dung file...");
                try (FileInputStream fis = new FileInputStream(fileToSend)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                }

                // Nhận phản hồi từ server
                String serverResponse = readLine(in);
                System.out.println("Phản hồi từ Server: " + serverResponse);

                if ("OK".equals(serverResponse)) {
                    System.out.println("Truyền file thành công và toàn vẹn dữ liệu được đảm bảo!");
                } else {
                    System.out.println("Lỗi truyền file từ phía server: " + serverResponse);
                }
            }

        } catch (Exception e) {
            System.err.println("Lỗi Client: " + e.getMessage());
        }
    }

    private static String readLine(InputStream in) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                bout.write(b);
            }
        }
        if (b == -1 && bout.size() == 0) {
            return null;
        }
        return new String(bout.toByteArray(), StandardCharsets.UTF_8);
    }

    private static String calculateSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[4096];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
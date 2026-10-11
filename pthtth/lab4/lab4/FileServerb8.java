package lab4.lab4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileServerb8 {
    private static final int PORT = 5008;
    private static final String UPLOAD_DIR = "data/";

    public static void main(String[] args) {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("File Server đang lắng nghe tại port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Lỗi Server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        String remoteAddress = socket.getRemoteSocketAddress().toString();
        System.out.println("Client kết nối từ: " + remoteAddress);

        try (
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true)) {
            // Đọc metadata dòng bằng dòng an toàn từ InputStream gốc
            String fileNameInput = readLine(in);
            String fileSizeStr = readLine(in);
            String clientHash = readLine(in);

            if (fileNameInput == null || fileSizeStr == null || clientHash == null) {
                writer.println("ERR INVALID_METADATA");
                return;
            }

            long fileSize = Long.parseLong(fileSizeStr.trim());

            // Chống Path Traversal
            Path safeName = Paths.get(fileNameInput).getFileName();
            if (safeName == null || safeName.toString().isEmpty()) {
                writer.println("ERR INVALID_FILENAME");
                return;
            }

            File targetFile = new File(UPLOAD_DIR, safeName.toString());
            System.out.println(
                    "Đang nhận file: " + safeName + " (" + fileSize + " bytes) vào " + targetFile.getAbsolutePath());

            // Đọc đúng số lượng byte đã khai báo
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[4096];
                long totalRead = 0;
                while (totalRead < fileSize) {
                    int bytesToRead = (int) Math.min(buffer.length, fileSize - totalRead);
                    int read = in.read(buffer, 0, bytesToRead);
                    if (read == -1) {
                        break;
                    }
                    fos.write(buffer, 0, read);
                    totalRead += read;
                }
            }

            if (targetFile.length() != fileSize) {
                writer.println("ERR INCOMPLETE_FILE");
                return;
            }

            // Tính lại SHA-256 đối chiếu
            String serverHash = calculateSHA256(targetFile);

            if (serverHash.equalsIgnoreCase(clientHash.trim())) {
                writer.println("OK");
                System.out.println("Giao dịch thành công. File hợp lệ, hash khớp.");
            } else {
                writer.println("ERR HASH_MISMATCH");
                System.err.println("Lỗi: Hash không khớp!");
            }

        } catch (Exception e) {
            System.err.println("Lỗi xử lý client (" + remoteAddress + "): " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
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

    private static String calculateSHA256(File file) throws IOException, NoSuchAlgorithmException {
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
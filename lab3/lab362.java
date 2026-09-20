package lab3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class lab362 {
    public static void main(String[] args) {
        Path file = Path.of("data", "ghi_chu.txt");

        try {
            Files.createDirectories(file.getParent());

            System.out.println(file.toAbsolutePath());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    file,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {

                writer.write("Java I/O làm việc với các luồng dữ liệu.");
                writer.newLine();
                writer.write("BufferedWriter giúp ghi văn bản hiệu quả.");
                writer.newLine();
                writer.write("UTF-8 hỗ trợ tiếng Việt ổn định.");
                writer.newLine();
            }

            try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                String line;
                int number = 1;
                while ((line = reader.readLine()) != null) {
                    System.out.printf("%d. %s%n", number++, line);
                }
            }

            try (BufferedReader readerWrongCharset = Files.newBufferedReader(file, StandardCharsets.ISO_8859_1)) {
                String line;
                int number = 1;
                while ((line = readerWrongCharset.readLine()) != null) {
                    System.out.printf("%d. %s%n", number++, line);
                }
            }

        } catch (IOException e) {
            System.err.println("Lỗi xử lý tệp " + file + ": " + e.getMessage());
        }
    }
}

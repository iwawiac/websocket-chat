package pl.sages.chat.client;

import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static Path getFilePath(String fileName, List<Path> fileList) {
        for (Path file : fileList) {
            if (file.getFileName().toString().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public static List<Path> listFiles(Boolean printToConsole) {
        List<Path> fileList = new ArrayList<>();
        Path folder = Paths.get(System.getProperty("user.dir"));
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
                for (Path file : stream) {
                    if (Files.isRegularFile(file)) {
                        fileList.add(file);
                        if (printToConsole) {
                            System.out.println(file.getFileName().toString());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileList;
    }

    public static ByteBuffer readFileIntoByteBuffer(Path filePath) throws IOException {
        // Read the contents of the file into the byte buffer
        byte[] bytes = Files.readAllBytes(filePath);
        return ByteBuffer.wrap(bytes);
    }

    public static void sendBinary(WebSocket webSocket, String input) {
        String[] parts = input.split(" ");
        if (parts.length != 2) {
            System.out.println("please provide one argument for the fileName");
            System.out.println("current files available are: ");
            listFiles(true);
            return;
        }

        String fileName = parts[1];
        Path filePath = getFilePath(fileName, listFiles(false));

        if (filePath == null) {
            System.out.println("no such file found: " + parts[1]);
            System.out.println("current files available are: ");
            listFiles(true);
            return;
        }

        try {
            ByteBuffer buffer = readFileIntoByteBuffer(filePath);
            webSocket.sendText("/send_file " + fileName, true);
            webSocket.sendBinary(buffer, true).join();
            System.out.println("file sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package pl.sages.chat.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileHandlerUtils {
    public static void saveFileFromClientOnServer(String fileName, byte[] decodedBinaryFromClient) {
        Path path = Paths.get("server_downloaded_" + fileName);
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.wrap(decodedBinaryFromClient);
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

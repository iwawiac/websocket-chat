package pl.sages.chat.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ClientFileUtils {
    public static File getFile(String fileName, List<File> fileList) {
        for (File file: fileList) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public static List<File> listFiles(Boolean printToConsole) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(System.getProperty("user.dir"));
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    fileList.add(file);
                    if(printToConsole) {
                        System.out.println(file.getName());
                    }
                }
            }
        }
        return fileList;
    }

    public static ByteBuffer readFileIntoByteBuffer(File file) throws IOException {
        // Open a file channel to read the file
        FileChannel channel = new FileInputStream(file).getChannel();
        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        // Read the contents of the file into the byte buffer
        channel.read(buffer);
        channel.close();
        buffer.rewind();
        return buffer;
    }

    public static void sendBinary(WebSocket webSocket, String input) {
        String [] parts = input.split(" ");
        if(parts.length!=2) {
            System.out.println("please provide one argument for the fileName");
            System.out.println("current files available are: ");
            listFiles(true);
            return;
        }

        String fileName = parts[1];
        File file = getFile(fileName, listFiles(false));

        if(file == null) {
            System.out.println("no such file found: " + parts[1]);
            System.out.println("current files available are: ");
            listFiles(true);
            return;
        }

        try{
            ByteBuffer buffer = readFileIntoByteBuffer(file);
            webSocket.sendText("/send_file "+ fileName, true);
            webSocket.sendBinary(buffer, true).join();
            System.out.println("file sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

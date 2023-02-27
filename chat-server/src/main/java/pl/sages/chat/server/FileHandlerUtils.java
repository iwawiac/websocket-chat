package pl.sages.chat.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandlerUtils {

    public static void saveFileFromClientOnServer(String fileName, byte [] decodedBinaryFromClient) {
        File file = new File("server_file" + fileName);
        try{
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(decodedBinaryFromClient);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

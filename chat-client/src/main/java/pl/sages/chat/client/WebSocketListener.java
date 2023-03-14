package pl.sages.chat.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

public class WebSocketListener implements WebSocket.Listener {

    private String tempFileName = "temp";
    private String userName;

    public WebSocketListener(String userName) {
        this.userName = userName;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        if(data.toString().contains("/sending_file")) {
            tempFileName = data.toString().split(" ")[2];
            System.out.println("downloading file: "+ tempFileName);
        } else {
            System.out.println(data);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        File file = new File(System.getProperty("user.dir"), userName+"_downloaded_" + tempFileName);
        try(FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            while (data.hasRemaining()) {
                fileOutputStream.getChannel().write(data);
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

}

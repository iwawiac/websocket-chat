package pl.sages.chat.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;

public class ChatClient {
    
    static String userName;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat");
        userName = scanner.nextLine();
        
        WebSocketListener webSocketListener = new WebSocketListener(userName);
        WebSocket webSocket = createClientWebsocket(webSocketListener);
        
        webSocket.sendText(userName, true).join();
        System.out.println("You are now in the group chat, type /help for the list of available commands");

        // running on a separate thread in order not to block on .take() operations
        ClientFileWatcher clientFileWatcher = new ClientFileWatcher();
        Thread fileWatcherThread = new Thread(clientFileWatcher);
        fileWatcherThread.start();

        while (!webSocket.isOutputClosed() && !webSocket.isInputClosed()) {
            String input = scanner.nextLine();

            if (input.contains("/send_file")) {
                FileUtils.sendBinary(webSocket, input);
            } else {
                webSocket.sendText(input, true).join();
            }
        }
    }

    private static WebSocket createClientWebsocket(WebSocketListener webSocketListener) {
        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Builder webSocketBuilder = client.newWebSocketBuilder();
        WebSocket webSocket = webSocketBuilder.buildAsync(URI.create("ws://localhost:8080/chat"), webSocketListener).join();
        var shutdownHook = Thread.ofPlatform().unstarted(() -> webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Exiting").join());
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        return webSocket;
    }
}
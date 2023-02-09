package pl.sages.chat.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;


public class ChatClient {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat");
        String userName = scanner.nextLine();

        var webSocketListener = new WebSocket.Listener() {

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                System.out.println(data);
                return WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public void onOpen(WebSocket webSocket) {
                WebSocket.Listener.super.onOpen(webSocket);
            }
        };

        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Builder webSocketBuilder = client.newWebSocketBuilder();
        WebSocket webSocket =
                webSocketBuilder.buildAsync(URI.create("ws://localhost:8080/chat"), webSocketListener).join();

        var shutdownHook = Thread.ofPlatform()
                .unstarted(() -> webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Exiting").join());
        Runtime.getRuntime().addShutdownHook(shutdownHook);




        webSocket.sendText(userName, false).join();
        System.out.println("You are now in the group chat, type /help for the list of available commands");

        while (true) {
           String input = scanner.nextLine();
           webSocket.sendText(input, false).join();
        }
    }
}
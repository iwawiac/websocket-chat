package pl.sages.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatServer {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(8080);
        System.out.println("""
                    Server has started on 127.0.0.1:8080
                    Waiting for a connection…
                    """);

        while(!server.isClosed()) {
            Socket client = server.accept();
            validateWebsocketConnection(client.getInputStream(), client.getOutputStream());
            ClientService clientService = new ClientService(client);
            Thread thread = new Thread(clientService);
            thread.start();
        }
    }


    private static void validateWebsocketConnection(InputStream inputStream, OutputStream outputStream) throws IOException, NoSuchAlgorithmException {
        Scanner s = new Scanner(inputStream, StandardCharsets.UTF_8);
        String data = s.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);
        if (get.find()) {
            handshake(data, outputStream);
        } else {
            throw new RuntimeException("Not a proper websocket protocol!");
        }
    }

    private static void handshake(String data, OutputStream out) throws IOException, NoSuchAlgorithmException {
        Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
        match.find();
        byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                + "Connection: Upgrade\r\n"
                + "Upgrade: websocket\r\n"
                + "Sec-WebSocket-Accept: "
                + EncoderUtils.encodeHashAndBase64(match)
                + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        out.write(response, 0, response.length);
    }

}

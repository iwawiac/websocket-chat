package pl.sages.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
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
        try (server) {
            try (Socket client = server.accept()) {
                System.out.println("A client connected!");
                try (InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream()) {

                    validateWebsocketConnection(in, out);

                    while (true) {
                        System.out.println("Waiting for a message!");
                        readResponse(in);
                    }
                }
            }
        }
    }

    private static void readResponse(InputStream in) throws IOException {
        int opcode = in.read(); // it should always be 0x1 (for now)
        int messageLength = in.read() - 128;
        messageLength = switch (messageLength) {
            case 127 -> throw new RuntimeException("Very long message!");
            case 126 -> throw new RuntimeException("Longer message!");
            default -> messageLength;
        };
        byte[] decodingKey = new byte[] { (byte) in.read(), (byte) in.read(), (byte) in.read(), (byte) in.read() };
        byte[] encodedMessage = in.readNBytes(messageLength);
        var decodedMessage = new String(decodeBytes(encodedMessage, decodingKey));
        System.out.println("Incoming message: " + decodedMessage);
        if (decodedMessage.contains("\u0003�Exiting")) {
            System.out.println("Client has disconnected!");
            System.exit(0);
        }
    }

    private static byte[] decodeBytes(byte[] encodedMessage, byte[] decodingKey) {
        byte[] decoded = new byte[encodedMessage.length];
        for (int i = 0; i < encodedMessage.length; i++) {
            decoded[i] = (byte) (encodedMessage[i] ^ decodingKey[i & 0x3]);
        }
        return decoded;
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
                + encodeHashAndBase64(match)
                + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
        out.write(response, 0, response.length);
    }

    private static String encodeHashAndBase64(Matcher match) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-1")
                .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                .getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

}

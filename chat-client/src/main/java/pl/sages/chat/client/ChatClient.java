package pl.sages.chat.client;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;


public class ChatClient {

    static String tempFileName = "temp";
    static String userName;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat");
        userName = scanner.nextLine();

        var webSocketListener = new WebSocket.Listener() {

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
            public void onOpen(WebSocket webSocket) {
                WebSocket.Listener.super.onOpen(webSocket);
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
        };

        HttpClient client = HttpClient.newHttpClient();
        WebSocket.Builder webSocketBuilder = client.newWebSocketBuilder();
        WebSocket webSocket =
                webSocketBuilder.buildAsync(URI.create("ws://localhost:8080/chat"), webSocketListener).join();

        var shutdownHook = Thread.ofPlatform()
                .unstarted(() -> webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Exiting").join());
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        webSocket.sendText(userName, true).join();
        System.out.println("You are now in the group chat, type /help for the list of available commands");

        while (!webSocket.isOutputClosed() && !webSocket.isInputClosed()) {
            String input = scanner.nextLine();
            if (input.contains("/send_file")) {
                String [] parts = input.split(" ");
                if(parts.length!=2) {
                    System.out.println("please provide one argument for the fileName");
                    System.out.println("current files available are: ");
                    listFiles(true);
                    continue;
                }

                String fileName = parts[1];
                File file = getFile(fileName, listFiles(false));

                if(file == null) {
                    System.out.println("no such file found: " + parts[1]);
                    System.out.println("current files available are: ");
                    listFiles(true);
                    continue;
                }

                try{
                    ByteBuffer buffer = readFileIntoByteBuffer(file);
                    webSocket.sendText("/send_file "+ fileName, true);
                    webSocket.sendBinary(buffer, true).join();
                    System.out.println("file sent");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                webSocket.sendText(input, true).join();
            }
        }
    }

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


}
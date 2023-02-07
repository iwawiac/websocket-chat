package pl.sages.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ClientService implements Runnable {

    public static List<ClientService> clientServiceList = new LinkedList<>();
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public ClientService(Socket clientSocket) {
        try{

            this.clientSocket = clientSocket;
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            clientServiceList.add(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (clientSocket.isConnected()) {
            String messageFromClient;
            try{
                messageFromClient = readResponse(this.inputStream);
                broadcastMessageToClients(messageFromClient);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


    private static String readResponse(InputStream in) throws IOException {
        int opcode = in.read(); // it should always be 0x1 (for now)
        int messageLength = in.read() - 128;
        messageLength = switch (messageLength) {
            case 127 -> throw new RuntimeException("Very long message!");
            case 126 -> throw new RuntimeException("Longer message!");
            default -> messageLength;
        };
        byte[] decodingKey = new byte[] { (byte) in.read(), (byte) in.read(), (byte) in.read(), (byte) in.read() };
        byte[] encodedMessage = in.readNBytes(messageLength);
        var decodedMessage = new String(DecoderUtils.decodeBytes(encodedMessage, decodingKey));
        System.out.println("Incoming message: " + decodedMessage);

        if (decodedMessage.contains("\u0003�Exiting")) {
            System.out.println("Client has disconnected!");
            System.exit(0);
        }
        return decodedMessage;
    }

    private void broadcastMessageToClients(String messageToBroadcast) {

        byte[] dataframe = WebSocketDataFrame.createDataFrame(messageToBroadcast);
        for (ClientService clientService : clientServiceList) {
            if(clientService != this) {
                try{
                    clientService.outputStream.write(dataframe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

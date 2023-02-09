package pl.sages.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientService implements Runnable {
    private Socket clientSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private String clientUserName;
    private Channel activeChannel = GroupChannel.getInstance();

    public ClientService(Socket clientSocket) {
        try{
            this.clientSocket = clientSocket;
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            this.clientUserName = readResponse(this.inputStream);
            GroupChannel.getInstance().addClientServiceToChannel(this);
            System.out.println(addTimeStampToTheMessage(this.clientUserName) + " has entered the group chat" );
            broadcastMessageToClients("Server: " + this.clientUserName + " has entered the group chat", false);
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
                if(messageFromClient.startsWith("/")) {
                    processMenuCommand(messageFromClient);
                } else{
                    broadcastMessageToClients(clientUserName +": " + messageFromClient, true);
                }
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

        if (decodedMessage.contains("\u0003�Exiting")) {
            System.out.println("Client has disconnected!");
            System.exit(0);
        }
        return decodedMessage;
    }

    private void broadcastMessageToClients(String messageToBroadcast, boolean addChannelName) {
        if (addChannelName == true) {
            messageToBroadcast = addTimeStampToTheMessage(addChannelNameToTheMessage(messageToBroadcast));
        } else {
            messageToBroadcast = addTimeStampToTheMessage(messageToBroadcast);
        }
        byte[] dataframe = WebSocketDataFrame.createDataFrame(messageToBroadcast);
        for (ClientService clientService : activeChannel.getClientServiceList()) {
            if(clientService != this) {
                try{
                    clientService.outputStream.write(dataframe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void replyFromServerToClient(String messageToSend) {
        try {
            messageToSend = addTimeStampToTheMessage(messageToSend);
            byte [] dataframe = WebSocketDataFrame.createDataFrame(messageToSend);
            this.outputStream.write(dataframe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String addTimeStampToTheMessage(String originalMessage) {
        LocalDateTime current = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = current.format(formatter);
        return formattedTime + " " + originalMessage;
    }

    private String addChannelNameToTheMessage(String originalMessage) {
        String channelName = this.activeChannel.getName();
        return "(" + channelName + " channel) " + originalMessage;
    }

    private void processMenuCommand (String commandFromClient) {

        String [] parts = commandFromClient.split(" ");
        String command = parts[0];
        System.out.println("command: "+command);

        switch (command) {
            case "/help":
                replyFromServerToClient(MenuUtils.helpMessage);
                break;
            case "/create_channel":
                String privateChannelName = parts[1];
                PrivateChannel newPrivateChannel = new PrivateChannel(privateChannelName);
                newPrivateChannel.addClientServiceToChannel(this);
                this.activeChannel = newPrivateChannel;
                replyFromServerToClient(MenuUtils.createdAndEnteredPrivateChannel + newPrivateChannel.getName());
                break;
            case "/join_channel":
                privateChannelName = parts[1];
                this.activeChannel = PrivateChannel.findChannelByName(privateChannelName);
                this.activeChannel.addClientServiceToChannel(this);
                replyFromServerToClient(MenuUtils.enteredPrivateChannel + privateChannelName);
                break;
            case "/leave_channel":
                privateChannelName = parts[1];
                Channel channelToLeave = PrivateChannel.findChannelByName(privateChannelName);
                channelToLeave.removeClientServiceFromChannel(this);
                this.activeChannel = GroupChannel.getInstance();
                replyFromServerToClient(MenuUtils.leftPrivateChannel + privateChannelName);
                replyFromServerToClient(MenuUtils.backToGroupChannel);
                break;
            case "/switch_channel":
                String channelName = parts[1];
                if(channelName.equals("group")) {
                    this.activeChannel = GroupChannel.getInstance();
                    replyFromServerToClient(MenuUtils.backToGroupChannel);
                } else {
                    Channel channelToSwitchTo = PrivateChannel.findChannelByName(channelName);
                    this.activeChannel = channelToSwitchTo;
                    replyFromServerToClient(MenuUtils.activeChannel + channelName);
                }
                break;
            case "/send_file": // TODO 1 add functionality
                replyFromServerToClient("you are trying to send a file, this functionality is not implemented yet");
                break;

            default:
                replyFromServerToClient(MenuUtils.menuCommandNotFound);
                break;
        }
    }

}

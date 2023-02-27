package pl.sages.chat.server;

import java.io.*;
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
        try {
            this.clientSocket = clientSocket;
            this.inputStream = clientSocket.getInputStream();
            this.outputStream = clientSocket.getOutputStream();
            this.clientUserName = new String(ReaderUtils.readResponse(this.inputStream));
            GroupChannel.getInstance().addClientServiceToChannel(this);
            System.out.println(addTimeStampToTheMessage(this.clientUserName) + " has entered the group chat");
            broadcastMessageToClients("Server: " + this.clientUserName + " has entered the group chat", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setActiveChannel(Channel activeChannel) {
        this.activeChannel = activeChannel;
    }

    public Channel getActiveChannel() {
        return activeChannel;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void run() {
        while (clientSocket.isConnected()) {
            String messageFromClient;
            try {
                messageFromClient = new String(ReaderUtils.readResponse(this.inputStream));
                if (messageFromClient.startsWith("/")) {
                    MenuUtils.processMenuCommand(messageFromClient, this);
                } else {
                    broadcastMessageToClients(clientUserName + ": " + messageFromClient, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void broadcastMessageToClients(String messageToBroadcast, boolean addChannelName) {
        if (addChannelName) {
            messageToBroadcast = addTimeStampToTheMessage(addChannelNameToTheMessage(messageToBroadcast));
        } else {
            messageToBroadcast = addTimeStampToTheMessage(messageToBroadcast);
        }
        byte[] dataframe = WebSocketDataFrame.createDataFrame(messageToBroadcast.getBytes(), false);
        for (ClientService clientService : activeChannel.getClientServiceList()) {
            if (clientService != this) {
                try {
                    clientService.outputStream.write(dataframe);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void broadcastBinaryToClients(byte [] binaryToBroadcast) {
        byte [] dataframe = WebSocketDataFrame.createDataFrame(binaryToBroadcast, true);
        for (ClientService clientService : activeChannel.getClientServiceList()) {
            if(clientService != this) {
                try {
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
            byte[] dataframe = WebSocketDataFrame.createDataFrame(messageToSend.getBytes(), false);
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
}

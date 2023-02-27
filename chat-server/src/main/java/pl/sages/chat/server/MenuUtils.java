package pl.sages.chat.server;

// commands client can use:
// /help
// /create_channel channelName

// first user input is checked for "/" char and if so, the command typed by the user is returned and passed to switch statement

import java.io.IOException;

public class MenuUtils {
    public static final String helpMessage = "Server: this is a sample help message";
    public static final String menuCommandNotFound = "Server: menu command not found, enter /help for the list of available commands";
    public static final String createdAndEnteredPrivateChannel = "Server: you have created and entered a private channel, channel name: ";
    public static final String enteredPrivateChannel = "Server: you have entered a private channel, channel name: ";
    public static final String leftPrivateChannel = "Server: you have left the private channel, channel name: ";
    public static final String activeChannel = "Server: you are now writing in the channel: ";
    public static final String backToGroupChannel = "Server: you are back to the group channel";

    public static void processMenuCommand(String commandFromClient, ClientService clientService) {

        String[] parts = commandFromClient.split(" ");
        String command = parts[0];
        System.out.println("command: " + command);

        switch (command) {
            case "/help":
                clientService.replyFromServerToClient(MenuUtils.helpMessage);
                break;
            case "/create_channel":
                String privateChannelName = parts[1];
                PrivateChannel newPrivateChannel = new PrivateChannel(privateChannelName);
                newPrivateChannel.addClientServiceToChannel(clientService);
                clientService.setActiveChannel(newPrivateChannel);
                clientService.replyFromServerToClient(MenuUtils.createdAndEnteredPrivateChannel + newPrivateChannel.getName());
                break;
            case "/join_channel":
                privateChannelName = parts[1];
                clientService.setActiveChannel(PrivateChannel.findChannelByName(privateChannelName));
                clientService.getActiveChannel().addClientServiceToChannel(clientService);
                clientService.replyFromServerToClient(MenuUtils.enteredPrivateChannel + privateChannelName);
                break;
            case "/leave_channel":
                privateChannelName = parts[1];
                Channel channelToLeave = PrivateChannel.findChannelByName(privateChannelName);
                channelToLeave.removeClientServiceFromChannel(clientService);
                clientService.setActiveChannel(GroupChannel.getInstance());
                clientService.replyFromServerToClient(MenuUtils.leftPrivateChannel + privateChannelName);
                clientService.replyFromServerToClient(MenuUtils.backToGroupChannel);
                break;
            case "/switch_channel":
                String channelName = parts[1];
                if (channelName.equals("group")) {
                    clientService.setActiveChannel(GroupChannel.getInstance());
                    clientService.replyFromServerToClient(MenuUtils.backToGroupChannel);
                } else {
                    Channel channelToSwitchTo = PrivateChannel.findChannelByName(channelName);
                    clientService.setActiveChannel(channelToSwitchTo);
                    clientService.replyFromServerToClient(MenuUtils.activeChannel + channelName);
                }
                break;
            case "/send_file":
                try{
                    String fileName = parts[1];
                    byte [] binaryFromClient = ReaderUtils.readResponse(clientService.getInputStream());
                    FileHandlerUtils.saveFileFromClientOnServer(fileName, binaryFromClient);
                    clientService.broadcastMessageToClients("/sending_file " + fileName, false);
                    clientService.broadcastBinaryToClients(binaryFromClient);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // get file name and extension
                // save file to server

                // broadcast file to clients

                break;

            default:
                clientService.replyFromServerToClient(MenuUtils.menuCommandNotFound);
                break;
        }
    }

}

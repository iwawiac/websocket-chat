package pl.sages.chat.server;

import java.io.IOException;

public class MenuUtils {
    public static final String helpMessage = """
            <HELP MESSAGE>
            Type in any text and it will be sent to all users subscribed to your active channel
            Your initial active channel is general group channel. Every user is subscribed to it.
            Every menu command should start with / and only menu commands start with /
             You can use the following commands to interact with the program:
            /help - receive help message
            /create_channel channelName - creates a private channel and sets it to your active channel, you have to provide one argument for channelName
            /join_channel channelName - subscribes you to an existing private channel and sets it as active channel
            /switch_channel channelName - switches your active channel to channelName, can be used to switch back to group channel
            /leave_channel channelName - unsubscribes you from channelName and switches your active channel to group channel
            /active - prints active channel you are currently writing to
            /send_file fileName - sends a file from websocket-chat directory to every user subscribed to your active channel
            """;
    public static final String menuCommandNotFound = "Server: menu command not found, enter /help for the list of available commands";
    public static final String createdAndEnteredPrivateChannel = "Server: you have created and entered a private channel, channel name: ";
    public static final String subscribedPrivateChannel = "Server: you have subscribed to a private channel, channel name: ";
    public static final String leftPrivateChannel = "Server: you have left the private channel, channel name: ";
    public static final String activeChannel = "Server: you are now writing in the channel: ";
    public static final String backToGroupChannel = "Server: you are back to the group channel";
    public static final String invalidNumberOfArgumentsForChannelCommand = "Server: invalid number of arguments. Please provide channel name after channel-related command, no spaces allowed \n(for example </create_channel testChannel>) ";
    public static final String channelNameTaken = "Server: this channel name is already taken, please pick a different name for the new channel";
    public static final String channelNotFound = "Server: unable to find private channel with the name specified bu user. Please check whether the name is correct";

    public static void processMenuCommand(String commandFromClient, ClientService clientService) {

        String[] partsFromClientMenuCommand = commandFromClient.split(" ");
        String command = partsFromClientMenuCommand[0];

        if (command.contains("_channel") && partsFromClientMenuCommand.length != 2) {
            clientService.replyFromServerToClient(MenuUtils.invalidNumberOfArgumentsForChannelCommand);
            return;
        }

        switch (command) {
            case "/help":
                clientService.replyFromServerToClient(MenuUtils.helpMessage);
                break;
            case "/create_channel":
                String privateChannelName = partsFromClientMenuCommand[1];
                if (PrivateChannel.findChannelByName(privateChannelName) != null) {
                    clientService.replyFromServerToClient(channelNameTaken);
                    break;
                }
                PrivateChannel newPrivateChannel = new PrivateChannel(privateChannelName);
                newPrivateChannel.addClientServiceToChannel(clientService);
                clientService.setActiveChannel(newPrivateChannel);
                clientService.replyFromServerToClient(MenuUtils.createdAndEnteredPrivateChannel + newPrivateChannel.getName());
                break;
            case "/join_channel":
                privateChannelName = partsFromClientMenuCommand[1];
                Channel privateChannelToJoin = PrivateChannel.findChannelByName(privateChannelName);
                if (privateChannelToJoin == null) {
                    clientService.replyFromServerToClient(MenuUtils.channelNotFound);
                    break;
                }
                clientService.setActiveChannel(privateChannelToJoin);
                privateChannelToJoin.addClientServiceToChannel(clientService);
                clientService.replyFromServerToClient(MenuUtils.subscribedPrivateChannel + privateChannelName);
                clientService.replyFromServerToClient(MenuUtils.activeChannel + privateChannelName);
                break;
            case "/leave_channel":
                privateChannelName = partsFromClientMenuCommand[1];
                Channel channelToLeave = PrivateChannel.findChannelByName(privateChannelName);
                if (channelToLeave == null) {
                    clientService.replyFromServerToClient(MenuUtils.channelNotFound);
                    break;
                }
                channelToLeave.removeClientServiceFromChannel(clientService);
                clientService.setActiveChannel(GroupChannel.getInstance());
                clientService.replyFromServerToClient(MenuUtils.leftPrivateChannel + privateChannelName);
                clientService.replyFromServerToClient(MenuUtils.backToGroupChannel);
                break;
            case "/switch_channel":
                String channelName = partsFromClientMenuCommand[1];
                if (channelName.equals("group")) {
                    clientService.setActiveChannel(GroupChannel.getInstance());
                    clientService.replyFromServerToClient(MenuUtils.backToGroupChannel);
                    break;
                }
                Channel channelToSwitchTo = PrivateChannel.findChannelByName(channelName);
                if (channelToSwitchTo == null) {
                    clientService.replyFromServerToClient(MenuUtils.channelNotFound);
                    break;
                }
                clientService.setActiveChannel(channelToSwitchTo);
                clientService.replyFromServerToClient(MenuUtils.activeChannel + channelName);
                break;
            case "/active":
                clientService.replyFromServerToClient(MenuUtils.activeChannel + clientService.getActiveChannel().getName());
                break;
            case "/send_file":
                try {
                    String fileName = partsFromClientMenuCommand[1];
                    byte[] binaryFromClient = ReaderUtils.readResponse(clientService.getInputStream());
                    FileHandlerUtils.saveFileFromClientOnServer(fileName, binaryFromClient);
                    clientService.broadcastMessageToClients("/sending_file " + fileName, false);
                    clientService.broadcastBinaryToClients(binaryFromClient);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            default:
                clientService.replyFromServerToClient(MenuUtils.menuCommandNotFound);
                break;
        }
    }
}

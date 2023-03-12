package pl.sages.chat.server.menu;

public final class ServerMessages {

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
   // TODO implement validation for invalid No of args in channel-related classes
    public static final String invalidNumberOfArgumentsForChannelCommand = "Server: invalid number of arguments. Please provide channel name after channel-related command, no spaces allowed \n(for example </create_channel testChannel>) ";
    public static final String channelNameTaken = "Server: this channel name is already taken, please pick a different name for the new channel";
    public static final String channelNotFound = "Server: unable to find private channel with the name specified bu user. Please check whether the name is correct";

}

package pl.sages.chat.server;

// commands client can use:
// /help
// /create_channel channelName

// first user input is checked for "/" char and if so, the command typed by the user is returned and passed to switch statement

public class MenuUtils {
    public static final String helpMessage = "Server: this is a sample help message";
    public static final String menuCommandNotFound = "Server: menu command not found, enter /help for the list of available commands";
    public static final String createdAndEnteredPrivateChannel = "Server: you have created and entered a private channel, channel name: ";
    public static final String enteredPrivateChannel = "Server: you have entered a private channel, channel name: ";
    public static final String leftPrivateChannel = "Server: you have left the private channel, channel name: ";
    public static final String activeChannel = "Server: you are now writing in the channel: ";
    public static final String backToGroupChannel = "Server: you are back to the group channel";

    // should return a command used by the user

}

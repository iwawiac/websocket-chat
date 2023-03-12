package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.*;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class LeaveChannelStrategy implements IMenuStrategy {
    @Override
    public void executeMenuCommandStrategy(String commandFromClient, ClientService clientService) {
        String [] partsFromClientMenuCommand = commandFromClient.split(" ");
        String privateChannelName = partsFromClientMenuCommand[1];
        Channel channelToLeave = PrivateChannel.findChannelByName(privateChannelName);
        if (channelToLeave == null) {
            clientService.replyFromServerToClient(ServerMessages.channelNotFound);
            return;
        }
        channelToLeave.removeClientServiceFromChannel(clientService);
        clientService.setActiveChannel(GroupChannel.getInstance());
        clientService.replyFromServerToClient(ServerMessages.leftPrivateChannel + privateChannelName);
        clientService.replyFromServerToClient(ServerMessages.backToGroupChannel);
    }
}

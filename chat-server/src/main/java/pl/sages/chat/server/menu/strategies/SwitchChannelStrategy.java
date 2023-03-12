package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.*;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class SwitchChannelStrategy implements IMenuStrategy {

    private ClientService clientService;

    public SwitchChannelStrategy(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void executeMenuCommandStrategy(String commandFromClient) {
        String [] partsFromClientMenuCommand = commandFromClient.split(" ");
        String channelName = partsFromClientMenuCommand[1];
        if (channelName.equals("group")) {
            clientService.setActiveChannel(GroupChannel.getInstance());
            clientService.replyFromServerToClient(ServerMessages.backToGroupChannel);
            return;
        }
        Channel channelToSwitchTo = PrivateChannel.findChannelByName(channelName);
        if (channelToSwitchTo == null) {
            clientService.replyFromServerToClient(ServerMessages.channelNotFound);
            return;
        }
        clientService.setActiveChannel(channelToSwitchTo);
        clientService.replyFromServerToClient(ServerMessages.activeChannel + channelName);
    }
}

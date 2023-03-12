package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.Channel;
import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.PrivateChannel;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class JoinChannelStrategy implements IMenuStrategy {

    @Override
    public void executeMenuCommandStrategy(String commandFromClient, ClientService clientService) {
        String [] partsFromClientMenuCommand = commandFromClient.split(" ");
        String privateChannelName = partsFromClientMenuCommand[1];
        Channel privateChannelToJoin = PrivateChannel.findChannelByName(privateChannelName);
        if (privateChannelToJoin == null) {
            clientService.replyFromServerToClient(ServerMessages.channelNotFound);
            return;
        }
        clientService.setActiveChannel(privateChannelToJoin);
        privateChannelToJoin.addClientServiceToChannel(clientService);
        clientService.replyFromServerToClient(ServerMessages.subscribedPrivateChannel + privateChannelName);
        clientService.replyFromServerToClient(ServerMessages.activeChannel + privateChannelName);

    }
}

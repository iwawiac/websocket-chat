package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
//import pl.sages.chat.server.MenuUtils;
import pl.sages.chat.server.PrivateChannel;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class CreateChannelStrategy implements IMenuStrategy {

    private ClientService clientService;

    public CreateChannelStrategy(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void executeMenuCommandStrategy(String commandFromClient) {
        String [] partsFromClientMenuCommand = commandFromClient.split(" ");
        String privateChannelName = partsFromClientMenuCommand[1];
        if (PrivateChannel.findChannelByName(privateChannelName) != null) {
            clientService.replyFromServerToClient(ServerMessages.channelNameTaken);
        }
        PrivateChannel newPrivateChannel = new PrivateChannel(privateChannelName);
        newPrivateChannel.addClientServiceToChannel(clientService);
        clientService.setActiveChannel(newPrivateChannel);
        clientService.replyFromServerToClient(ServerMessages.createdAndEnteredPrivateChannel + newPrivateChannel.getName());
    }
}

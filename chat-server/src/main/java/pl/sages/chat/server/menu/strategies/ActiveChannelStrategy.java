package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;
//import pl.sages.chat.server.MenuUtils;

public class ActiveChannelStrategy implements IMenuStrategy {
    private ClientService clientService;

    public ActiveChannelStrategy(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void executeMenuCommandStrategy(String commandFromClient) {
        clientService.replyFromServerToClient(ServerMessages.activeChannel + clientService.getActiveChannel().getName());
    }
}

package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;
//import pl.sages.chat.server.MenuUtils;

public class NotFoundStrategy implements IMenuStrategy {

    private ClientService clientService;

    public NotFoundStrategy(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void executeMenuCommandStrategy(String commandFromClient) {
        clientService.replyFromServerToClient(ServerMessages.menuCommandNotFound);
    }
}

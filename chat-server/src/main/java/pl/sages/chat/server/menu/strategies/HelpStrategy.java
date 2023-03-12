package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class HelpStrategy implements IMenuStrategy {

    private ClientService clientService;

    public HelpStrategy(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void executeMenuCommandStrategy(String commandFromClient) {
        this.clientService.replyFromServerToClient(ServerMessages.helpMessage);
    }

}

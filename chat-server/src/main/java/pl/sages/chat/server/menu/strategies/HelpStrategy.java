package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.menu.IMenuStrategy;
import pl.sages.chat.server.menu.ServerMessages;

public class HelpStrategy implements IMenuStrategy {
    @Override
    public void executeMenuCommandStrategy(String commandFromClient, ClientService clientService) {
        clientService.replyFromServerToClient(ServerMessages.helpMessage);
    }

}

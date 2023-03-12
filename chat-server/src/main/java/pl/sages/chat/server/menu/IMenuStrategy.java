package pl.sages.chat.server.menu;

import pl.sages.chat.server.ClientService;

public interface IMenuStrategy {
    void executeMenuCommandStrategy(String commandFromClient, ClientService clientService);
}

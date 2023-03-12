package pl.sages.chat.server.menu;

import pl.sages.chat.server.ClientService;

public class MenuStrategyManager {
    private final MenuStrategyMap menuStrategyMap;
    private final ClientService clientService;

    public MenuStrategyManager(ClientService clientService) {
        this.clientService = clientService;
        this.menuStrategyMap = new MenuStrategyMap();
    }

    public void processMenuCommand(String commandFromClient) {
        String commandUsed = commandFromClient.split(" ")[0];
        IMenuStrategy activeIMenuStrategy = menuStrategyMap.findMenuStrategy(commandUsed);
        activeIMenuStrategy.executeMenuCommandStrategy(commandFromClient, this.clientService);
    }
}

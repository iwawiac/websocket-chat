package pl.sages.chat.server.menu;

import pl.sages.chat.server.ClientService;

public class MenuStrategyManager {

    private IMenuStrategy activeIMenuStrategy;
    private MenuStrategyMap menuStrategyMap;
    private ClientService clientService;

    public MenuStrategyManager(ClientService clientService) {
        this.clientService = clientService;
        this.menuStrategyMap = new MenuStrategyMap(this.clientService);
    }

    public void processMenuCommand(String commandFromClient) {
        String commandUsed = commandFromClient.split(" ")[0];
        activeIMenuStrategy = menuStrategyMap.findMenuStrategy(commandUsed);
        this.activeIMenuStrategy.executeMenuCommandStrategy(commandFromClient);
    }
}

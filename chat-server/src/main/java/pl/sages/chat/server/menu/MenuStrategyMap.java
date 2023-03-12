package pl.sages.chat.server.menu;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.menu.strategies.*;

public class MenuStrategyMap {

    private ClientService clientService;
    private IMenuStrategy helpStrategy;
    private IMenuStrategy createChannelStrategy;
    private IMenuStrategy joinChannelStrategy;
    private IMenuStrategy leaveChannelStrategy;
    private IMenuStrategy switchChannelStrategy;
    private IMenuStrategy activeChannelStrategy;
    private IMenuStrategy sendFileStrategy;
    private IMenuStrategy notFoundStrategy;

    public MenuStrategyMap(ClientService clientService) {
        this.clientService = clientService;
        helpStrategy = new HelpStrategy(clientService);
        createChannelStrategy = new CreateChannelStrategy(clientService);
        joinChannelStrategy = new JoinChannelStrategy(clientService);
        leaveChannelStrategy = new LeaveChannelStrategy(clientService);
        switchChannelStrategy = new SwitchChannelStrategy(clientService);
        activeChannelStrategy = new ActiveChannelStrategy(clientService);
        sendFileStrategy = new SendFileStrategy(clientService);
        notFoundStrategy = new NotFoundStrategy(clientService);
    }

    public IMenuStrategy findMenuStrategy(String commandUsed){
        switch (commandUsed) {
            case "/help":
                return helpStrategy;
            case "/create_channel":
                return createChannelStrategy;
            case "/join_channel":
                return joinChannelStrategy;
            case "/leave_channel":
                return leaveChannelStrategy;
            case "/switch_channel":
                return switchChannelStrategy;
            case "/active":
                return activeChannelStrategy;
            case "/send_file":
                return sendFileStrategy;
            default:
                return notFoundStrategy;
        }
    }
}

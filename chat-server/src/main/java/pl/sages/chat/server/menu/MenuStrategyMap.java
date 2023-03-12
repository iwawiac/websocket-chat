package pl.sages.chat.server.menu;

import pl.sages.chat.server.menu.strategies.*;

public class MenuStrategyMap {

    private final IMenuStrategy helpStrategy;
    private final IMenuStrategy createChannelStrategy;
    private final IMenuStrategy joinChannelStrategy;
    private final IMenuStrategy leaveChannelStrategy;
    private final IMenuStrategy switchChannelStrategy;
    private final IMenuStrategy activeChannelStrategy;
    private final IMenuStrategy sendFileStrategy;
    private final IMenuStrategy notFoundStrategy;

    public MenuStrategyMap() {
        helpStrategy = new HelpStrategy();
        createChannelStrategy = new CreateChannelStrategy();
        joinChannelStrategy = new JoinChannelStrategy();
        leaveChannelStrategy = new LeaveChannelStrategy();
        switchChannelStrategy = new SwitchChannelStrategy();
        activeChannelStrategy = new ActiveChannelStrategy();
        sendFileStrategy = new SendFileStrategy();
        notFoundStrategy = new NotFoundStrategy();
    }

    public IMenuStrategy findMenuStrategy(String commandUsed){
        return switch (commandUsed) {
            case "/help" -> helpStrategy;
            case "/create_channel" -> createChannelStrategy;
            case "/join_channel" -> joinChannelStrategy;
            case "/leave_channel" -> leaveChannelStrategy;
            case "/switch_channel" -> switchChannelStrategy;
            case "/active" -> activeChannelStrategy;
            case "/send_file" -> sendFileStrategy;
            default -> notFoundStrategy;
        };
    }
}

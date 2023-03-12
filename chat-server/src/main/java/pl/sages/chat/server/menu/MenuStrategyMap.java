package pl.sages.chat.server.menu;

import pl.sages.chat.server.menu.strategies.*;

import java.util.HashMap;
import java.util.Map;

public class MenuStrategyMap {

    private final Map<String, IMenuStrategy> strategyMap;
    private final IMenuStrategy notFoundStrategy;

    public MenuStrategyMap() {
        this.notFoundStrategy = new NotFoundStrategy();
        strategyMap = new HashMap<>();
        strategyMap.put("/help", new HelpStrategy());
        strategyMap.put("/create_channel", new CreateChannelStrategy());
        strategyMap.put("/join_channel", new JoinChannelStrategy());
        strategyMap.put("/leave_channel", new LeaveChannelStrategy());
        strategyMap.put("/switch_channel", new SwitchChannelStrategy());
        strategyMap.put("/active", new ActiveChannelStrategy());
        strategyMap.put("/send_file", new SendFileStrategy());
    }

    public IMenuStrategy findMenuStrategy(String commandUsed) {
        String command = commandUsed.split(" ")[0];
        IMenuStrategy strategy = strategyMap.get(command);
        if(strategy==null) {
            return this.notFoundStrategy;
        } else {
            return  strategy;
        }
    }
}


package pl.sages.chat.server;

import java.util.LinkedList;
import java.util.List;

public class GroupChannel implements Channel {
    private final String channelName = "group";
    private static final List<ClientService> clientServiceList = new LinkedList<>();
    private static GroupChannel singletonGroupChannelInstance = null;

    public static GroupChannel getInstance() {
        if (singletonGroupChannelInstance == null) {
            singletonGroupChannelInstance = new GroupChannel();
        }
        return singletonGroupChannelInstance;
    }

    public String getName() {
        return this.channelName;
    }

    public List<ClientService> getClientServiceList() {
        return clientServiceList;
    }

    public void addClientServiceToChannel(ClientService clientServiceToAdd) {
        clientServiceList.add(clientServiceToAdd);
    }

    public void removeClientServiceFromChannel(ClientService clientServiceToRemove) {
        clientServiceList.remove(clientServiceToRemove);
    }
}

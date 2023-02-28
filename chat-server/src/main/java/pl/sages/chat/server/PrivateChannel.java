package pl.sages.chat.server;

import java.util.LinkedList;
import java.util.List;

public class PrivateChannel implements Channel{

    private final List<ClientService> clientServiceList = new LinkedList<>();
    private static final List<PrivateChannel> privateChannelList = new LinkedList<>();
    private final String name;

    public PrivateChannel(String name) {
        this.name = name;
        privateChannelList.add(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public List<ClientService> getClientServiceList() {
        return clientServiceList;
    }

    @Override
    public void addClientServiceToChannel(ClientService clientServiceToAdd) {
        if(clientServiceList.contains(clientServiceToAdd)) {
            return;
        }
        clientServiceList.add(clientServiceToAdd);
    }

    @Override
    public void removeClientServiceFromChannel(ClientService clientServiceToRemove) {
        clientServiceList.remove(clientServiceToRemove);
    }

    public static Channel findChannelByName(String channelName) {
        for(PrivateChannel privateChannel: privateChannelList) {
            if (privateChannel.getName().equals(channelName)) {
                return privateChannel;
            }
        }
        return null;
    }

    public static void removeClientServiceFromAllPrivateChannels(ClientService clientService){
        for (PrivateChannel channel : privateChannelList) {
            channel.removeClientServiceFromChannel(clientService);
        }
    }
}

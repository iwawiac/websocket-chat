package pl.sages.chat.server;

import java.util.LinkedList;
import java.util.List;

public class GroupChannel implements Channel{
    private final String channelName = "group";
    private static List<ClientService> clientServiceList = new LinkedList<>();

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





    // first everyone is added to the group channel - this channel always exists - DONE
    // once create_channel command is used - a new channel is created and user is added to this channel's clientServiceList
    // Channel is set as "active channel" as a ClientService field. There can be only one active channel.
    // Once a client enters a private channel - he does not receive any messages from the group channel
    // when a client sends a message - active channel is retrieved from ClientService
    // broadcast method then loops through all ClientServices within the channel


}

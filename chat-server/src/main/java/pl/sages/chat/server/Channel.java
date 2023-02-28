package pl.sages.chat.server;

import java.util.List;

public interface Channel {

    List<ClientService> getClientServiceList();
    void addClientServiceToChannel(ClientService clientServiceToAdd);
    void removeClientServiceFromChannel(ClientService clientServiceToRemove);
    String getName();


}

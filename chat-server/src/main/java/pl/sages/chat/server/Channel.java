package pl.sages.chat.server;

import java.util.List;

public interface Channel {

    List<ClientService> getClientServiceList();
    void addClientServiceToChannel(ClientService clientServiceToAdd);
    public void removeClientServiceFromChannel(ClientService clientServiceToRemove);
    public String getName();


}

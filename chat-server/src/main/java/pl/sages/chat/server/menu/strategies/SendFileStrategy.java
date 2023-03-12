package pl.sages.chat.server.menu.strategies;

import pl.sages.chat.server.ClientService;
import pl.sages.chat.server.FileHandlerUtils;
import pl.sages.chat.server.ReaderUtils;
import pl.sages.chat.server.menu.IMenuStrategy;

import java.io.IOException;

public class SendFileStrategy implements IMenuStrategy {
    @Override
    public void executeMenuCommandStrategy(String commandFromClient, ClientService clientService) {
        try {
            String [] partsFromClientMenuCommand = commandFromClient.split(" ");
            String fileName = partsFromClientMenuCommand[1];
            byte[] binaryFromClient = ReaderUtils.readResponse(clientService.getInputStream());
            FileHandlerUtils.saveFileFromClientOnServer(fileName, binaryFromClient);
            clientService.broadcastMessageToClients("/sending_file " + fileName, false);
            clientService.broadcastBinaryToClients(binaryFromClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

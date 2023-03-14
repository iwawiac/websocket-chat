package pl.sages.chat.client;

import java.io.IOException;
import java.nio.file.*;

public class ClientFileWatcher implements Runnable{

    @Override
    public void run() {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            Path rootDir = Paths.get(System.getProperty("user.dir"));
            rootDir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            while (true) {
                WatchKey watchKey;
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        Path createdPath = ((WatchEvent<Path>) event).context();
                        System.out.println("New file downloaded: " + createdPath);
                    }
                }

                // Reset the watch key to receive further events
                boolean valid = watchKey.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
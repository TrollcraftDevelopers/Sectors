package pl.trollcraft.sectors.messaging;

import java.util.UUID;
import java.util.function.Consumer;

public class ServerRequest {

    private final UUID id;
    private final Consumer<String[]> callback;

    public ServerRequest(UUID id, Consumer<String[]> callback) {
        this.id = id;
        this.callback = callback;
    }

    public UUID getId() {
        return id;
    }

    public Consumer<String[]> getCallback() {
        return callback;
    }
}

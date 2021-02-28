package pl.trollcraft.sectors.messaging;

import net.md_5.bungee.api.plugin.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RequestsController implements Listener {

    private final List<Request> requests;

    public RequestsController() {
        requests = new ArrayList<>();
    }

    public void register(Request request) {
        requests.add(request);
    }

    public Optional<Request> get(int id) {
        return requests.stream()
                .filter(request -> id == request.id())
                .findFirst();
    }

}

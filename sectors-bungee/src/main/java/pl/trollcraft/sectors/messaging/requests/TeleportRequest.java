package pl.trollcraft.sectors.messaging.requests;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.messaging.Request;

import java.util.logging.Logger;

@Deprecated
public class TeleportRequest implements Request {

    private static final Logger LOG
            = Logger.getLogger(TeleportRequest.class.getSimpleName());

    private static final int ID = 5;

    private final Messenger messenger;
    private final SectorsController sectorsController;

    public TeleportRequest(Messenger messenger,
                      SectorsController sectorsController) {

        this.messenger = messenger;
        this.sectorsController = sectorsController;
    }

    @Override
    public int id() {
        return ID;
    }

    //TODO implement group check.
    @Override
    public String[] process(String from, String[] data) {

        String playerName = data[0];
        ProxyServer server = ProxyServer.getInstance();

        LOG.info("Teleporting " + playerName);

        ProxiedPlayer player = server.getPlayer(playerName);
        if (player == null)
            return new String[] {"OFFLINE"};

        ServerInfo info = player.getServer().getInfo();

        messenger.forward( (byte) 3, info, data, r -> {});

        return new String[] {"OK"};
    }
}

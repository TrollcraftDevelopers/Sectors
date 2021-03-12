package pl.trollcraft.sectors.messaging.requests;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.Optional;
import java.util.logging.Logger;

@Deprecated
public class TPARequest implements Request {

    private static final Logger LOG
            = Logger.getLogger(TPARequest.class.getSimpleName());

    private static final int ID = 4;

    private final Messenger messenger;
    private final SectorsController sectorsController;

    public TPARequest(Messenger messenger,
                      SectorsController sectorsController) {

        this.messenger = messenger;
        this.sectorsController = sectorsController;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public String[] process(String from, String[] data) {

        String requesting = data[0];
        String playerName = data[1];

        ProxyServer server = ProxyServer.getInstance();
        ProxiedPlayer player = server.getPlayer(playerName);

        if (player == null)
            return new String[] {"OK", "OFFLINE"};

        ServerInfo info = player.getServer().getInfo();
        String serverName = info.getName();

        Optional<SectorsGroup> serverGroup = sectorsController.getSectorsGroup(serverName);
        Optional<SectorsGroup> fromGroup = sectorsController.getSectorsGroup(from);

        if (!serverGroup.isPresent() || !fromGroup.isPresent())
            return new String[] {"OK", "OFFLINE"};

        if (serverGroup.get().equals(fromGroup.get())) {

            // requesting, player
            String[] message = { requesting, playerName };
            messenger.forward( (byte) 2, info, message, r -> {});
            //TODO send request.

            return new String[]{"OK", serverName};
        }

        return new String[] { "OK", "OFFLINE" };

    }

}

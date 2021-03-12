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

public class LocateRequest implements Request {

    private static final Logger LOG
            = Logger.getLogger(TeleportRequest.class.getSimpleName());

    private static final int ID = 6;

    private final SectorsController sectorsController;

    public LocateRequest(SectorsController sectorsController) {
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

        ProxiedPlayer player = server.getPlayer(playerName);
        if (player == null)
            return new String[] {"OFFLINE"};

        ServerInfo info = player.getServer().getInfo();

        Optional<SectorsGroup> fromGroup = sectorsController.getSectorsGroup(from);
        Optional<SectorsGroup> toGroup = sectorsController.getSectorsGroup(info.getName());

        if (!fromGroup.isPresent() || !toGroup.isPresent())
            return new String[] {"OFFLINE"};

        String fromGroupName = fromGroup.get().getName();
        String toGroupName = toGroup.get().getName();

        if (!fromGroupName.equals(toGroupName))
            return new String[] {"OFFLINE"};

        return new String[] {"OK", info.getName()};
    }

}

package pl.trollcraft.sectors.model;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A 2D chart of world
 * handled by separate
 * server instance.
 */
public class Sector {

    private static final Logger LOG
            = Logger.getLogger(Sector.class.getSimpleName());

    private String serverName;

    private Pos a;
    private Pos b;

    private boolean online;

    private int players;
    private int maxPlayers;

    public Sector(String serverName, Pos a, Pos b) {
        this.serverName = serverName;
        this.a = a;
        this.b = b;
        online = true;
    }

    public String getServerName() {
        return serverName;
    }

    public Pos getA() {
        return a;
    }

    public Pos getB() {
        return b;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayers() {
        return players;
    }

    public boolean isOnline() {
        return online;
    }

    public void update() {

        ProxyServer proxy = ProxyServer.getInstance();
        ServerInfo serverInfo = proxy.getServerInfo(serverName);

        if (serverInfo == null)
            throw new IllegalStateException(String.format("%s does not exist.", serverInfo));

        serverInfo.ping( (serverPing, exception) -> {

            if (exception != null) {
                online = false;
                LOG.log(Level.SEVERE, "Error while fetching data for sector " + serverName);
                LOG.log(Level.SEVERE, exception.getMessage());

            }
            else {

                online = true;
                players = serverPing.getPlayers().getOnline();
                maxPlayers = serverPing.getPlayers().getMax();
                LOG.log(Level.INFO, "Sector " + serverName + " is OK." );

            }

        });

    }

}

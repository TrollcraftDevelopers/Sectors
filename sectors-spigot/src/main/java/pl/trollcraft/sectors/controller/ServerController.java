package pl.trollcraft.sectors.controller;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import pl.trollcraft.sectors.model.Server;
import redis.clients.jedis.Jedis;

import java.util.logging.Logger;

public class ServerController {

    private static final Logger LOG
            = Logger.getLogger(ServerController.class.getSimpleName());

    private Server server;
    private World world;

    private double warningDistance;
    private double cancelBuildingDistance;

    private final Plugin plugin;
    private final Jedis jedis;

    private BukkitTask reportingTask;

    public ServerController(Plugin plugin,
                            Jedis jedis) {

        this.plugin = plugin;
        this.jedis = jedis;
    }

    public void set(Server server,
                    World world,
                    double warningDistance,
                    double cancelBuildingDistance) {

        this.server = server;
        this.world = world;
        this.warningDistance = warningDistance;
        this.cancelBuildingDistance = cancelBuildingDistance;
    }

    public Server getServer() {
        return server;
    }

    public double getWarningDistance() {
        return warningDistance;
    }

    public double getCancelBuildingDistance() {
        return cancelBuildingDistance;
    }
}

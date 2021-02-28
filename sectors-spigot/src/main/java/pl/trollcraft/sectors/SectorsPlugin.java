package pl.trollcraft.sectors;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import pl.trollcraft.sectors.commands.SectorsDebugCommand;
import pl.trollcraft.sectors.commands.SectorsExtensiveCommand;
import pl.trollcraft.sectors.components.ComponentsManager;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.config.config.ComponentsConfig;
import pl.trollcraft.sectors.config.config.GeneralConfig;
import pl.trollcraft.sectors.controller.SectorBorderController;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;
import pl.trollcraft.sectors.controller.ServerController;
import pl.trollcraft.sectors.listeners.*;
import pl.trollcraft.sectors.listeners.movement.PlayerCheckpointCreatorListener;
import pl.trollcraft.sectors.listeners.movement.PlayerNearSectorEndListener;
import pl.trollcraft.sectors.listeners.movement.PlayerSectorBorderDeterminer;
import pl.trollcraft.sectors.listeners.movement.PlayerSectorLeaveListener;
import pl.trollcraft.sectors.messaging.Messenger;
import redis.clients.jedis.Jedis;

import java.util.logging.Level;

public class SectorsPlugin extends JavaPlugin {

    private Jedis jedis;
    private Messenger messenger;

    private ServerController serverController;
    private SectorPlayersController sectorPlayersController;
    private SectorController sectorController;
    private SectorBorderController sectorBorderController;

    private SectorsExtensiveCommand sectorsExtensiveCommand;

    private PlayerSectorLeaveListener playerSectorLeaveListener;
    private PlayerCheckpointCreatorListener playerCheckpointCreatorListener;
    private PlayerNearSectorEndListener playerNearSectorEndListener;
    private PlayerSectorBorderDeterminer playerSectorBorderDeterminer;

    private ComponentsManager componentsManager;

    @Override
    public void onLoad() {
        jedis = new Jedis("localhost");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        serverController = new ServerController(this, jedis);

        ConfigProvider provider = new ConfigProvider(this, "config.yml");
        new GeneralConfig(serverController).configure(provider);

        messenger = new Messenger(this);

        sectorPlayersController = new SectorPlayersController(this, messenger, jedis, serverController);
        sectorController = new SectorController(messenger, serverController);

        sectorBorderController = new SectorBorderController(sectorController);

        componentsManager = new ComponentsManager(this);
        componentsManager.prepare();

        getLogger().log(Level.INFO, "Completed loading.");
    }

    @Override
    public void onEnable() {

        // ---- Commands ----

        PluginCommand sectorDebugCommand = getCommand("sector");
        PluginCommand sectorsExtensiveCommand = getCommand("sectors");

        assert sectorDebugCommand != null;
        assert sectorsExtensiveCommand != null;

        sectorDebugCommand.setExecutor(new SectorsDebugCommand(messenger,
                sectorController, sectorBorderController));

        this.sectorsExtensiveCommand = new SectorsExtensiveCommand();
        sectorsExtensiveCommand.setExecutor(this.sectorsExtensiveCommand);

        // ---- Listeners ----

        getServer().getPluginManager()
                .registerEvents(new JoinListeners(this,
                        sectorPlayersController, sectorController), this);

        getServer().getPluginManager()
                .registerEvents(new QuitListeners(sectorPlayersController), this);

        getServer().getPluginManager()
                .registerEvents(new BuildingListener(sectorPlayersController), this);

        playerSectorLeaveListener = new PlayerSectorLeaveListener(this,
                sectorPlayersController, sectorController);

        playerCheckpointCreatorListener = new PlayerCheckpointCreatorListener(this,
                sectorPlayersController, sectorController);

        playerNearSectorEndListener = new PlayerNearSectorEndListener(this,
                serverController, sectorPlayersController, sectorController);

        playerSectorBorderDeterminer = new PlayerSectorBorderDeterminer(this,
                sectorController, sectorPlayersController, sectorBorderController);

        getLogger().log(Level.INFO, "Completed enabling.");

        // Components

        getLogger().log(Level.INFO, "Loading components from configuration file.");
        ConfigProvider provider = new ConfigProvider(this, "components.yml");
        new ComponentsConfig(this, componentsManager).configure(provider);
    }

    @Override
    public void onDisable() {
        playerSectorLeaveListener.cancel();
        playerCheckpointCreatorListener.cancel();
        playerNearSectorEndListener.cancel();
        playerSectorBorderDeterminer.cancel();

        getLogger().log(Level.INFO, "Disabled.");
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public ServerController getServerController() {
        return serverController;
    }

    public SectorsExtensiveCommand getSectorsExtensiveCommand() {
        return sectorsExtensiveCommand;
    }
}

package pl.trollcraft.sectors;

import net.md_5.bungee.api.plugin.Plugin;
import pl.trollcraft.sectors.commands.SectorsDebugCommand;
import pl.trollcraft.sectors.components.ComponentsManager;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.config.config.ComponentsConfig;
import pl.trollcraft.sectors.config.config.SectorsConfig;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.messaging.RequestsController;
import pl.trollcraft.sectors.messaging.requests.*;
import pl.trollcraft.sectors.messaging.sync.Synchronizer;
import pl.trollcraft.sectors.messaging.sync.SynchronizerController;
import pl.trollcraft.sectors.messaging.sync.SynchronizerListener;
import redis.clients.jedis.Jedis;

public class BungeeSectorsPlugin extends Plugin {

    private static BungeeSectorsPlugin plugin;

    private Jedis jedis;

    private Messenger messenger;
    private RequestsController requestsController;

    private SectorsController sectorsController;
    private SynchronizerController synchronizerController;

    private ConfigProvider syncProvider;

    @Override
    public void onLoad() {

        plugin = this;

        jedis = new Jedis("localhost");

        sectorsController = new SectorsController(this);
        //sectorsController.runSectorsInformationUpdater();

        ConfigProvider defaultConfigProvider = new ConfigProvider(this, "config.yml");
        SectorsConfig sectorsConfig = new SectorsConfig(sectorsController);
        sectorsConfig.configure(defaultConfigProvider);

        requestsController = new RequestsController();

        messenger = new Messenger(this, requestsController);

        synchronizerController = new SynchronizerController(messenger, sectorsController);
        sectorsController.getAllGroups().forEach( sectorsGroup ->
                synchronizerController.register(new Synchronizer(sectorsGroup)));

        syncProvider = new ConfigProvider(this, "sync.yml");
        synchronizerController.load(syncProvider);

        requestsController.register(new GetSectorRequest(sectorsController));
        requestsController.register(new DetermineSectorRequest(sectorsController));
        requestsController.register(new SyncSectorsRequest(synchronizerController));

        //TODO Move to a component or separate plugin!!!
        requestsController.register(new TPARequest(messenger, sectorsController));
        requestsController.register(new TeleportRequest(messenger, sectorsController));
        //TODO end

        requestsController.register(new LocateRequest(sectorsController));

        ComponentsManager componentsManager = new ComponentsManager(this);
        componentsManager.prepare();

        ConfigProvider componentsConfigProvider = new ConfigProvider(this, "components.yml");
        new ComponentsConfig(this, componentsManager).configure(componentsConfigProvider);

        getProxy().getPluginManager().registerCommand(this, new SectorsDebugCommand(defaultConfigProvider, sectorsConfig));

        getProxy().getPluginManager().registerListener(this, new SynchronizerListener(synchronizerController, sectorsController));
    }

    @Override
    public void onDisable() {
        synchronizerController.save(syncProvider);
    }

    public Jedis getJedis() {
        return jedis;
    }

    public Messenger getMessenger() {
        return messenger;
    }

    public RequestsController getRequestsController() {
        return requestsController;
    }

    public SectorsController getSectorsController() {
        return sectorsController;
    }

    public SynchronizerController getSynchronizerController() {
        return synchronizerController;
    }

    public static BungeeSectorsPlugin getPlugin() {
        return plugin;
    }

}

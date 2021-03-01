package pl.trollcraft.sectors;

import net.md_5.bungee.api.plugin.Plugin;
import pl.trollcraft.sectors.components.ComponentsManager;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.config.config.ComponentsConfig;
import pl.trollcraft.sectors.config.config.SectorsConfig;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.messaging.RequestsController;
import pl.trollcraft.sectors.messaging.requests.DetermineSectorRequest;
import pl.trollcraft.sectors.messaging.requests.GetSectorRequest;
import redis.clients.jedis.Jedis;

public class BungeeSectorsPlugin extends Plugin {

    private Jedis jedis;

    private Messenger messenger;
    private RequestsController requestsController;

    private SectorsController sectorsController;

    @Override
    public void onLoad() {

        jedis = new Jedis("localhost");

        sectorsController = new SectorsController(this);
        //sectorsController.runSectorsInformationUpdater();

        ConfigProvider defaultConfigProvider = new ConfigProvider(this, "config.yml");
        new SectorsConfig(sectorsController).configure(defaultConfigProvider);

        requestsController = new RequestsController();
        requestsController.register(new GetSectorRequest(sectorsController));
        requestsController.register(new DetermineSectorRequest(sectorsController));

        messenger = new Messenger(this, requestsController);

        ComponentsManager componentsManager = new ComponentsManager(this);
        componentsManager.prepare();

        ConfigProvider componentsConfigProvider = new ConfigProvider(this, "components.yml");
        new ComponentsConfig(this, componentsManager).configure(componentsConfigProvider);

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
}

package pl.trollcraft.sectors.config.config;

import org.bukkit.Bukkit;
import org.bukkit.World;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.controller.ServerController;
import pl.trollcraft.sectors.model.Server;

public class GeneralConfig implements Config{

    private final ServerController serverController;

    public GeneralConfig(ServerController serverController) {
        this.serverController = serverController;
    }

    @Override
    public void configure(ConfigProvider provider) {
        String groupName = provider.read("groupName", String.class);
        String serverName = provider.read("serverName", String.class);

        Server server = new Server();
        server.setGroupName(groupName);
        server.setSectorName(serverName);

        String worldName = provider.read("worldName", String.class);
        World world = Bukkit.getWorld(worldName);

        double warningDistance = provider.read("warningDistance", Double.class);
        double cancelBuildingDistance = provider.read("cancelBuildingDistance", Double.class);

        serverController.set(server, world, warningDistance, cancelBuildingDistance);
    }

}

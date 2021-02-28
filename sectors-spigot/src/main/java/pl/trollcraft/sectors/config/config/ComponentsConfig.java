package pl.trollcraft.sectors.config.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import pl.trollcraft.sectors.components.ComponentsManager;
import pl.trollcraft.sectors.config.ConfigProvider;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentsConfig implements Config{

    private static final Logger LOG
            = Logger.getLogger(ComponentsConfig.class.getSimpleName());

    private final Plugin plugin;
    private final ComponentsManager componentsManager;

    public ComponentsConfig(Plugin plugin,
                            ComponentsManager componentsManager) {

        this.plugin = plugin;
        this.componentsManager = componentsManager;
    }

    @Override
    public void configure(ConfigProvider provider) {

        ConfigurationSection section = provider.read("components", ConfigurationSection.class);
        section.getKeys(false).forEach( componentName -> {

            String fileName = provider.read("components." + componentName + ".file", String.class);
            String mainClass = provider.read("components." + componentName + ".class", String.class);

            File jarFile = new File(plugin.getDataFolder().getAbsolutePath() +
                    File.separator + "/components/" + fileName);

            if (!jarFile.exists())
                LOG.log(Level.WARNING, "Jar file of " + componentName + " component does not exist.");

            else {
                LOG.log(Level.INFO, "Loading component " + componentName + ".");
                boolean loaded = componentsManager.load(jarFile, componentName, mainClass);

                if (loaded)
                    LOG.log(Level.INFO, "Component " + componentName + " loaded.");
                else
                    LOG.log(Level.INFO, "Component " + componentName + " loading FAILED.");
            }



        } );

    }

}

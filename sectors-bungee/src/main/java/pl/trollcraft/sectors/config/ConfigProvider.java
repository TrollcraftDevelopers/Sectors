package pl.trollcraft.sectors.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigProvider {

    private static final Logger LOG =
            Logger.getLogger(ConfigProvider.class.getTypeName());

    private final Plugin plugin;
    private final String fileName;

    private Configuration conf;

    public ConfigProvider(Plugin plugin, String fileName) {

        this.plugin = plugin;
        this.fileName = fileName;

        if (!plugin.getDataFolder().exists() && plugin.getDataFolder().mkdir())
            LOG.log(Level.INFO, "Created data folder.");

        try {

            File configFile = new File(plugin.getDataFolder(), fileName);
            if (!configFile.exists()) {
                try (InputStream in = plugin.getResourceAsStream(fileName)) {
                    Files.copy(in, configFile.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            conf = ConfigurationProvider
                    .getProvider(YamlConfiguration.class)
                    .load(configFile);

        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public<T> T read(String key, Class<T> clazz) {
        Object o = conf.get(key);
        return clazz.cast(o);
    }

    public void write(String key, Object o) {
        conf.set(key, o);
    }

    public Configuration getConf() {
        return conf;
    }

    public boolean save() {
        try {

            ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .save(conf, new File(plugin.getDataFolder(), fileName));

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}

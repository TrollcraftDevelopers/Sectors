package pl.trollcraft.sectors.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigProvider {

    private static final Logger LOG =
            Logger.getLogger(ConfigProvider.class.getTypeName());

    private YamlConfiguration conf;

    public ConfigProvider(Plugin plugin, String fileName) {

        File file = new File(plugin.getDataFolder() + File.separator + fileName);
        if (!file.exists())
            plugin.saveResource(fileName, false);
        conf = new YamlConfiguration();

        try {
            conf.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to load config " + fileName);
        }

    }

    public<T> T read(String key, Class<T> clazz) {
        Object o = conf.get(key);
        return clazz.cast(o);
    }

    public boolean isSet(String key) {
        return conf.contains(key);
    }

    public void write(String key, Object o) {
        conf.set(key, o);
    }

}

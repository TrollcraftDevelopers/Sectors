package pl.trollcraft.sectors.components;

import pl.trollcraft.sectors.SectorsPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentsManager {

    private static final Logger LOG
            = Logger.getLogger(ComponentsManager.class.getSimpleName());

    private final SectorsPlugin plugin;
    private final Map<String, Component> components;

    public ComponentsManager(SectorsPlugin plugin) {
        this.plugin = plugin;
        components = new HashMap<>();
    }

    public void prepare() {

        File componentsDir = new File(plugin.getDataFolder(), "components");
        if (!componentsDir.exists() && componentsDir.mkdir())
            LOG.log(Level.INFO, "Created components directory.");

    }

    public boolean load(File jarFile,
                        String componentName,
                        String componentClass) {

        try {

            URL[] urls = new URL[] { jarFile.toURI().toURL() };

            URLClassLoader jar = new URLClassLoader(urls, getClass().getClassLoader());

            Class<?> clazz = jar.loadClass(componentClass);
            Class<? extends Component> initClazz = clazz.asSubclass(Component.class);

            Constructor<? extends Component> constructor = initClazz.getConstructor();
            Component component = constructor.newInstance();
            component.setPlugin(plugin);

            if (components.containsKey(componentName)) {
                LOG.log(Level.SEVERE, "Component with the same name already exists.");
                return false;
            }

            if (component.load()) {
                components.put(componentName, component);
                return true;
            }

            return false;

        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }


    }

}

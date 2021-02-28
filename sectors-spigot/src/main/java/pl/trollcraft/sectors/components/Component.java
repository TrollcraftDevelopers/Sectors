package pl.trollcraft.sectors.components;

import pl.trollcraft.sectors.SectorsPlugin;

public abstract class Component {

    private SectorsPlugin plugin;

    public final void setPlugin(SectorsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract boolean load();

    public abstract boolean unload();

    public final SectorsPlugin getPlugin() {
        return plugin;
    }
}

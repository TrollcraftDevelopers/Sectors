package pl.trollcraft.sectors.components;

import pl.trollcraft.sectors.BungeeSectorsPlugin;

public abstract class Component {

    private BungeeSectorsPlugin plugin;

    public final void setPlugin(BungeeSectorsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract boolean load();

    public abstract boolean unload();

    public final BungeeSectorsPlugin getPlugin() {
        return plugin;
    }

}

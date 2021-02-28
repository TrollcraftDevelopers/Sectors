package pl.trollcraft.sectors.config.config;

import net.md_5.bungee.config.Configuration;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.model.Pos;
import pl.trollcraft.sectors.model.Sector;

public class SectorsConfig implements Config{

    private SectorsController sectorsController;

    public SectorsConfig(SectorsController sectorsController) {
        this.sectorsController = sectorsController;
    }

    @Override
    public void configure(ConfigProvider provider) {

        Configuration sectors = provider.read("sectors", Configuration.class);
        sectors.getKeys().forEach( secName -> {

            double x = provider.read("sectors." + secName + ".region.a.x", Double.class);
            double y = provider.read("sectors." + secName + ".region.a.y", Double.class);
            Pos a = new Pos(x, y);

            x = provider.read("sectors." + secName + ".region.b.x", Double.class);
            y = provider.read("sectors." + secName + ".region.b.y", Double.class);
            Pos b = new Pos(x, y);

            sectorsController.store(new Sector(secName, a, b));

        } );

    }

}

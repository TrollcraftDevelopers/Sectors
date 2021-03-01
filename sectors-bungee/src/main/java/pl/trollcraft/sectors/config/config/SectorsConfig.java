package pl.trollcraft.sectors.config.config;

import net.md_5.bungee.config.Configuration;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.model.Pos;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.logging.Logger;

public class SectorsConfig implements Config {

    private static final Logger LOG
            = Logger.getLogger(SectorsConfig.class.getSimpleName());

    private final SectorsController sectorsController;

    public SectorsConfig(SectorsController sectorsController) {
        this.sectorsController = sectorsController;
    }

    @Override
    public void configure(ConfigProvider provider) {

        Configuration sectorsGroups = provider.read("sectors", Configuration.class);
        sectorsGroups.getKeys().forEach( sectorsGroup -> {

            LOG.info("Creating sectors group of name: " + sectorsGroup + ".");
            sectorsController.store(new SectorsGroup(sectorsGroup));

            Configuration sectors = provider.read("sectors." + sectorsGroup, Configuration.class);
            sectors.getKeys().forEach( secName -> {

                double x = provider.read("sectors." + sectorsGroup + "." + secName + ".region.a.x", Double.class);
                double y = provider.read("sectors." + sectorsGroup + "." + secName + ".region.a.y", Double.class);
                Pos a = new Pos(x, y);

                x = provider.read("sectors." + sectorsGroup + "." + secName + ".region.b.x", Double.class);
                y = provider.read("sectors." + sectorsGroup + "." + secName + ".region.b.y", Double.class);
                Pos b = new Pos(x, y);

                LOG.info("Registering sector " + secName + " to group " + sectorsGroup + ".");
                sectorsController.store(sectorsGroup, new Sector(secName, a, b));

            } );

        } );



    }

}

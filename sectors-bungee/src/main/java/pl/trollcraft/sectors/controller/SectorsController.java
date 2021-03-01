package pl.trollcraft.sectors.controller;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SectorsController {

    private static final Logger LOG
            = Logger.getLogger(SectorsController.class.getSimpleName());

    private final Plugin plugin;
    private final List<SectorsGroup> sectorsGroups;

    public SectorsController(Plugin plugin){
        this.plugin = plugin;
        sectorsGroups = new ArrayList<>();
    }

    public void store(SectorsGroup sectorsGroup) {
        sectorsGroups.add(sectorsGroup);
    }

    public void store(String sectorsGroupName,
                      Sector sector) {

        sectorsGroups.stream()
                .filter(sectorsGroup -> sectorsGroup.getName().equals(sectorsGroupName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown sectors group."))
                .getSectors()
                .add(sector);
    }

    public List<Sector> getSectors(String sectorsGroupName) {
        return sectorsGroups.stream()
                .filter(sectorsGroup -> sectorsGroup.getName().equals(sectorsGroupName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown sectors group."))
                .getSectors();
    }

    public Optional<Sector> get(String sectorsGroupName,
                                double x,
                                double z) {

        return sectorsGroups.stream()
                .filter(sectorsGroup -> sectorsGroup.getName().equals(sectorsGroupName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown sectors group."))
                .getSectors()
                .stream()
                .filter( sec -> (x < sec.getA().getX() && x > sec.getB().getX()) || (x < sec.getB().getX() && x > sec.getA().getX())  )
                .filter( sec -> (z < sec.getA().getZ() && z > sec.getB().getZ()) || (z < sec.getB().getZ() && z > sec.getA().getZ())  )
                .findFirst();

    }

    public Optional<Sector> get(String sectorsGroupName,
                                String serverName) {

        return sectorsGroups.stream()
                .filter(sectorsGroup -> sectorsGroup.getName().equals(sectorsGroupName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Unknown sectors group."))
                .getSectors()
                .stream()
                .filter( sec -> sec.getServerName().equals(serverName) )
                .findFirst();
    }

    // Moving to a component.
    // Moving to a component.
    // Moving to a component.
    /*public void runSectorsInformationUpdater() {

        ProxyServer
                .getInstance()
                .getScheduler()
                .schedule(plugin, () -> {

            LOG.log(Level.INFO, "Running sector information update...");
            sectorsGroups.forEach( sectorsGroup -> sectorsGroup.getSectors().forEach(Sector::update) );

        }, 20, 20, TimeUnit.SECONDS);

    }*/

}

package pl.trollcraft.sectors.controller;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import pl.trollcraft.sectors.model.Sector;

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
    private final List<Sector> sectors;

    public SectorsController(Plugin plugin){
        this.plugin = plugin;
        sectors = new ArrayList<>();
    }

    public void store(Sector sector) {
        sectors.add(sector);
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    public Optional<Sector> get(double x, double z) {

        return sectors.stream()
                .filter( sec -> (x < sec.getA().getX() && x > sec.getB().getX()) || (x < sec.getB().getX() && x > sec.getA().getX())  )
                .filter( sec -> (z < sec.getA().getZ() && z > sec.getB().getZ()) || (z < sec.getB().getZ() && z > sec.getA().getZ())  )
                .findFirst();
    }

    public Optional<Sector> get(String serverName) {
        return sectors.stream()
                .filter( sec -> sec.getServerName().equals(serverName) )
                .findFirst();
    }

    public void runSectorsInformationUpdater() {

        ProxyServer
                .getInstance()
                .getScheduler()
                .schedule(plugin, () -> {

            LOG.log(Level.INFO, "Running sector information update...");
            sectors.forEach(Sector::update);

        }, 20, 20, TimeUnit.SECONDS);

    }

}

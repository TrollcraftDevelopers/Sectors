package pl.trollcraft.sectors.listeners.movement;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerSectorLeaveListener {

    private static final Logger LOG
            = Logger.getLogger(PlayerSectorLeaveListener.class.getSimpleName());

    private final Plugin plugin;
    private final BukkitTask task;

    private final SectorPlayersController sectorPlayersController;
    private final SectorController sectorController;

    public PlayerSectorLeaveListener(Plugin plugin,
                                     SectorPlayersController sectorPlayersController,
                                     SectorController sectorController) {

        this.plugin = plugin;
        this.sectorPlayersController = sectorPlayersController;
        this.sectorController = sectorController;
        task = setupTask();
    }

    public BukkitTask setupTask() {

        if (task != null)
            throw new IllegalStateException("Task already set up.");

        return new BukkitRunnable() {

            @Override
            public void run() {

                if (!sectorController.isSet())
                    return;

                sectorPlayersController
                        .getSectorPlayers()
                        .forEach( p -> {

                    double x = p.getPlayer().getLocation().getX();
                    double z = p.getPlayer().getLocation().getZ();

                    if (!sectorController.inSector(x, z)){

                        LOG.log(Level.INFO, p.getPlayer().getName() + " left the sector.");
                        sectorPlayersController.exitSector(p);

                    }


                } );

            }

        }.runTaskTimerAsynchronously(plugin, 10, 10);

    }

    public void cancel() {
        task.cancel();
    }

}

package pl.trollcraft.sectors.listeners.movement;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;

import java.util.logging.Logger;

public class PlayerCheckpointCreatorListener {

    private static final Logger LOG
            = Logger.getLogger(PlayerCheckpointCreatorListener.class.getSimpleName());

    private final Plugin plugin;
    private final BukkitTask task;

    private final SectorPlayersController sectorPlayersController;
    private final SectorController sectorController;

    public PlayerCheckpointCreatorListener(Plugin plugin,
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

                            if (sectorController.inSector(x, z)){
                                p.setLastSectorLocation(p.getPlayer().getLocation());
                            }


                        } );

            }

        }.runTaskTimerAsynchronously(plugin, 20*5, 10);

    }

    public void cancel() {
        task.cancel();
    }

}

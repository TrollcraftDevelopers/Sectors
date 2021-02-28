package pl.trollcraft.sectors.listeners.movement;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.trollcraft.sectors.controller.SectorBorderController;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;

import java.util.logging.Logger;

@Deprecated
public class PlayerSectorBorderDeterminer {

    private static final Logger LOG
            = Logger.getLogger(PlayerCheckpointCreatorListener.class.getSimpleName());

    private final Plugin plugin;
    private final SectorController sectorController;
    //private final SectorBorderController sectorBorderController;
    private final SectorPlayersController sectorPlayersController;
    private final BukkitTask task;

    public PlayerSectorBorderDeterminer(Plugin plugin,
                                        SectorController sectorController,
                                        SectorPlayersController sectorPlayersController,
                                        SectorBorderController sectorBorderController) {

        this.plugin = plugin;
        this.sectorController = sectorController;
        this.sectorPlayersController = sectorPlayersController;
        //this.sectorBorderController = sectorBorderController;
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

                            Player player = p.getPlayer();
                            //sectorBorderController.displayBorder(player);

                        } );

            }

        }.runTaskTimerAsynchronously(plugin, 20*2, 20*2);

    }

    public void cancel() {
        task.cancel();
    }

}

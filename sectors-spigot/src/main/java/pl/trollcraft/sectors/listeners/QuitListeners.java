package pl.trollcraft.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.trollcraft.sectors.controller.SectorPlayersController;
import pl.trollcraft.sectors.model.sector.SectorPlayer;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuitListeners implements Listener {

    private static final Logger LOG
            = Logger.getLogger(QuitListeners.class.getSimpleName());

    private SectorPlayersController sectorPlayersController;

    public QuitListeners(SectorPlayersController sectorPlayersController) {
        this.sectorPlayersController = sectorPlayersController;
    }

    @EventHandler
    public void onQuit (PlayerQuitEvent event) {

        Player player = event.getPlayer();
        Optional<SectorPlayer> oSectorPlayer = sectorPlayersController.get(player);

        if (oSectorPlayer.isPresent()) {

            SectorPlayer sectorPlayer = oSectorPlayer.get();
            sectorPlayersController.delete(sectorPlayer);
            LOG.log(Level.INFO, "Player left the server.");

        }
        else
            LOG.log(Level.INFO, "No sector player data (probably sent to other sector).");

    }

}

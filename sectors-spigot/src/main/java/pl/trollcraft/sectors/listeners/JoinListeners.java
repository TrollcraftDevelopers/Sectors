package pl.trollcraft.sectors.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;
import pl.trollcraft.sectors.model.SectorPlayer;

import javax.swing.text.html.Option;
import java.util.Optional;

public class JoinListeners implements Listener {

    private final Plugin plugin;

    private final SectorPlayersController sectorPlayersController;
    private final SectorController sectorController;

    public JoinListeners(Plugin plugin,
                         SectorPlayersController sectorPlayersController,
                         SectorController sectorController) {

        this.plugin = plugin;
        this.sectorPlayersController = sectorPlayersController;
        this.sectorController = sectorController;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        if (!sectorController.isSet())

            new BukkitRunnable() {

                @Override
                public void run() {
                    sectorController.fetch();
                }

            }.runTaskLater(plugin, 20);

    }

    @EventHandler
    public void onSectorJoin (PlayerJoinEvent event) {

        Player player = event.getPlayer();
        Optional<SectorPlayer> oSectorPlayer = sectorPlayersController.get(player);

        if (!oSectorPlayer.isPresent()){

            SectorPlayer sectorPlayer = new SectorPlayer(player);
            sectorPlayersController.enterSector(sectorPlayer);

        }
    }

}

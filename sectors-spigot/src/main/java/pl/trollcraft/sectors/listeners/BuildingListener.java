package pl.trollcraft.sectors.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import pl.trollcraft.sectors.controller.SectorPlayersController;
import pl.trollcraft.sectors.model.sector.SectorPlayer;

import java.util.Optional;

public class BuildingListener implements Listener {

    private final SectorPlayersController sectorPlayersController;

    public BuildingListener(SectorPlayersController sectorPlayersController) {
        this.sectorPlayersController = sectorPlayersController;
    }

    @EventHandler
    public void onBlockPlace (BlockPlaceEvent event) {
        handleCancellableEvent(event, event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        handleCancellableEvent(event, event.getPlayer());
    }

    private void handleCancellableEvent(Cancellable event, Player player) {

        Optional<SectorPlayer> oSectorPlayer = sectorPlayersController.get(player);

        oSectorPlayer.ifPresent( sectorPlayer -> {

            if (sectorPlayer.isNearBorder()) {
                player.sendMessage(ChatColor.RED + "Jestes zbyt blisko granicy sektora, by budowac.");
                event.setCancelled(true);
            }

        } );

    }

}

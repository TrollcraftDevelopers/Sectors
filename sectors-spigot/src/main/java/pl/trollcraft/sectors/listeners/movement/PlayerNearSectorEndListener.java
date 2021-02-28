package pl.trollcraft.sectors.listeners.movement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.controller.SectorPlayersController;
import pl.trollcraft.sectors.controller.ServerController;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.event.border.SectorBorderApproachEvent;
import pl.trollcraft.sectors.model.event.border.SectorBorderMoveAwayEvent;
import pl.trollcraft.sectors.model.indicators.BossBarIndicator;
import pl.trollcraft.sectors.model.indicators.SectorBorderIndicator;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class PlayerNearSectorEndListener {

    private final Plugin plugin;

    private final ServerController serverController;
    private final SectorPlayersController sectorPlayersController;
    private final SectorController sectorController;
    private final List<SectorBorderIndicator> indicators;

    private final BukkitTask task;

    public PlayerNearSectorEndListener(Plugin plugin,
                                       ServerController serverController,
                                       SectorPlayersController sectorPlayersController,
                                       SectorController sectorController) {

        this.plugin = plugin;
        this.serverController = serverController;
        this.sectorPlayersController = sectorPlayersController;
        this.sectorController = sectorController;
        indicators = new LinkedList<>();

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

                Sector sector = sectorController.getSector();

                double warningDistance = serverController.getWarningDistance();
                double cancelBuildingDistance = serverController.getCancelBuildingDistance();

                sectorPlayersController
                        .getSectorPlayers()
                        .forEach( p -> {

                            Player player = p.getPlayer();
                            Location loc = player.getLocation();

                            Optional<SectorBorderIndicator> oIndicator
                                    = getIndicator(player);

                            double dist = Math.round(sector.distance(loc));

                            // Checking if player is in area protected from building.
                            if (dist <= cancelBuildingDistance && !p.isNearBorder()) {
                                p.setNearBorder(true);
                                p.getPlayer().sendMessage(ChatColor.RED + "Jestes na pograniczu sektorow. Nie mozesz budowac.");
                            }
                            else if (dist >= cancelBuildingDistance && p.isNearBorder()) {
                                p.setNearBorder(false);
                                p.getPlayer().sendMessage(ChatColor.GREEN + "Opuszczasz pogranicze sektorow. Mozesz juz budowac.");
                            }

                            // Checking if player should be warned he is approaching the border of
                            // the sector.
                            if (dist <= warningDistance) {

                                if (oIndicator.isPresent())
                                    oIndicator.get().update(dist);

                                else {
                                    SectorBorderIndicator indicator = new BossBarIndicator(player);
                                    indicator.update(dist);
                                    indicators.add(indicator);

                                    Event event = new SectorBorderApproachEvent(p, true);
                                    Bukkit.getPluginManager().callEvent(event);
                                }

                            }
                            else
                                oIndicator.ifPresent(ind -> {
                                    ind.hide();
                                    indicators.remove(ind);

                                    Event event = new SectorBorderMoveAwayEvent(p, true);
                                    Bukkit.getPluginManager().callEvent(event);
                                });

                        } );

            }

        }.runTaskTimerAsynchronously(plugin, 20, 20);

    }

    public void cancel() {
        task.cancel();
    }

    private Optional<SectorBorderIndicator> getIndicator(Player player) {
        return indicators.stream()
                .filter(ind -> ind.getPlayer().equals(player))
                .findFirst();
    }

}

package pl.trollcraft.sectors.controller;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.model.event.transfer.SectorPreTransferEvent;
import pl.trollcraft.sectors.model.sector.SectorPlayer;
import pl.trollcraft.sectors.model.event.SectorAppearEvent;
import pl.trollcraft.sectors.model.event.transfer.SectorPostTransferEvent;
import pl.trollcraft.sectors.model.event.SectorLeaveEvent;
import redis.clients.jedis.Jedis;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controls player behaviour on
 * entering and exiting sectors.
 *
 * @author Jakub Zelmanowicz
 */
public class SectorPlayersController {

    private static final Logger LOG
            = Logger.getLogger(SectorPlayersController.class.getSimpleName());

    private final List<SectorPlayer> sectorPlayers;

    private final Plugin plugin;
    private final Messenger messenger;
    private final Jedis jedis;
    private final ServerController serverController;

    public SectorPlayersController(Plugin plugin,
                                   Messenger messenger,
                                   Jedis jedis,
                                   ServerController serverController) {

        this.plugin = plugin;
        this.messenger = messenger;
        this.jedis = jedis;
        this.serverController = serverController;
        sectorPlayers = new LinkedList<>();
    }

    public List<SectorPlayer> getSectorPlayers() {
        return sectorPlayers;
    }

    public Optional<SectorPlayer> get(Player player) {
        return sectorPlayers.stream()
                .filter(sectorPlayer -> sectorPlayer.getPlayer().equals(player))
                .findFirst();
    }

    /**
     * Called when a player exits
     * this instance's sector.
     */
    public void exitSector(SectorPlayer sectorPlayer) {

        String groupName = serverController.getServer().getGroupName();

        LOG.log(Level.INFO, sectorPlayer.getPlayer().getName() + " exited the sector.");
        double x = sectorPlayer.getX();
        double z = sectorPlayer.getZ();

        String[] data = new String[] {
                groupName,
                String.valueOf(x),
                String.valueOf(z)
        };

        SectorLeaveEvent event = new SectorLeaveEvent(sectorPlayer, true);
        Bukkit.getPluginManager().callEvent(event);

        LOG.log(Level.INFO, "Firing sector leave event.");
        if (event.isCancelled()) {
            LOG.log(Level.INFO, "Sector leave cancelled!");
            if (sectorPlayer.getLastSectorLocation() != null)
                sectorPlayer.getPlayer().teleport(sectorPlayer.getLastSectorLocation());
            return;
        }

        LOG.log(Level.INFO, "Determining new sector...");
        messenger.forward( (byte) 1 , data, res -> {

            if (res[0].equals("OK")) {

                String playerName = sectorPlayer.getPlayer().getName();
                String sectorName = res[1];

                if (res[2].equals("OFFLINE")) {

                    LOG.log(Level.INFO, sectorName + " is offline.");
                    sectorPlayer.getPlayer().sendMessage(ChatColor.RED + "Ten sektor jest chwilowo " +
                            "niedostepny.");

                    if (sectorPlayer.getLastSectorLocation() != null)
                        sectorPlayer.getPlayer().teleport(sectorPlayer.getLastSectorLocation());

                    return;

                }

                LOG.log(Level.INFO, "Sector is OK.");
                LOG.log(Level.INFO, "Moving to sector (server) " + sectorName + "...");

                double y = sectorPlayer.getPlayer().getLocation().getY();
                float yaw = sectorPlayer.getPlayer().getLocation().getYaw();
                float pitch = sectorPlayer.getPlayer().getLocation().getPitch();

                Vector dir = sectorPlayer.getPlayer().getVelocity();
                double vx = dir.getX();
                double vz = dir.getZ();

                LOG.log(Level.INFO, "Sending data to NoSQL database...");
                jedis.set(String.format("%s.%s.from", playerName, sectorName), serverController.getServer().getSectorName());
                jedis.set(String.format("%s.%s.loc.x", playerName, sectorName), String.valueOf(x));
                jedis.set(String.format("%s.%s.loc.y", playerName, sectorName), String.valueOf(y));
                jedis.set(String.format("%s.%s.loc.z", playerName, sectorName), String.valueOf(z));
                jedis.set(String.format("%s.%s.loc.yaw", playerName, sectorName), String.valueOf(yaw));
                jedis.set(String.format("%s.%s.loc.pitch", playerName, sectorName), String.valueOf(pitch));
                jedis.set(String.format("%s.%s.dir.x", playerName, sectorName), String.valueOf(vx));
                jedis.set(String.format("%s.%s.dir.z", playerName, sectorName), String.valueOf(vz));

                jedis.expire(String.format("%s.%s.from", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.loc.x", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.loc.y", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.loc.z", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.loc.yaw", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.loc.pitch", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.dir.x", playerName, sectorName), 30);
                jedis.expire(String.format("%s.%s.dir.z", playerName, sectorName), 30);

                LOG.log(Level.INFO, "Firing preTransfer event...");
                SectorPreTransferEvent sectorPreTransferEvent
                        = new SectorPreTransferEvent(sectorPlayer, sectorName, jedis);
                Bukkit.getPluginManager().callEvent(sectorPreTransferEvent);

                LOG.log(Level.INFO, "Sending transfer request...");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF(sectorName);
                sectorPlayer.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());

                verifyTransfer(sectorPlayer);
                //sectorPlayers.remove(sectorPlayer);
            }
            else {
                LOG.log(Level.INFO, "No sector at given position.");
                sectorPlayer.getPlayer().sendMessage(ChatColor.RED + "Koniec mapy!");

                if (sectorPlayer.getLastSectorLocation() != null) {
                    sectorPlayer.getPlayer().sendMessage(ChatColor.RED + "Przed wyruszeniem w droge nalezy zebrac druzyne.");
                    sectorPlayer.getPlayer().teleport(sectorPlayer.getLastSectorLocation());
                }

            }
        });

    }

    /**
     * Controls whether the sector transfer of player
     * was successful. If so - sector player object
     * is deleted from server.
     *
     * @param sectorPlayer - transferred player.
     */
    public void verifyTransfer(SectorPlayer sectorPlayer) {

        new BukkitRunnable() {

            @Override
            public void run() {

                if (!sectorPlayer.getPlayer().isOnline()) {
                    LOG.log(Level.INFO, "Transfer successful. Deleting SectorPlayer object.");
                    delete(sectorPlayer);
                }
                else {
                    LOG.log(Level.INFO, "Transfer failed.");

                    if (sectorPlayer.getLastSectorLocation() != null)
                        sectorPlayer.getPlayer().teleport(sectorPlayer.getLastSectorLocation());

                }
            }

        }.runTaskLater(plugin, 10);

    }

    /**
     * Called when a player enters
     * this instance's sector.
     */
    public void enterSector(SectorPlayer sectorPlayer) {

        SectorAppearEvent sectorAppearEvent
                = new SectorAppearEvent(sectorPlayer);
        Bukkit.getPluginManager().callEvent(sectorAppearEvent);

        String sectorName = serverController.getServer().getSectorName();
        String playerName = sectorPlayer.getPlayer().getName();

        LOG.log(Level.INFO, "Player " + playerName + " appeared in the sector (" + sectorName + ")");

        if (jedis.exists(String.format("%s.%s.loc.x", playerName, sectorName))) {

            LOG.log(Level.INFO, "Transfer data found.");

            String fromSector = jedis.get(String.format("%s.%s.from", playerName, sectorName));
            double x = Double.parseDouble(jedis.get(String.format("%s.%s.loc.x", playerName, sectorName)));
            double y = Double.parseDouble(jedis.get(String.format("%s.%s.loc.y", playerName, sectorName)));
            double z = Double.parseDouble(jedis.get(String.format("%s.%s.loc.z", playerName, sectorName)));
            float yaw = Float.parseFloat(jedis.get(String.format("%s.%s.loc.yaw", playerName, sectorName)));
            float pitch = Float.parseFloat(jedis.get(String.format("%s.%s.loc.pitch", playerName, sectorName)));
            double vx = Double.parseDouble(jedis.get(String.format("%s.%s.dir.x", playerName, sectorName)));
            double vz = Double.parseDouble(jedis.get(String.format("%s.%s.dir.z", playerName, sectorName)));

            World world = sectorPlayer.getPlayer().getWorld();
            Location loc = new Location(world, x, y, z, yaw, pitch);
            Vector vel = new Vector(vx, 0, vz);
            loc.add(vel.multiply(2));

            LOG.log(Level.INFO, "Positioning player.");

            sectorPlayer.getPlayer().teleport(loc);
            sectorPlayer.getPlayer().getLocation().setDirection(vel);

            // Firing event when player is ready
            SectorPostTransferEvent sectorPostTransferEvent
                    = new SectorPostTransferEvent(sectorPlayer, sectorName, fromSector, jedis);
            Bukkit.getPluginManager().callEvent(sectorPostTransferEvent);

            LOG.log(Level.INFO, "Deleting transfer data.");
            jedis.keys(String.format("%s.%s.*", playerName, sectorName))
                    .forEach(jedis::del);
        }
        else
            LOG.log(Level.INFO, "Probably joined the server.");

        sectorPlayers.add(sectorPlayer);
        LOG.log(Level.INFO, "Player " + sectorPlayer.getPlayer().getName() + " is now in this instance's sector.");

    }

    public void delete(SectorPlayer sectorPlayer) {
        sectorPlayers.remove(sectorPlayer);
    }

}

package pl.trollcraft.sectors.model.sector;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a player in
 * instance's sector.
 *
 * @author Jakub Zelmanowicz
 */
public class SectorPlayer {

    private final Player player;
    private Location lastSectorLocation;

    private boolean nearBorder;
    private boolean sentToOtherSector;

    public SectorPlayer(Player player) {
        this.player = player;
        nearBorder = false;
        sentToOtherSector = false;
    }

    public Player getPlayer() {
        return player;
    }

    public double getX() {
        return player.getLocation().getX();
    }

    public double getZ() {
        return player.getLocation().getZ();
    }

    public Location getLastSectorLocation() {
        return lastSectorLocation;
    }

    public void setLastSectorLocation(Location lastSectorLocation) {
        this.lastSectorLocation = lastSectorLocation;
    }

    public boolean isNearBorder() {
        return nearBorder;
    }

    public void setNearBorder(boolean nearBorder) {
        this.nearBorder = nearBorder;
    }

    public boolean isBeingSentToOtherSector() {
        return sentToOtherSector;
    }

    public void setSentToOtherSector(boolean sentToOtherSector) {
        this.sentToOtherSector = sentToOtherSector;
    }
}

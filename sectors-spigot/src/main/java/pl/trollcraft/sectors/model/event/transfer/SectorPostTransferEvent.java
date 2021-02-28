package pl.trollcraft.sectors.model.event.transfer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.event.SectorAppearEvent;
import pl.trollcraft.sectors.model.sector.SectorPlayer;
import redis.clients.jedis.Jedis;

/**
 * Called when sector player enters from other sector.
 * The data transfer has been proceed and player is ready to
 * go deeper in the sector.
 *
 * Event is called on a "receiving server".
 *
 * @see SectorAppearEvent
 * @author Jakub Zelmanowicz
 */
public class SectorPostTransferEvent extends Event {

    private final SectorPlayer sectorPlayer;
    private final String sectorName;
    private final String fromSector;
    private final Jedis jedis;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SectorPostTransferEvent(SectorPlayer sectorPlayer,
                                   String sectorName,
                                   String fromSector,
                                   Jedis jedis) {

        this.sectorPlayer = sectorPlayer;
        this.sectorName = sectorName;
        this.fromSector = fromSector;
        this.jedis = jedis;
    }

    public SectorPlayer getSectorPlayer() {
        return sectorPlayer;
    }

    public String getFromSector() {
        return fromSector;
    }

    /**
     * Reading additinal data put in SectorPreTransferEvent
     *
     * @see SectorPreTransferEvent
     *
     * @param key - key of data.
     * @return the data.
     */
    public String readAdditional(String key) {
        return jedis.get(String.format("%s.%s.%s",sectorPlayer.getPlayer().getName(), sectorName, key));
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

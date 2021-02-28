package pl.trollcraft.sectors.model.event.transfer;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.sector.SectorPlayer;
import redis.clients.jedis.Jedis;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Called when sector player enters from other sector.
 * The data transfer is ready to send player to another server,
 * data is being loaded so additional information my be
 * inserted.
 *
 * Event is called on a "sending server".
 *
 * @author Jakub Zelmanowicz
 */
public class SectorPreTransferEvent extends Event {

    private static final Logger LOG
            = Logger.getLogger(SectorPreTransferEvent.class.getSimpleName());

    private final SectorPlayer sectorPlayer;
    private final String toSector;
    private final Jedis jedis;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SectorPreTransferEvent(SectorPlayer sectorPlayer,
                                  String toSector,
                                  Jedis jedis) {

        this.sectorPlayer = sectorPlayer;
        this.toSector = toSector;
        this.jedis = jedis;
    }

    public SectorPlayer getSectorPlayer() {
        return sectorPlayer;
    }

    public String getToSector() {
        return toSector;
    }

    /**
     * Puts additional transfer data.
     *
     * @param key - key of the data,
     * @param val - value of the data.
     */
    public void putAdditional(String key, String val) {
        String keyName = String.format("%s.%s.%s", sectorPlayer.getPlayer(), toSector, key);
        jedis.set(keyName, val);
        jedis.expire(keyName, 30);
        LOG.log(Level.INFO, "Added data to transfer (" + key + ").");
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

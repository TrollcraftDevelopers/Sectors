package pl.trollcraft.sectors.model.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.event.transfer.SectorPostTransferEvent;
import pl.trollcraft.sectors.model.sector.SectorPlayer;

/**
 * Called when a player appears in the sector.
 * It isn't known yet if he entered the sector from
 * other sector.
 *
 * @see SectorPostTransferEvent
 * @author Jakub Zelmanowicz
 */
public class SectorAppearEvent extends Event {

    private final SectorPlayer sectorPlayer;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SectorAppearEvent(SectorPlayer sectorPlayer) {
        this.sectorPlayer = sectorPlayer;
    }

    public SectorPlayer getSectorPlayer() {
        return sectorPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

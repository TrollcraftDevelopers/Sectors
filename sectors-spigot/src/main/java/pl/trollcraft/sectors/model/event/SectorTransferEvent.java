package pl.trollcraft.sectors.model.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.SectorPlayer;

/**
 * Called when sector player enters from other sector.
 * The data transfer has been proceed and player is ready to
 * go deeper in the sector.
 *
 * @see SectorAppearEvent
 * @author Jakub Zelmanowicz
 */
public class SectorTransferEvent extends Event {

    private final SectorPlayer sectorPlayer;

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public SectorTransferEvent(SectorPlayer sectorPlayer) {
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

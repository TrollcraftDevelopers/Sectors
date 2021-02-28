package pl.trollcraft.sectors.model.event.border;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.SectorPlayer;

public class SectorBorderApproachEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final SectorPlayer sectorPlayer;

    public SectorBorderApproachEvent(SectorPlayer sectorPlayer, boolean async) {
        super(async);
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

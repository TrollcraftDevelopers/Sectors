package pl.trollcraft.sectors.model.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.SectorPlayer;

public class SectorLeaveEvent extends Event implements Cancellable {

    private final SectorPlayer sectorPlayer;

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled;

    public SectorLeaveEvent(SectorPlayer sectorPlayer, boolean async) {
        super(async);

        this.sectorPlayer = sectorPlayer;
        cancelled = false;
    }

    public SectorPlayer getSectorPlayer() {
        return sectorPlayer;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

package pl.trollcraft.sectors.model.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SyncEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final String command;
    private final String[] args;

    public SyncEvent(String command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

package pl.trollcraft.sectors.model.event.calc;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pl.trollcraft.sectors.model.sector.Sector;

public class SectorCalculatingEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final CalculationState calculationState;
    private final Sector sector;

    public SectorCalculatingEvent(CalculationState calculationState,
                                  Sector sector,
                                  boolean async) {
        super(async);

        this.sector = sector;
        this.calculationState = calculationState;
    }

    public CalculationState getCalculationState() {
        return calculationState;
    }

    public Sector getSector() {
        return sector;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}

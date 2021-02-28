package pl.trollcraft.sectors.model.indicators;


import org.bukkit.entity.Player;

public abstract class SectorBorderIndicator {

    protected final Player player;

    public SectorBorderIndicator(Player player) {
        this.player = player;
    }

    public abstract void update(double dist);

    public abstract void hide();

    public Player getPlayer() {
        return player;
    }
}

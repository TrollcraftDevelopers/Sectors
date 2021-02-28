package pl.trollcraft.sectors.model.indicators;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarIndicator extends SectorBorderIndicator {

    private final BossBar bossBar;

    public BossBarIndicator(Player player) {
        super(player);
        bossBar = Bukkit.createBossBar(ChatColor.RED + "UWAGA! Granica sektora", BarColor.RED, BarStyle.SOLID);
        bossBar.addPlayer(player);
        bossBar.setProgress(0);
    }

    @Override
    public void update(double dist) {
        double maxDist = 50;
        bossBar.setProgress(1-(dist / maxDist));
    }

    @Override
    public void hide() {
        bossBar.removePlayer(player);
    }
}

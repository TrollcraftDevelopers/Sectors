package pl.trollcraft.sectors.model.indicators;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActionBarIndicator extends SectorBorderIndicator {

    public ActionBarIndicator(Player player) {
        super(player);

    }

    @Override
    public void update(double dist) {

        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(
                        ChatColor.DARK_RED +
                                "UWAGA! " +
                                ChatColor.RED +
                                "Granica sektora za " +
                                ChatColor.YELLOW +
                                dist +
                                " blokow."));
    }

    @Override
    public void hide() {
        player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                new TextComponent(""));
    }
}

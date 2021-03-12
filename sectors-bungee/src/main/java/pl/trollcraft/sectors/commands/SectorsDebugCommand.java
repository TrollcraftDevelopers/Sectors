package pl.trollcraft.sectors.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.config.config.SectorsConfig;

public class SectorsDebugCommand extends Command {

    private static final String NAME = "sectors";

    private final ConfigProvider configProvider;
    private final SectorsConfig sectorsConfig;

    public SectorsDebugCommand(ConfigProvider configProvider,
                               SectorsConfig sectorsConfig) {
        super(NAME);
        this.configProvider = configProvider;
        this.sectorsConfig = sectorsConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(new TextComponent("Dostepne komendy:\n" +
                    "/sectors reload <komponent> - przeladowanie komponentu."));
        }
        else {

            if (args[0].equalsIgnoreCase("reload")) {

                if (args.length != 2) {
                    sender.sendMessage(new TextComponent("Uzycie: /sectors reload <komponent>"));
                    return;
                }

                String component = args[1];

                //TODO change to component reload.
                if (component.equalsIgnoreCase("sectors")) {
                    sectorsConfig.configure(configProvider);
                    sender.sendMessage(new TextComponent("Przeladowano komponent."));
                }
                else
                    throw new IllegalArgumentException("Not implemented yet.");

            }

        }

    }

}

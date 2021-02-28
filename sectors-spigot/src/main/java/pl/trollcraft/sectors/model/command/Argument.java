package pl.trollcraft.sectors.model.command;

import org.bukkit.command.CommandSender;

public abstract class Argument {

    private final String name;
    private final String desc;

    public Argument(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public final String getName() {
        return name;
    }

    public final String getDesc() {
        return desc;
    }

    public abstract void execute(CommandSender sender, String[] args);

}

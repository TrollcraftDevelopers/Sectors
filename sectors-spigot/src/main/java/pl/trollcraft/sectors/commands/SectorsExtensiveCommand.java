package pl.trollcraft.sectors.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.trollcraft.sectors.model.command.Argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Since a component is not able to register a command
 * easily - this class allows components to create
 * commands. It also displays help
 * automatically.
 *
 * @author Jakub Zelmanowicz
 */
public final class SectorsExtensiveCommand implements CommandExecutor {

    private final List<Argument> arguments;

    public SectorsExtensiveCommand() {
        arguments = new ArrayList<>();
    }

    @Override
    public final boolean onCommand(CommandSender sender,
                             Command command,
                             String s,
                             String[] args) {

        if (arguments.isEmpty()){
            sender.sendMessage(ChatColor.GRAY + "Brak rozszerzen komendy.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "Sectors:\n");
            arguments.forEach(arg ->
                    sender.sendMessage(ChatColor.GRAY + "/" + s + " "
                            + arg.getName() + " - " + ChatColor.YELLOW + arg.getDesc())
            );
        }
        else {

            String argName = args[0];

            Optional<Argument> oArg = arguments.stream()
                    .filter(arg -> arg.getName().equalsIgnoreCase(argName))
                    .findFirst();

            if (oArg.isPresent()) {

                String[] nArgs = Arrays.copyOfRange(args, 1, args.length);
                oArg.get().execute(sender, nArgs);

            }
            else
                sender.sendMessage(ChatColor.RED + "Nieznane polecenie.");

        }

        return true;
    }

    public final void registerArgument(Argument argument) {
        arguments.add(argument);
    }

}

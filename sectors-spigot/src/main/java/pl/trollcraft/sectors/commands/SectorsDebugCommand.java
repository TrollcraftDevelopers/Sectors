package pl.trollcraft.sectors.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.trollcraft.sectors.controller.SectorBorderController;
import pl.trollcraft.sectors.controller.SectorController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.geo.Border;
import pl.trollcraft.sectors.model.geo.Direction;
import pl.trollcraft.sectors.model.geo.Pos;

/**
 * Local sector debugging commands
 * for admins and developers.
 *
 * @author Jakub Zelmanowicz
 */
public final class SectorsDebugCommand implements CommandExecutor {

    private final Messenger messenger;
    private final SectorController sectorController;
    private final SectorBorderController sectorBorderController;

    private Pos a, b;

    public SectorsDebugCommand(Messenger messenger,
                               SectorController sectorController,
                               SectorBorderController sectorBorderController) {

        this.messenger = messenger;
        this.sectorController = sectorController;
        this.sectorBorderController = sectorBorderController;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String s,
                             String[] args) {

        if (!sender.hasPermission("sectors.admin")) {
            sender.sendMessage("Brak uprawnien.");
            return true;
        }

        if (args.length == 0){
            sender.sendMessage(
                    "Sectors debug command:\n" +
                            "/sector check - w sektorze tej instancji?,\n" +
                            "/sector proxy-check - sprawdzenie sektora z serwera centralnego," +
                            "/sector delete - usuwa dane sektora,\n" +
                            "/sector fetch - pobiera dane sektora,\n" +
                            "/sector data - info. o tej sektorze tej instancji.\n" +
                            "/sector sign-borders - rysuje granice czasteczkami.\n" +
                            "/sector dist - pokazuje odleglosci do granic.\n" +
                            "/sector border <kierunek> - zaznacza wektor.\n" +
                            "/sector direction - pokazuje Twoj kierunek.\n" +
                            "/sector longer-border - zwraca dl. dluzej granicy.\n" +
                            "/sector angles - zwraca katy w stopniach pomiedzy Toba, a wektorami punktow.\n" +
                            "/sector points - zwraca info. o punktach."
            );
        }
        else {

            Player player = (Player) sender;

            if (args[0].equalsIgnoreCase("check")) {

                if (sectorController.isSet()) {

                    Location loc = player.getLocation();
                    double x = loc.getX();
                    double z = loc.getZ();

                    boolean inSector = sectorController.inSector(x, z);

                    if (inSector)
                        sender.sendMessage("Jestes w sektorze tego serwera.");
                    else
                        sender.sendMessage("Jestes poza sektorem tego serwera.");

                }
                else
                    sender.sendMessage("Sektor nie zostal pobrany z serwera centralnego.");

            }

            else if (args[0].equalsIgnoreCase("proxy-check")) {

                double x = player.getLocation().getX();
                double z = player.getLocation().getZ();

                String[] data = new String[] {
                    String.valueOf(x),
                    String.valueOf(z)
                };

                messenger.forward((byte) 1, data, res -> {

                    if (res[0].equals("OK"))
                        sender.sendMessage("W sektorze: " + res[1]);
                    else
                        sender.sendMessage("Poza jakimkolwiek sektorem.");

                });

                sender.sendMessage("Oczekiwanie na odpowiedz...");

            }

            else if (args[0].equalsIgnoreCase("delete")) {

                if (sectorController.isSet()) {
                    sectorController.delete();
                    sender.sendMessage("Dane sektora usuniete. " +
                            "Nalezy je jak najszybciej sciagnac. " +
                            "W kazdym razie przy dolaczeniu kolejnego gracza " +
                            "dane zostana automatycznie pobrane z serwera centralnego.");
                }
                else
                    sender.sendMessage("Brak danych sektora.");

            }

            else if (args[0].equalsIgnoreCase("fetch")) {

                sectorController.fetch();
                sender.sendMessage("Wyslano zadanie pobrania danych sektora.\n" +
                        "Sprawdz status korzystajac z komendy /sector data");

            }

            else if (args[0].equalsIgnoreCase("data")) {

                if (sectorController.isSet()) {

                    Sector sector = sectorController.getSector();

                    String name = sector.getServerName();
                    String a = sector.getA().getX() + "; " + sector.getA().getZ();
                    String b = sector.getB().getX() + "; " + sector.getB().getZ();

                    sender.sendMessage(name);
                    sender.sendMessage("A: " + a);
                    sender.sendMessage("B: " + b);

                }
                else
                    sender.sendMessage("Brak danych sektora.");

            }

            else if (args[0].equalsIgnoreCase("sign-borders")) {

                if (sectorController.isSet()) {

                    World world = player.getWorld();

                    Sector sector = sectorController.getSector();
                    Border[] borders = sector.getBorders();

                    for (Border border : borders) {

                        border.forEachBlock(world, 90, b -> {

                            Location loc = b.getLocation();
                            world.spawnParticle(Particle.FLAME, loc, 0, 0, 0, 0);

                        });

                    }

                }
                else
                    sender.sendMessage("Brak danych sektora.");

            }

            else if (args[0].equalsIgnoreCase("dist")) {

                if (sectorController.isSet()) {

                    Sector sector = sectorController.getSector();
                    for (Border b : sector.getBorders()) {
                        player.sendMessage(b.distance(player.getLocation()) + "");
                    }

                }
                else
                    sender.sendMessage("Brak danych sektora.");

            }

            else if (args[0].equalsIgnoreCase("border")) {

                World world = player.getWorld();

                Direction dir = Direction.valueOf(args[1].toUpperCase());
                Border border = sectorController.getSector().getBorder(dir);

                border.forEachBlock(world, 130, b -> b.setType(Material.BEDROCK));

            }

            else if (args[0].equalsIgnoreCase("direction")) {
                Direction direction = sectorBorderController.getDirection(player);
                player.sendMessage(direction.name());
            }

            else if (args[0].equalsIgnoreCase("longer-border")) {

                player.sendMessage("Dlugosc dluzej granicy wynosi " + sectorController.getSector().getLongerBorderLength());

            }

            else if (args[0].equalsIgnoreCase("display-border")) {

                sectorBorderController.displayBorder(player);

            }

            else if (args[0].equalsIgnoreCase("angles")) {

                Sector sector = sectorController.getSector();

                Vector dir = player.getLocation().getDirection();

                double angleA = sectorBorderController.angle(sector.getACenter(), dir);
                double angleB = sectorBorderController.angle(sector.getBCenter(), dir);
                double angleC = sectorBorderController.angle(sector.getCCenter(), dir);
                double angleD = sectorBorderController.angle(sector.getDCenter(), dir);

                player.sendMessage("A-C: " + Math.round(angleA));
                player.sendMessage("B-C: " + Math.round(angleB));
                player.sendMessage("C-C: " + Math.round(angleC));
                player.sendMessage("D-C: " + Math.round(angleD));

            }

            else if (args[0].equalsIgnoreCase("points")) {

                Sector sector = sectorController.getSector();

                player.sendMessage("A: " + sector.getA().toString());
                player.sendMessage("B: " + sector.getB().toString());
                player.sendMessage("C: " + sector.getC().toString());
                player.sendMessage("D: " + sector.getD().toString());

            }

        }

        return true;
    }
}

package pl.trollcraft.sectors.controller;

import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.trollcraft.sectors.help.packet.WrapperPlayServerWorldBorder;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.geo.Direction;
import pl.trollcraft.sectors.model.geo.Pos;

public class SectorBorderController {

    private final SectorController sectorController;

    public SectorBorderController(SectorController sectorController) {
        this.sectorController = sectorController;
    }

    public Direction getDirection(Player player) {

        double rotation = (player.getLocation().getYaw() - 90) % 360;

        if (rotation < 0) {
            rotation += 360.0;
            if (0 <= rotation && rotation < 22.5) {
                return Direction.NORTH;
            }
            if (22.5 <= rotation && rotation < 67.5) {
                return Direction.NORTH_EAST;
            }
            if (67.5 <= rotation && rotation < 112.5) {
                return Direction.EAST;
            }
            if (112.5 <= rotation && rotation < 157.5) {
                return Direction.EAST_SOUTH;
            }
            if (157.5 <= rotation && rotation < 202.5) {
                return Direction.SOUTH;
            }
            if (202.5 <= rotation && rotation < 247.5) {
                return Direction.SOUTH_WEST;
            }
            if (247.5 <= rotation && rotation < 292.5) {
                return Direction.WEST;
            }
            if (292.5 <= rotation && rotation < 337.5) {
                return Direction.WEST_NORTH;
            }
            if (337.5 <= rotation && rotation < 359) {
                return Direction.NORTH;
            }
        }
        return Direction.UNDEFINED;

    }

    public double angle(Vector v, Vector dir) {
        double vL = Math.sqrt( Math.pow(v.getX(), 2) + Math.pow(v.getZ(), 2) );
        double dirL = Math.sqrt( Math.pow(dir.getX(), 2) + Math.pow(dir.getZ(), 2) );
        return Math.toDegrees( Math.acos ( (v.getX()*dir.getX() + v.getZ()*dir.getZ()) / (vL * dirL)) );
    }

    public void displayBorder(Player player) {

        Sector sector = sectorController.getSector();

        Pos pos = getPointOf(player);

        double x = pos.getX(), z = pos.getZ();
        double l = sector.getLongerBorderLength();

        switch (pos.getDirection()) {

            case NORTH_EAST:
                x += l/2;
                z -= l/2;
                break;

            case EAST_SOUTH:
                x += l/2;
                z += l/2;
                break;

            case SOUTH_WEST:
                x -= l/2;
                z += l/2;
                break;

            default:
                x -= l/2;
                z -= l/2;
                break;

        }

        sendBorderPacket(player, x, z, l+2);

    }

    public Pos getPointOf(Player player) {

        Sector sector = sectorController.getSector();
        Vector dir = player.getLocation().getDirection();

        Location loc = player.getLocation();
        Pos p = new Pos(loc.getX(), loc.getZ());

        double distA = sector.getA().dist(p);
        double distB = sector.getB().dist(p);
        double distC = sector.getC().dist(p);
        double distD = sector.getD().dist(p);

        double angleA = angle(sector.getACenter(), dir)/distA;
        double angleB = angle(sector.getBCenter(), dir)/distB;
        double angleC = angle(sector.getCCenter(), dir)/distC;
        double angleD = angle(sector.getDCenter(), dir)/distD;

        if (angleA > angleB && angleA > angleC && angleA > angleD)
            return sector.getA();

        else if (angleB > angleA && angleB > angleC && angleB > angleD)
            return sector.getB();

        else if (angleC > angleA && angleC > angleB && angleC > angleD)
            return sector.getC();

        else return sector.getD();

    }

    private void sendBorderPacket(Player player,
                                  double x,
                                  double z,
                                  double r) {

        WrapperPlayServerWorldBorder border = new WrapperPlayServerWorldBorder();
        border.setRadius(r);
        border.setCenterX(x);
        border.setCenterZ(z);
        border.setOldRadius(r);
        border.setPortalTeleportBoundary(29999984);
        border.setWarningDistance(10);
        border.setWarningTime(10);
        border.setSpeed(0);
        border.setAction(EnumWrappers.WorldBorderAction.INITIALIZE);
        border.sendPacket(player);
    }

}

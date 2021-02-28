package pl.trollcraft.sectors.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pl.trollcraft.sectors.model.geo.Axis;
import pl.trollcraft.sectors.model.geo.Border;
import pl.trollcraft.sectors.model.geo.Direction;
import pl.trollcraft.sectors.model.geo.Pos;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A 2D chart of world
 * representing local instance
 * world chart.
 */
public class Sector {

    private final String serverName;

    private final Pos a;
    private final Pos b;
    private Pos c;
    private Pos d;

    private final Pos center;

    private Vector aC;
    private Vector bC;
    private Vector cC;
    private Vector dC;

    private Border[] borders;

    private double longerBorderLength;

    public Sector(String serverName, Pos a, Pos b) {
        this.serverName = serverName;
        this.a = a;
        this.b = b;
        center = new Pos((a.getX() + b.getX()) / 2, (a.getZ() + b.getZ()) / 2);
    }

    public String getServerName() {
        return serverName;
    }

    public Pos getA() {
        return a;
    }

    public Pos getB() {
        return b;
    }

    public Pos getC() {
        return c;
    }

    public Pos getD() {
        return d;
    }

    public Pos getCenter() {
        return center;
    }

    public Vector getACenter() {
        return aC;
    }

    public Vector getBCenter() {
        return bC;
    }

    public Vector getCCenter() {
        return cC;
    }

    public Vector getDCenter() {
        return dC;
    }

    public Border getBorder(Direction direction) {
        for (Border b : borders)
            if (b.getDirection() == direction)
                return b;
        return null;
    }

    /**
     * Calculates properties of the sector.
     */
    public void calculate() {

        double xa = a.getX();
        double za = a.getZ();
        double xb = b.getX();
        double zb = b.getZ();

        // X is a horizontal axis, Z is a vertical axis.
        if (xa < xb && zb > za) {
            c = new Pos(xb, za);
            d = new Pos(xa, zb);
        }

        // X is a vertical axis, Z is a horizontal axis.
        else {
            c = new Pos(xa, zb);
            d = new Pos(xb, za);
        }

        // Calculating vectors

        aC = new Vector(center.getX() - a.getX(), 0, center.getZ() - a.getZ()).normalize();
        bC = new Vector(center.getX() - b.getX(), 0, center.getZ() - b.getZ()).normalize();
        cC = new Vector(center.getX() - c.getX(), 0, center.getZ() - c.getZ()).normalize();
        dC = new Vector(center.getX() - d.getX(), 0, center.getZ() - d.getZ()).normalize();

        borders = new Border[4];
        borders[0] = new Border(a, c);
        borders[1] = new Border(c, b);
        borders[2] = new Border(b, d);
        borders[3] = new Border(d, a);

        // Determining orientations of borders.

        List<Border> zBorders = Arrays.stream(borders)
                .filter( bor -> bor.getAxis() == Axis.Z )
                .sorted()
                .collect(Collectors.toList());

        zBorders.get(0).setDirection(Direction.SOUTH);
        zBorders.get(1).setDirection(Direction.NORTH);

        List<Border> xBorders = Arrays.stream(borders)
                .filter( bor -> bor.getAxis() == Axis.X )
                .sorted()
                .collect(Collectors.toList());

        xBorders.get(0).setDirection(Direction.EAST);
        xBorders.get(1).setDirection(Direction.WEST);

        // Determining longer border.
        longerBorderLength = zBorders.get(0).length();
        double l = xBorders.get(0).length();
        if (l > longerBorderLength)
            longerBorderLength = l;

    }

    public void calculateDirections() {

        double xa = a.getX(), za = a.getZ();
        double xb = b.getX(), zb = b.getZ();
        double xc = c.getX(), zc = c.getZ();
        double xd = d.getX(), zd = d.getZ();

        double xCenter = center.getX(), zCenter = center.getZ();
        double xDelta = xCenter, zDelta = zCenter;

        if (xCenter + xDelta != 0)
            xDelta *= -1;
        if (zCenter + zDelta != 0)
            zDelta *= -1;

        xa += xDelta;
        xb += xDelta;
        xc += xDelta;
        xd += xDelta;

        za += zDelta;
        zb += zDelta;
        zc += zDelta;
        zd += zDelta;

        // Determining directions

        a.setDirection(determineDirection(xa, za));
        b.setDirection(determineDirection(xb, zb));
        c.setDirection(determineDirection(xc, zc));
        d.setDirection(determineDirection(xd, zd));

    }

    private Direction determineDirection(double x, double z) {
        if (x >= 0 && z <= 0) return Direction.SOUTH_WEST;
        else if (x < 0 && z < 0) return Direction.EAST_SOUTH;
        else if (x < 0 && z > 0) return Direction.NORTH_EAST;
        else return Direction.WEST_NORTH;
    }

    public Border[] getBorders() {
        return borders;
    }

    /**
     * Calculates point's distance to the nearest border.
     *
     * @param loc - location to calculate distance from,
     * @return distance to the nearest border, -1 if distance is greater than 10.
     */
    public double distance(Location loc) {

        Optional<Double> dist = Arrays.stream(borders)
                .map( b -> b.distance(loc))
                .min(Double::compare);

        /*Optional<Double> dist = Arrays.stream(borders)
                        .map( b -> b.distance(loc))
                        .filter( d -> d <= min)
                        .min(Double::compare);*/

        if (dist.isPresent())
            return dist.get();

        return -1;

    }

    /**
     * Since sector is a rectangle it is
     * sometimes necessary to retrieve
     * longer border.
     *
     * @return length of longer border
     */
    public double getLongerBorderLength() {
        return longerBorderLength;
    }


}

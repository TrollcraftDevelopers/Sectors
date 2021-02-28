package pl.trollcraft.sectors.model;

/**
 * A 2D position in the
 * world.
 */
public class Pos {

    private double x;
    private double z;

    public Pos(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }
}

package pl.trollcraft.sectors.model.geo;

/**
 * A 2D position in the
 * world.
 */
public class Pos {

    private double x;
    private double z;
    private Direction direction;

    public Pos(double x, double z) {
        this.x = x;
        this.z = z;
        direction = Direction.UNDEFINED;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Pos middle(Pos other) {
        double x = (this.x + other.x)/2;
        double z = (this.z + other.z)/2;
        return new Pos(x, z);
    }

    public double dist(Pos p) {
        return Math.sqrt( Math.pow(p.x - x, 2) + Math.pow(p.z - z, 2) );
    }

    @Override
    public String toString() {
        return "Pos{" +
                "x=" + x +
                ", z=" + z +
                ", dir=" + direction.name() +
                '}';
    }
}

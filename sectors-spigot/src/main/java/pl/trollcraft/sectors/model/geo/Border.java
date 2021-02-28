package pl.trollcraft.sectors.model.geo;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class Border implements Comparable<Border> {

    private final Pos aPos, bPos;

    private final Axis axis;
    private final double axisValue;
    private Direction direction;

    public Border(Pos a, Pos b) {

        this.aPos = a;
        this.bPos = b;

        double xa = a.getX();
        double za = a.getZ();
        double xb = b.getX();
        double zb = b.getZ();

        if (za == zb) {
            axis = Axis.Z;
            axisValue = za;
        }
        else if (xa == xb){
            axis = Axis.X;
            axisValue = xa;
        }
        else
            throw new IllegalStateException("Border must be vertical.");
    }

    public Axis getAxis() {
        return axis;
    }

    public double getAxisValue() {
        return axisValue;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double length() {
        return Math.sqrt( Math.pow(bPos.getX() - aPos.getX(), 2) +
                Math.pow(bPos.getZ() - aPos.getZ(), 2) );
    }

    public double distance(Location p) {

        if (axis == Axis.X) {
            double x = p.getX();
            return Math.abs(axisValue - x);
        }

        else {
            double z = p.getZ();
            return Math.abs(axisValue - z);
        }

    }

    public void forEachBlock(World world,
                             double y,
                             Consumer<Block> blockConsumer) {

        Vector start = new Vector(aPos.getX(), y, aPos.getZ());
        Vector end = new Vector(bPos.getX(), y, bPos.getZ());

        Vector dir = end.subtract(start);
        int len = (int) dir.length();

        BlockIterator it = new BlockIterator(world, start, dir, 0, len);
        Block b;
        while (it.hasNext()) {
            b = it.next();
            blockConsumer.accept(b);
        }

    }

    @Override
    public int compareTo(Border o) {

        if (o.axis == axis) {

            if (o.axisValue > axisValue)
                return 1;
            else if (o.axisValue < axisValue)
                return -1;
            return 0;

        }
        else
            throw new IllegalStateException("Cannot compare borders on different axis.");

    }
}

package pl.trollcraft.sectors.controller;

import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.model.geo.Pos;
import pl.trollcraft.sectors.model.sector.Sector;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SectorController {

    private static final Logger LOG
            = Logger.getLogger(SectorController.class.getSimpleName());

    private final Messenger messenger;
    private final ServerController serverController;

    public SectorController (Messenger messenger,
                             ServerController serverController) {

        this.messenger = messenger;
        this.serverController = serverController;
    }

    /**
     * This instance's sector.
     */
    private Sector sector;

    public Sector getSector() {
        return sector;
    }

    public boolean isSet() {
        return sector != null;
    }

    public boolean inSector(double x, double z) {
        if (!isSet())
            throw new IllegalStateException("Sector needs to be fetched first.");

        double xa = sector.getA().getX();
        double za = sector.getA().getZ();
        double xb = sector.getB().getX();
        double zb = sector.getB().getZ();

        boolean xs = (x < xa && x > xb) || (x < xb && x > xa);
        boolean ys = (z < za && z > zb) || (z < zb && z > za);

        return xs && ys;
    }

    public void fetch() {

        if (isSet()) {
            LOG.log(Level.WARNING, "Sector data already fetched.");
            return;
        }

        String groupName = serverController.getServer().getGroupName();
        String serverName = serverController.getServer().getSectorName();

        LOG.log(Level.INFO, "Fetching sector from central bungee server for name "
                + serverName + " from group " + groupName);

        String[] data = new String[]{
                groupName,
                serverName
        };

        messenger.forward((byte) 0, data, res -> {

            if (res[0].equals("OK")) {

                double x = Double.parseDouble(res[1]);
                double z = Double.parseDouble(res[2]);
                Pos a = new Pos(x, z);

                x = Double.parseDouble(res[3]);
                z = Double.parseDouble(res[4]);
                Pos b = new Pos(x, z);

                sector = new Sector(serverName, a, b);

                LOG.log(Level.INFO, "Fetched sector data for this instance. Setting borders...");

                sector.calculate();
                //sector.calculateDirections();

            }
            else
                throw new IllegalStateException("No server sector defined in central server for this instance.");

        });


    }

    public void delete() {
        if (isSet())
            sector = null;
        throw new NullPointerException("No sector data.");
    }

}

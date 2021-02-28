package pl.trollcraft.sectors.messaging.requests;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.model.Sector;

import java.util.Optional;

public class DetermineSectorRequest implements Request {

    private static final int ID = 1;

    private final SectorsController sectorsController;

    public DetermineSectorRequest(SectorsController sectorsController) {
        this.sectorsController = sectorsController;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public String[] process(String[] data) {

        double x = Double.parseDouble(data[0]);
        double z = Double.parseDouble(data[1]);

        Optional<Sector> oSector = sectorsController.get(x, z);

        if (oSector.isPresent()) {

            Sector sector = oSector.get();
            String status = "ONLINE";

            // Checking whether player would be able to
            // join the sector server.

            if (!sector.isOnline())
                status = "OFFLINE";

            else if (sector.getPlayers() >= sector.getMaxPlayers())
                status = "FULL";

            return new String[] {
                    "OK",
                    sector.getServerName(),
                    status
            };

        }

        return new String[] {
                "FAILED",
                "NO_SECTOR_FOUND"
        };

    }
}

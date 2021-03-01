package pl.trollcraft.sectors.messaging.requests;

import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.model.Sector;

import java.util.Optional;

public class GetSectorRequest implements Request {

    private static final int ID = 0;

    private final SectorsController sectorsController;

    public GetSectorRequest(SectorsController sectorsController) {
        this.sectorsController = sectorsController;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public String[] process(String[] data) {

        String groupName = data[0];
        String serverName = data[1];

        Optional<Sector> oSector = sectorsController.get(groupName, serverName);

        if (oSector.isPresent()) {

            Sector sector = oSector.get();

            return new String[] {
                    "OK",
                    String.valueOf(sector.getA().getX()),
                    String.valueOf(sector.getA().getZ()),
                    String.valueOf(sector.getB().getX()),
                    String.valueOf(sector.getB().getZ()),
            };

        }

        return new String[] {
                "FAILED"
        };
    }
}

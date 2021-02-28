package pl.trollcraft.sectors.messaging.requests;

import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.model.Sector;
import redis.clients.jedis.Jedis;

import java.util.List;

public final class GetSectorsReportRequest implements Request {

    private static final int ID = 2;

    private final Jedis jedis;
    private final SectorsController sectorsController;

    public GetSectorsReportRequest(Jedis jedis,
                                   SectorsController sectorsController) {

        this.jedis = jedis;
        this.sectorsController = sectorsController;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public String[] process(String[] data) {

        List<Sector> sectors = sectorsController.getSectors();

        Sector sector;
        String[] report = new String[sectors.size()];
        for (int i = 0 ; i < sectors.size() ; i++) {

            sector = sectors.get(i);

            if (!sector.isOnline())
                report[i] = sector.getServerName() + ";offline";

            else {

                //TODO handle missing redis information

                String tps1 = jedis.get(String.format("%s.tps.1", sector.getServerName()));
                String tps5 = jedis.get(String.format("%s.tps.5", sector.getServerName()));
                String tps15 = jedis.get(String.format("%s.tps.15", sector.getServerName()));

                int maxPlayer = sector.getMaxPlayers();
                int players = sector.getPlayers();

                report[i] = sector.getServerName() + ";online;"
                        + tps1 + ";" + tps5 + ";" + tps15 + ";"
                        + players + ";" + maxPlayer;

            }

        }

        return report;

    }
}

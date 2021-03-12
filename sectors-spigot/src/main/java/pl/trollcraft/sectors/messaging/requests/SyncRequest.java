package pl.trollcraft.sectors.messaging.requests;

import org.bukkit.Bukkit;
import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.model.event.SyncEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A request pointing server to sync itself
 * with data received from the central server.
 *
 * @author Jakub Zelmanowicz
 */
public class SyncRequest implements Request {

    private static final Logger LOG
            = Logger.getLogger(SyncRequest.class.getSimpleName());

    private static final byte ID = 0;

    @Override
    public byte id() {
        return ID;
    }

    @Override
    public String[] process(String[] data) {

        for (String d : data) {

            LOG.log(Level.INFO, "Data: " + d);

            if (!d.contains(";"))
                throw new IllegalArgumentException("Invalid data format. Expected [command;args]");

            String[] dataArr = d.split(";");
            String command = dataArr[0];
            String[] args;
            if (dataArr[1].contains(":"))
                args = dataArr[1].split(":");
            else
                args = new String[] {dataArr[1]};

            SyncEvent syncEvent = new SyncEvent(command, args);
            Bukkit.getPluginManager().callEvent(syncEvent);

        }

        return new String[] {"OK"};
    }
}

package pl.trollcraft.sectors.messaging.requests;

import pl.trollcraft.sectors.messaging.Request;
import pl.trollcraft.sectors.messaging.sync.Synchronizer;
import pl.trollcraft.sectors.messaging.sync.SynchronizerController;

import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncSectorsRequest implements Request {

    private static final Logger LOG
            = Logger.getLogger(SyncSectorsRequest.class.getSimpleName());

    private static final int ID = 3;

    private final SynchronizerController synchronizerController;

    public SyncSectorsRequest(SynchronizerController synchronizerController) {
        this.synchronizerController = synchronizerController;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public String[] process(String from, String[] data) {

        LOG.log(Level.INFO, "Received sync request from sector " + from);

        String groupName = data[0];
        Optional<Synchronizer> oSync = synchronizerController.get(groupName);
        if (!oSync.isPresent())
            return new String[] {"FAILED", "UNKNOWN GROUP"};

        Synchronizer sync = oSync.get();
        String[] commands = Arrays.copyOfRange(data, 1, data.length);

        synchronizerController.push(sync, commands, /*Skipping sector the request came from*/ from);
        LOG.log(Level.INFO, "Messages pushed.");

        return new String[] {"OK"};
    }

}

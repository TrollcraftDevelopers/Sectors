package pl.trollcraft.sectors.messaging.sync;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Listens and calls synchronization
 * when a server is ready.
 *
 * @author Jakub Zelmanowicz
 */
public class SynchronizerListener implements Listener {

    private static final Logger LOG
            = Logger.getLogger(SynchronizerListener.class.getSimpleName());

    private final SynchronizerController synchronizerController;
    private final SectorsController sectorsController;

    public SynchronizerListener(SynchronizerController synchronizerController,
                                SectorsController sectorsController) {

        this.synchronizerController = synchronizerController;
        this.sectorsController = sectorsController;
    }

    @EventHandler
    public void onSwitch (ServerConnectedEvent event) {

        ServerInfo info = event.getServer().getInfo();

        if (info.getPlayers().size() == 0) {

            LOG.info("A player connected. Checking if any synchronization is needed.");
            String sectorName = info.getName();

            Optional<SectorsGroup> oGroup = sectorsController.getSectorsGroup(sectorName);

            if (!oGroup.isPresent()){
                LOG.info("No group of sector.");
                return;
            }

            SectorsGroup group = oGroup.get();

            if (synchronizerController.isSyncAvailable(group.getName(), sectorName)) {

                LOG.info("Synchronization is available. Sending sync request to " + sectorName);
                synchronizerController.sync(group.getName(), sectorName);

            }

        }



    }

}

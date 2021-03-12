package pl.trollcraft.sectors.messaging.sync;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import pl.trollcraft.sectors.config.ConfigProvider;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.messaging.Messenger;
import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SynchronizerController {

    private static final Logger LOG
            = Logger.getLogger(SynchronizerController.class.getSimpleName());

    private final Messenger messenger;
    private final SectorsController sectorsController;

    private final List<Synchronizer> synchronizerList;

    public SynchronizerController(Messenger messenger,
                                  SectorsController sectorsController) {

        this.messenger = messenger;
        this.sectorsController = sectorsController;

        synchronizerList = new ArrayList<>();
    }

    public void register(Synchronizer synchronizer) {
        synchronizerList.add(synchronizer);
    }

    public Optional<Synchronizer> get(String groupName) {
        return synchronizerList.stream()
                .filter( synchronizer -> synchronizer.getSectorsGroup().getName().equals(groupName) )
                .findFirst();
    }

    public Queue<String> get(String groupName, Sector sector) {
        Synchronizer sync = synchronizerList.stream()
                .filter(synchronizer -> synchronizer.getSectorsGroup().getName().equals(groupName))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("Unknown group name.") );

        if (!sync.getMessages().containsKey(sector))
            throw new IllegalStateException("The sector does not belong to the provided group.");

        return sync.getMessages().get(sector);
    }

    /**
     * Pushes a message to sync with
     * every sector.
     *
     * @param message - message to send to every sector.
     */
    public void push(Synchronizer synchronizer, String[] message, String exclude) {

        LOG.log(Level.INFO, "Pushing a new message.");

        for (String s : message)
            LOG.info(s);

        synchronizer.getMessages().forEach( (sec, mes) -> {

            if (sec.getServerName().equals(exclude)) {
                LOG.info("Skipping sector " + sec.getServerName());
                return;
            }

            mes.addAll(Arrays.asList(message));

            int players = sec.getServerInfo().getPlayers().size();

            if (players == 0)
                LOG.log(Level.INFO, "Cannot push to sector " + sec.getServerName() +
                        " due to no players. Waiting for any player to show up.");
            else {

                //TODO replace with sync method.
                String[] data = new String[mes.size()];
                int endPoint = mes.size();

                int ind = 0;
                for (String s : mes)
                    data[ind++] = s;

                messenger.forward((byte) 0, sec.getServerInfo(), data, res -> {

                    LOG.log(Level.INFO, "Syncing sector " + sec.getServerName() + " successful. Removing messages.");
                    for (int i = 0 ; i < endPoint ; i++)
                        mes.poll();

                });

            }

        } );

    }

    /**
     * Checks whether any data is available
     * to synchronize the sector.
     *
     * @param groupName - name of the group of the sector,
     * @param sectorName - name of the sector
     * @return true if there are any updates, false otherwise.
     */
    public boolean isSyncAvailable(String groupName, String sectorName) {

        Optional<Sector> oSector = sectorsController.getSector(sectorName);
        if (!oSector.isPresent())
            return false;

        Optional<Synchronizer> oSynchronizer = get(groupName);
        if (!oSynchronizer.isPresent())
            return false;

        Synchronizer synchronizer = oSynchronizer.get();
        Sector sector = oSector.get();

        if (!synchronizer.getMessages().containsKey(sector))
            return false;

        Queue<String> messages = synchronizer.getMessages().get(sector);
        return !messages.isEmpty();

    }

    /**
     * Performs a synchronization of a sector.
     *
     * @param groupName - group of the sector,
     * @param sectorName - sector name.
     */
    public void sync(String groupName, String sectorName) {

        Sector sector = sectorsController.get(groupName, sectorName)
                .orElseThrow( () -> new IllegalStateException("Group does not exists or the sector does not belong to the group.") );

        Queue<String> messages = get(groupName, sector);
        for (String s : messages)
            LOG.info("To sync to " + sectorName + ": " + s);

        String[] data = new String[messages.size()];
        int endPoint = messages.size();

        int ind = 0;
        for (String s : messages)
            data[ind++] = s;

        messenger.forward((byte) 0, sector.getServerInfo(), data, res -> {

            LOG.log(Level.INFO, "Syncing sector " + sector.getServerName() + " successful. Removing messages.");
            messages.clear();
            /*for (int i = 0 ; i < endPoint ; i++)
                messages.poll();*/

        });

    }

    public void load(ConfigProvider provider) {

        Configuration conf = provider.getConf();

        LOG.info("Loading saved sync messages...");
        synchronizerList.forEach( synchronizer -> {

            String group = synchronizer.getSectorsGroup().getName();
            LOG.info("Loading " + group + ".");

            if (!conf.contains("sync." + group))
                return;

            synchronizer.getSectorsGroup().getSectors().forEach( sector -> {

                String sectorName = sector.getServerName();
                LOG.info("Loading " + sectorName + ".");

                if (!conf.contains("sync." + group + "." + sectorName))
                    return;

                List<String> messages = conf.getStringList("sync." + group + "." + sectorName);
                synchronizer.getMessages().get(sector).addAll(messages);

            } );

        } );

    }

    @Deprecated
    public void _load(ConfigProvider provider) {

        LOG.info("Loading saved sync messages...");

        Configuration sync = provider.read("sync", Configuration.class);
        sync.getKeys().forEach( groupName -> {

            LOG.info("Loading " + groupName + ".");

            SectorsGroup sectorsGroup = sectorsController.get(groupName)
                    .orElseThrow( () -> new IllegalStateException("No sector group of name " + groupName) );

            Synchronizer synchronizer = new Synchronizer(sectorsGroup);

            Configuration group = provider.read(String.format("sync.%s", groupName), Configuration.class);
            group.getKeys().forEach( serverName -> {

                LOG.info("Loading " + serverName + ".");

                Sector sector = sectorsController.getSector(serverName)
                        .orElseThrow( () -> new IllegalStateException("No sector of name " + serverName) );

                if (!synchronizer.getMessages().containsKey(sector))
                    return;

                List<String> messages = provider.getConf().getStringList(String.format("sync.%s.%s", groupName, serverName));
                messages.forEach( message -> {
                    LOG.info(groupName + "." + serverName + ": " + message);
                    synchronizer.getMessages().get(sector).add(message);
                });

            } );

            register(synchronizer);
            LOG.info("Synchronizer " + groupName + " has been loaded.");

        } );

    }

    //TEST IT
    public boolean save(ConfigProvider provider) {

        synchronizerList.forEach( synchronizer -> {

            String group = synchronizer.getSectorsGroup().getName();
            synchronizer.getMessages().forEach( (server, messages) ->
                provider.write(String.format("sync.%s.%s", group, server.getServerName()), messages)
            );

        } );

        return provider.save();

    }

}

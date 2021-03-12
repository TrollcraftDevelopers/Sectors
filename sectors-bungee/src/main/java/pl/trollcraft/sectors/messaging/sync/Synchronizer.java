package pl.trollcraft.sectors.messaging.sync;

import pl.trollcraft.sectors.model.Sector;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Synchronizes information accross
 * sectors in a group.
 *
 * It detects if central server is able
 * to send data to synchronizer, if not
 * - it waits for any player to join.
 *
 * @author Jakub Zelmanowicz
 */
public class Synchronizer {

    /**
     * Group to perform synchronization on.
     */
    private final SectorsGroup sectorsGroup;

    /**
     * Messages to push to the sectors.
     */
    private final Map<Sector, Queue<String>> messages;

    public Synchronizer(SectorsGroup sectorsGroup) {
        this.sectorsGroup = sectorsGroup;

        // Initiating sectors queues.
        messages = new HashMap<>();
        this.sectorsGroup.getSectors().forEach( sector ->
            messages.put(sector, new LinkedList<>())
        );
    }

    public SectorsGroup getSectorsGroup() {
        return sectorsGroup;
    }

    public Map<Sector, Queue<String>> getMessages() {
        return messages;
    }
}

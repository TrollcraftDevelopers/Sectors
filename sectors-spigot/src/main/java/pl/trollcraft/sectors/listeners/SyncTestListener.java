package pl.trollcraft.sectors.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pl.trollcraft.sectors.model.event.SyncEvent;

// Test class
// Test class
// Test class
public class SyncTestListener implements Listener {

    @EventHandler
    public void onSync(SyncEvent event) {
        Bukkit.broadcastMessage("Synchronizacja danych z serwera centralnego.");
        Bukkit.broadcastMessage("Polecenie: " + event.getCommand());
        Bukkit.broadcastMessage("Argumenty polecenia:");
        for (String arg : event.getArgs()) {
            Bukkit.broadcastMessage(arg);
        }
    }

}

package pl.trollcraft.sectors.listener;

import com.google.common.annotations.Beta;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import pl.trollcraft.sectors.controller.SectorsController;
import pl.trollcraft.sectors.model.SectorsGroup;

import java.util.Optional;
import java.util.logging.Logger;

@Deprecated
public class GroupSwitchListener implements Listener {

    private static final Logger LOG
            = Logger.getLogger(GroupSwitchListener.class.getSimpleName());

    private SectorsController sectorsController;

    @EventHandler
    public void onSwitch (ServerSwitchEvent event) {

        String from = event.getFrom().getName();
        String to = event.getPlayer().getName();

        Optional<SectorsGroup> fromGroup = sectorsController.getSectorsGroup(from);
        Optional<SectorsGroup> toGroup = sectorsController.getSectorsGroup(to);

        String fromGroupName = null,
               toGroupName = null;

        if (fromGroup.isPresent())
            fromGroupName = fromGroup.get().getName();

        if (toGroup.isPresent())
            toGroupName  = toGroup.get().getName();

        if (fromGroupName == null && toGroupName == null)
            return;



    }

}

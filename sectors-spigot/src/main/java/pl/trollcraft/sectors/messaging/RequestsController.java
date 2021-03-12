package pl.trollcraft.sectors.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestsController implements PluginMessageListener {

    private static final Logger LOG
            = Logger.getLogger(RequestsController.class.getSimpleName());

    private static final String REQ_CHANNEL = "tc:sectorsrequests";

    private final Plugin plugin;
    private final List<Request> bungeeRequests;

    public RequestsController(Plugin plugin) {
        this.plugin = plugin;
        bungeeRequests = new ArrayList<>();

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, REQ_CHANNEL);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, REQ_CHANNEL, this);
    }

    public void register(Request request) {
        bungeeRequests.add(request);
    }

    public Optional<Request> get(byte id) {
        return bungeeRequests.stream()
                .filter(bungeeRequest -> id == bungeeRequest.id())
                .findFirst();
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {

        if (channel.equals(REQ_CHANNEL)) {

            LOG.log(Level.INFO, "Received a request.");

            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

            byte requestId = in.readByte();
            UUID id = UUID.fromString(in.readUTF());

            Optional<Request> oRequest = get(requestId);

            if (oRequest.isPresent()) {

                byte l = in.readByte();
                String[] data = new String[l];
                for (byte i = 0 ; i < l ; i++)
                    data[i] = in.readUTF();

                String[] response = oRequest.get().process(data);
                LOG.log(Level.INFO, "Processed request.");

                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF(id.toString());
                out.writeByte(response.length);
                for (String r : response)
                    out.writeUTF(r);

                Bukkit.getOnlinePlayers().stream()
                        .findFirst()
                        .ifPresent( p -> {

                            p.sendPluginMessage(plugin, REQ_CHANNEL, out.toByteArray());
                            LOG.log(Level.INFO, "Forwarded the message successfully.");

                        }  );

            }
            else
                LOG.log(Level.WARNING, "Dropping request due to unknown id.");

        }

    }
}

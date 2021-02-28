package pl.trollcraft.sectors.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Messenger implements PluginMessageListener {

    private static final Logger LOG
            = Logger.getLogger(Messenger.class.getSimpleName());

    private static final String CHANNEL = "tc:sectors";

    private final Plugin plugin;
    private final List<ServerRequest> serverRequests;

    public Messenger(Plugin plugin) {

        this.plugin = plugin;
        serverRequests = new LinkedList<>();

        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    public void forward(int requestId,
                        String[] message,
                        Consumer<String[]> callback) {

        if (Bukkit.getOnlinePlayers().size() < 1)
            throw new IllegalStateException("No players to forward a message.");

        UUID id = UUID.randomUUID();
        ServerRequest serverRequest = new ServerRequest(id, callback);
        serverRequests.add(serverRequest);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeInt(requestId);
        out.writeUTF(id.toString());
        out.writeInt(message.length);
        for (String m : message)
            out.writeUTF(m);

        Bukkit.getOnlinePlayers().stream()
                .findFirst()
                .ifPresent( p -> {

                    p.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
                    LOG.log(Level.INFO, "Forwarded the message successfully.");

                }  );

        LOG.log(Level.INFO, "Tried to forward the message " + id.toString());

    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

        if (s.equals(CHANNEL)) {

            LOG.log(Level.INFO, "Received message.");

            ByteArrayDataInput in = ByteStreams.newDataInput(bytes);

            UUID id = UUID.fromString(in.readUTF());
            Optional<ServerRequest> oReq = serverRequests.stream()
                    .filter(serverRequest -> serverRequest.getId().equals(id))
                    .findFirst();

            LOG.log(Level.INFO, "Message id is " + id.toString());

            if (oReq.isPresent()) {

                LOG.log(Level.INFO, "Processing message " + id.toString());

                ServerRequest r = oReq.get();
                serverRequests.remove(r);

                int l = in.readInt();
                String[] message = new String[l];

                LOG.log(Level.INFO, "Message length is " + l);

                for (int i = 0 ; i < l ; i++) {
                    message[i] = in.readUTF();
                    LOG.log(Level.INFO, message[i]);
                }

                LOG.log(Level.INFO, "Calling callback consumer for " + id.toString());
                r.getCallback().accept(message);

            }
            else
                LOG.log(Level.WARNING, "Dropping message " + id.toString());

        }

    }

}

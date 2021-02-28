package pl.trollcraft.sectors.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Messenger implements Listener {

    private static final Logger LOG
            = Logger.getLogger(Messenger.class.getTypeName());

    private static final String CHANNEL = "tc:sectors";
    private static final String REQ_CHANNEL = "tc:sectorsrequests";

    private final List<ServerRequest> requests;
    private final RequestsController requestsController;

    public Messenger(Plugin plugin,
                     RequestsController requestsController) {

        plugin.getProxy().registerChannel(CHANNEL);
        plugin.getProxy().registerChannel(REQ_CHANNEL);

        plugin.getProxy()
                .getPluginManager()
                .registerListener(plugin, this);

        requests = new LinkedList<>();
        this.requestsController = requestsController;
    }

    /**
     * Forwards a response message to a certain server.
     *
     * @param serverInfo - server to forward data to,
     * @param data - data to forward to server.
     */
    public void forward(ServerInfo serverInfo,
                        UUID uuid,
                        String... data){

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF(uuid.toString());
        out.writeInt(data.length);
        for (String d : data)
            out.writeUTF(d);

        serverInfo.sendData(CHANNEL, out.toByteArray());
        LOG.log(Level.INFO, "Forwarded message to server " + serverInfo.getName());
    }

    /**
     * Forwards a response message to a certain server.
     *
     * @param serverInfo - server to forward data to,
     * @param data - data to forward to server.
     */
    public void forward(byte requestId,
                        ServerInfo serverInfo,
                        String[] data,
                        Consumer<String[]> callback){

        UUID id = UUID.randomUUID();
        ServerRequest request = new ServerRequest(id, callback);
        requests.add(request);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeInt(requestId);
        out.writeUTF(id.toString());

        out.writeByte(data.length);
        for (String d : data)
            out.writeUTF(d);

        serverInfo.sendData(REQ_CHANNEL, out.toByteArray());
        LOG.log(Level.INFO, "Forwarded request to server " + serverInfo.getName());
    }

    /**
     * Handles incoming server request.
     * Format:
     *
     * - Request byte ID,
     * - Request UUID,
     * - Data length,
     * - Data (String Array)
     *
     * @param event - instance of plugin event.
     */
    @EventHandler
    public void onMessage (PluginMessageEvent event) {

        String channel = event.getTag();
        if (channel.equals(CHANNEL)) {

            LOG.log(Level.INFO, "Received a request.");

            ByteArrayDataInput in = ByteStreams
                    .newDataInput(event.getData());

            int requestId = in.readInt();
            UUID uuid = UUID.fromString(in.readUTF());

            int l = in.readInt();
            String[] data = new String[l];
            for (int i = 0 ; i < l ; i++)
                data[i] = in.readUTF();

            requestsController.get(requestId).ifPresent( req -> {

                String[] res = req.process(data);

                Server server = (Server) event.getSender();
                ServerInfo info = server.getInfo();

                forward(info, uuid, res);
                LOG.log(Level.INFO, "Forwarded response message to " + uuid);

            } );

        }

        else if (channel.equals(REQ_CHANNEL)) {

            ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());

            UUID id = UUID.fromString(in.readUTF());
            Optional<ServerRequest> oReq = requests.stream()
                    .filter(serverRequest -> serverRequest.getId().equals(id))
                    .findFirst();

            LOG.log(Level.INFO, "Message id is " + id.toString());

            if (oReq.isPresent()) {

                LOG.log(Level.INFO, "Processing message " + id.toString());

                ServerRequest r = oReq.get();
                requests.remove(r);

                int l = in.readInt();
                String[] message = new String[l];

                LOG.log(Level.INFO, "Message length is " + l);

                for (byte i = 0 ; i < l ; i++) {
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

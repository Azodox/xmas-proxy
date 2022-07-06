package fr.olten.proxy.listener;

import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import fr.olten.proxy.ProxyPlugin;

import java.util.concurrent.TimeUnit;

public class ServerPreConnectListener {

    private final ProxyPlugin plugin;
    public ServerPreConnectListener(ProxyPlugin proxyPlugin) {
        this.plugin = proxyPlugin;
    }

    @Subscribe
    public void onPreConnect(ServerPreConnectEvent event){
        if(event.getOriginalServer().getServerInfo().getName().equals(plugin.getConfig().getString("ServersName.survie"))){
            plugin.getServer().getScheduler()
                    .buildTask(plugin, () -> event.getPlayer().getCurrentServer().ifPresent(serverConnection -> {
                        if (serverConnection.getServerInfo().getName().equals(plugin.getConfig().getString("ServersName.lobby"))) {
                            var out = ByteStreams.newDataOutput();
                            out.writeUTF("UnableToConnectTo");
                            out.writeUTF(event.getOriginalServer().getServerInfo().getName());
                            out.writeUTF(event.getPlayer().getUniqueId().toString());
                            serverConnection.sendPluginMessage(plugin.getBungeecordChannel(), out.toByteArray());
                        }
                    }))
                    .delay(500, TimeUnit.MILLISECONDS).schedule();
        }
    }
}

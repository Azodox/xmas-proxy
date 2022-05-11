package fr.olten.proxy.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import fr.olten.proxy.ProxyPlugin;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ServerConnectedListener {

    private final ProxyPlugin plugin;

    public ServerConnectedListener(ProxyPlugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onServerConnected(ServerConnectedEvent event){
        var player = event.getPlayer();
        var previousServer = event.getPreviousServer();
        var server = event.getServer();

        previousServer.ifPresent(s -> {
            var previousServerName = s.getServerInfo().getName();
            if(previousServerName.equals(plugin.getConfig().getString("ServersName.survie"))){
                if(server.getServerInfo().getName().equals(plugin.getConfig().getString("ServersName.lobby"))){
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Incoming");
                    out.writeUTF(player.getUniqueId().toString());

                    plugin.getServer().getScheduler().buildTask(plugin, () -> server.sendPluginMessage(plugin.getBungeecordChannel(), out.toByteArray())).delay(500, TimeUnit.MILLISECONDS).schedule();
                }
            }
        });
    }
}

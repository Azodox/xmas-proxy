package fr.olten.proxy.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import fr.olten.proxy.ProxyPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.nio.file.Paths;

public class PingListener {

    private final ProxyPlugin proxyPlugin;
    public PingListener(ProxyPlugin proxyPlugin) {
        this.proxyPlugin = proxyPlugin;
    }

    @Subscribe
    public void onPing(ProxyPingEvent event) throws IOException {
        event.setPing(ServerPing.builder().
                favicon(Favicon.create(Paths.get(proxyPlugin.getDataDirectory() + "/favicon.png")))
                .description(
                        MiniMessage.miniMessage().deserialize("   <color:#FFFFFF>✳   ✳      <color:#FF0000>O<color:#BA0C0C>L<color:#ECFFEB>T<color:#2A850E>E<color:#27A300>N</color> <color:#FFFFFF><bold>✳</bold> <color:#FF0000>X<color:#BA0C0C>M<color:#ECFFEB>A<color:#2A850E>S</color><color:#FFFFFF>  <color:#FFFFFF><bold><italic>✳</italic></bold>  <color:dark_aqua>2<color:aqua>0<color:dark_aqua>2<color:aqua>2</color>  <color:#FFFFFF> ✳   ✳\n" +
                                "   <color:#FFFFFF><bold><italic>✳</italic></bold>     <color:#FFFFFF><bold><italic>✳</italic></bold>       <color:gray>A<color:#ECFFEB>m<color:gray>u<color:#ECFFEB>s<color:gray>e<color:#ECFFEB>z <color:gray>v<color:#ECFFEB>o<color:gray>u<color:#ECFFEB>s b<color:#ECFFEB>i<color:gray>e<color:#ECFFEB>n<color:gray>!</color><color:#FFFFFF>  <color:#FFFFFF><bold>✳</bold>  <color:#FFFFFF><italic>✳</italic>     <color:#FFFFFF><bold>✳</bold>")
                )
                .version(new ServerPing.Version(758, "1.18.2"))
                .onlinePlayers(proxyPlugin.getServer().getPlayerCount()).maximumPlayers(2022).build());
    }
}

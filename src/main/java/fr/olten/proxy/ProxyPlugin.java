package fr.olten.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.olten.proxy.listener.PingListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Plugin(id = "xmas-proxy", name = "Proxy", version = "0.1.0")
public class ProxyPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public ProxyPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;

        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }

        this.dataDirectory = dataDirectory;

        logger.info("Hi there!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event){
        server.getEventManager().register(this, new PingListener(this));
        server.getScheduler().buildTask(this, new Runnable() {

            private int i = 0;

            @Override
            public void run() {
                if(i >= 6)
                    i--;
                else
                    i++;

                server.sendPlayerListHeader(
                        MiniMessage.miniMessage().deserialize(String.format("<rainbow:%s>OLTEN XMAS 2022</rainbow>\n", i)).decorate(TextDecoration.BOLD));
            }
        }).repeat(500, TimeUnit.MILLISECONDS).schedule();
        server.getScheduler().buildTask(this, new Runnable() {

            private final static String[] messages = {
                    "Tourne la roulette au spawn pour\nobtenir des récompense chaque jour !",
                    "Envie de faire une balade ?\nDes chevaux sont à ta disposition au village de noël !",
                    "Une quantité énorme de secrets sont\ncachés dans le village de noël...",
            };

            private int messageIndex = 0;

            @Override
            public void run() {
                server.sendPlayerListFooter(
                        Component.text("\nAMUSEZ VOUS BIEN !").color(TextColor.fromHexString(randomHex())).decorate(TextDecoration.BOLD)
                                .append(Component.text("\n" + messages[messageIndex]).color(NamedTextColor.WHITE).decorate(TextDecoration.ITALIC))
                );

                messageIndex++;
                if(messageIndex >= messages.length){
                    messageIndex = 0;
                }
            }

            private String randomHex(){
                var r = new Random();
                int rand_num = r.nextInt(0xffffff + 1);
                return String.format("#%06x", rand_num);
            }


        }).repeat(4, TimeUnit.SECONDS).schedule();
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public ProxyServer getServer() {
        return server;
    }
}

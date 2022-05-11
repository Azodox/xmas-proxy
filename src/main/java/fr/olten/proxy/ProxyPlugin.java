package fr.olten.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import fr.olten.proxy.listener.PingListener;
import fr.olten.proxy.listener.ServerConnectedListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Plugin(id = "xmas-proxy", name = "Proxy", version = "0.1.0")
public class ProxyPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private TomlParseResult config;
    private ChannelIdentifier bungeecordChannel;

    @Inject
    public ProxyPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) throws IOException {
        this.server = server;
        this.logger = logger;

        if(!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }

        this.dataDirectory = dataDirectory;
        this.initConfig();

        logger.info("Hi there!");
    }

    private void initConfig() throws IOException {
        var baseConfig = new File("config.toml");
        var config = new File(dataDirectory.toFile(), "config.toml");

        if(!config.exists()) {
            config.createNewFile();
        }

        if(!FileUtils.contentEquals(config, baseConfig) && config.length() != 0){
            logger.info("File '" + config.getPath() + "' content doesn't equals to '" + baseConfig.getPath() + "' and is not empty, skipping files copy.");
        }else {
            InputStream in = getClass().getResourceAsStream("/" + config.getName());
            FileOutputStream out = new FileOutputStream(config);

            byte[] buf = new byte[1024];
            int n;

            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            }

            in.close();
            out.close();
            logger.info("File '" + config.getPath() + "' copied from resources.");
        }

        this.config = Toml.parse(Paths.get(this.getDataDirectory() + File.separator + config.getName()));
        this.config.errors().forEach(error -> System.err.println(error.toString()));
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event){
        this.bungeecordChannel = MinecraftChannelIdentifier.create("xmas", "lobbysurvie");
        server.getChannelRegistrar().register(bungeecordChannel);

        server.getEventManager().register(this, new PingListener(this));
        server.getEventManager().register(this, new ServerConnectedListener(this));
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

    public TomlParseResult getConfig() {
        return config;
    }

    public ChannelIdentifier getBungeecordChannel() {
        return bungeecordChannel;
    }
}

package com.vecoo.extrartp;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.RTPListener;
import com.vecoo.extrartp.util.PermissionNodes;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.slf4j.Logger;

import java.nio.file.Path;

@Mod(ExtraRTP.MOD_ID)
public class ExtraRTP {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static ExtraRTP instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;

    private MinecraftServer server;

    public ExtraRTP() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RTPListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.registerPermission(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RandomTeleportCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        this.server = event.getServer();
    }

    public void loadConfig() {
        this.serverConfig = YamlConfigFactory.load(ServerConfig.class, Path.of("config/ExtraRTP/config.yml"));
        this.localeConfig = YamlConfigFactory.load(LocaleConfig.class, Path.of("config/ExtraRTP/locale.yml"));
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getServerConfig() {
        return instance.serverConfig;
    }

    public LocaleConfig getLocaleConfig() {
        return instance.localeConfig;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}
package com.vecoo.extrartp;

import com.mojang.logging.LogUtils;
import com.vecoo.extralib.config.YamlConfigFactory;
import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.RTPListener;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.nio.file.Path;

public class ExtraRTP implements ModInitializer {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogUtils.getLogger();

    @Getter
    private static ExtraRTP instance;

    private ServerConfig serverConfig;
    private LocaleConfig localeConfig;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        loadConfig();

        CommandRegistrationCallback.EVENT.register(RandomTeleportCommand::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerPlayConnectionEvents.JOIN.register(RTPListener::onPlayerJoin);
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
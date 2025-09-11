package com.vecoo.extrartp;

import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.PlayerJoinListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraRTP implements ModInitializer {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogManager.getLogger("ExtraRTP");

    private static ExtraRTP instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private MinecraftServer server;

    @Override
    public void onInitialize() {
        instance = this;

        loadConfig();

        CommandRegistrationCallback.EVENT.register(RandomTeleportCommand::register);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> this.server = server);
        ServerPlayConnectionEvents.JOIN.register(PlayerJoinListener::onPlayerJoin);
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraRTP] Error load config.", e);
        }
    }

    public static ExtraRTP getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return LOGGER;
    }

    public ServerConfig getConfig() {
        return instance.config;
    }

    public LocaleConfig getLocale() {
        return instance.locale;
    }

    public MinecraftServer getServer() {
        return instance.server;
    }
}
package com.vecoo.extrartp;

import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.RTPListener;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraRTP.MOD_ID)
public class ExtraRTP {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogManager.getLogger();

    private static ExtraRTP instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private MinecraftServer server;

    public ExtraRTP() {
        instance = this;

        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RTPListener());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        this.server = event.getServer();

        PermissionAPI.registerNode("minecraft.command.rtp", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.rtp.dimension", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.rtp.dimension.player", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.rtp.cooldown", DefaultPermissionLevel.OP, "");
        PermissionAPI.registerNode("minecraft.command.rtp.reload", DefaultPermissionLevel.OP, "");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        RandomTeleportCommand.register(event.getDispatcher());
    }

    public void loadConfig() {
        try {
            this.config = new ServerConfig();
            this.config.init();
            this.locale = new LocaleConfig();
            this.locale.init();
        } catch (Exception e) {
            LOGGER.error("Error load config.", e);
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
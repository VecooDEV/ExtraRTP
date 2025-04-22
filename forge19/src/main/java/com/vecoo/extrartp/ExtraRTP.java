package com.vecoo.extrartp;

import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.RTPListener;
import com.vecoo.extrartp.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod(ExtraRTP.MOD_ID)
public class ExtraRTP {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogManager.getLogger("ExtraRTP");

    private static ExtraRTP instance;

    private ServerConfig config;
    private LocaleConfig locale;

    private MinecraftServer server;

    public ExtraRTP() {
        instance = this;

        this.loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RTPListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.permissionList.add(PermissionNodes.RANDOMTELEPORT_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.RANDOMTELEPORT_COOLDOWN);
        PermissionNodes.permissionList.add(PermissionNodes.RANDOMTELEPORT_RELOAD_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.RANDOMTELEPORT_DIMENSION_COMMAND);
        PermissionNodes.permissionList.add(PermissionNodes.RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND);

        event.addNodes(new ArrayList<>(PermissionNodes.permissionList));
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
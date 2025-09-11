package com.vecoo.extrartp;

import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.listener.RTPListener;
import com.vecoo.extrartp.util.PermissionNodes;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

        loadConfig();

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new RTPListener());
    }

    @SubscribeEvent
    public void onPermissionGather(PermissionGatherEvent.Nodes event) {
        PermissionNodes.PERMISSION_LIST.add(PermissionNodes.RANDOMTELEPORT_COMMAND);
        PermissionNodes.PERMISSION_LIST.add(PermissionNodes.RANDOMTELEPORT_COOLDOWN);
        PermissionNodes.PERMISSION_LIST.add(PermissionNodes.RANDOMTELEPORT_RELOAD_COMMAND);
        PermissionNodes.PERMISSION_LIST.add(PermissionNodes.RANDOMTELEPORT_DIMENSION_COMMAND);
        PermissionNodes.PERMISSION_LIST.add(PermissionNodes.RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND);

        for (PermissionNode<?> node : PermissionNodes.PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
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
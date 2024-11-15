package com.vecoo.extrartp;

import com.vecoo.extralib.permission.UtilPermissions;
import com.vecoo.extrartp.command.RandomTeleportCommand;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.PermissionConfig;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ExtraRTP.MOD_ID)
public class ExtraRTP {
    public static final String MOD_ID = "extrartp";
    private static final Logger LOGGER = LogManager.getLogger("ExtraRTP");

    private static ExtraRTP instance;

    private ServerConfig config;
    private LocaleConfig locale;
    private PermissionConfig permission;

    public ExtraRTP() {
        instance = this;

        this.loadConfig();

        UtilPermissions.registerPermission(permission.getPermissionCommand());

        MinecraftForge.EVENT_BUS.register(this);
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
            this.permission = new PermissionConfig();
            this.permission.init();
        } catch (Exception e) {
            LOGGER.error("[ExtraRTP] Error load config.");
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

    public PermissionConfig getPermission() {
        return instance.permission;
    }
}
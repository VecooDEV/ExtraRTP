package com.vecoo.extrartp.config;

import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

@Getter
@ConfigSerializable
public class LocaleConfig {
    private String reload = "&e(!) Configs reloaded.";
    private String successfulTeleport = "&e(!) You have been successfully teleported dimension %dimension%.";
    private String successfulTeleportPlayer = "&e(!) You have been successfully teleported player %player% dimension %dimension%.";
    private String cooldownTeleport = "&e(!) Random teleportation on cooldown, %cooldown% seconds remaining.";

    private String errorReload = "&c(!) Reload error, checking console and fixes config.";
    private String dimensionBlacklist = "&c(!) The dimension %dimension% is blacklisted.";
    private String notDimensionFound = "&c(!) Dimension %dimension% not found.";
    private String failedTeleport = "&c(!) Random teleportation failed, try again /rtp!";
}
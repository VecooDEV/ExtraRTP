package com.vecoo.extrartp.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrartp.ExtraRTP;

public class LocaleConfig {
    private String configReload = "&e(!) Configs reloaded.";
    private String successfulTeleport = "&e(!) You have been successfully teleported dimension %dimension%.";
    private String successfulTeleportPlayer = "&e(!) You have been successfully teleported player %player% dimension %dimension%.";
    private String cooldownTeleport = "&e(!) Random teleportation on cooldown, %cooldown% seconds remaining.";

    private String dimensionBlacklist = "&c(!) The dimension %dimension% is blacklisted.";
    private String notDimensionFound = "&c(!) Dimension %dimension% not found.";
    private String failedTeleport = "&c(!) Random teleportation failed, try again /rtp!";

    public String getSuccessfulTeleport() {
        return this.successfulTeleport;
    }

    public String getConfigReload() {
        return this.configReload;
    }

    public String getCooldownTeleport() {
        return this.cooldownTeleport;
    }

    public String getNotDimensionFound() {
        return this.notDimensionFound;
    }

    public String getDimensionBlacklist() {
        return this.dimensionBlacklist;
    }

    public String getFailedTeleport() {
        return this.failedTeleport;
    }

    public String getSuccessfulTeleportPlayer() {
        return this.successfulTeleportPlayer;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraRTP/", "locale.json", UtilGson.getGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraRTP/", "locale.json", el -> {
            LocaleConfig config = UtilGson.getGson().fromJson(el, LocaleConfig.class);

            this.configReload = config.getConfigReload();
            this.successfulTeleport = config.getSuccessfulTeleport();
            this.cooldownTeleport = config.getCooldownTeleport();
            this.notDimensionFound = config.getNotDimensionFound();
            this.failedTeleport = config.getFailedTeleport();
            this.dimensionBlacklist = config.getDimensionBlacklist();
            this.successfulTeleportPlayer = config.getSuccessfulTeleportPlayer();
        }).join();

        if (!completed) {
            ExtraRTP.getLogger().error("Error init locale config, generating new locale config.");
            write();
        }
    }
}
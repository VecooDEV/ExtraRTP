package com.vecoo.extrartp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrartp.ExtraRTP;

import java.util.HashMap;
import java.util.Set;

public class ServerConfig {
    private String defaultWorld = "overworld";
    private int countAttemptsTeleport = 5;
    private int cooldownSecondTeleport = 60;
    private boolean firstJoinRTP = false;
    private boolean blacklistWorld = false;
    private Set<String> blacklistWorldList = Sets.newHashSet("the_nether", "the_end");
    private HashMap<String, Integer> heightWorlds;

    public ServerConfig() {
        this.heightWorlds = new HashMap<>();
        this.heightWorlds.put("overworld", 319);
        this.heightWorlds.put("the_nether", 120);
        this.heightWorlds.put("the_end", 319);
    }

    public String getDefaultWorld() {
        return this.defaultWorld;
    }

    public int getCountAttemptsTeleport() {
        return this.countAttemptsTeleport;
    }

    public boolean isFirstJoinRTP() {
        return this.firstJoinRTP;
    }

    public boolean isBlacklistWorld() {
        return this.blacklistWorld;
    }

    public int getCooldownSecondTeleport() {
        return this.cooldownSecondTeleport;
    }

    public HashMap<String, Integer> getHeightWorlds() {
        return this.heightWorlds;
    }

    public Set<String> getBlacklistWorldList() {
        return this.blacklistWorldList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraRTP/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        boolean completed = UtilGson.readFileAsync("/config/ExtraRTP/", "config.json", el -> {
            ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

            this.defaultWorld = config.getDefaultWorld();
            this.countAttemptsTeleport = config.getCountAttemptsTeleport();
            this.blacklistWorld = config.isBlacklistWorld();
            this.blacklistWorldList = config.getBlacklistWorldList();
            this.heightWorlds = config.getHeightWorlds();
            this.cooldownSecondTeleport = config.getCooldownSecondTeleport();
            this.firstJoinRTP = config.isFirstJoinRTP();
        }).join();

        if (!completed) {
            ExtraRTP.getLogger().error("Error init config, generating new config.");
            write();
        }
    }
}
package com.vecoo.extrartp.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrartp.ExtraRTP;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ServerConfig {
    private String defaultWorld = "overworld";
    private int countAttemptsTeleport = 5;
    private int cooldownSecondTeleport = 60;
    private boolean throughLeaves = true;
    private boolean firstJoinRTP = false;
    private boolean blacklistWorld = false;
    private List<String> blacklistWorldList = Arrays.asList("the_nether", "the_end");
    private HashMap<String, Integer> heightWorlds;

    public ServerConfig() {
        this.heightWorlds = new HashMap<>();
        this.heightWorlds.put("overworld", 256);
        this.heightWorlds.put("the_nether", 120);
        this.heightWorlds.put("the_end", 256);
    }

    public String getDefaultWorld() {
        return this.defaultWorld;
    }

    public int getCountAttemptsTeleport() {
        return this.countAttemptsTeleport;
    }

    public boolean isThroughLeaves() {
        return this.throughLeaves;
    }

    public boolean isBlacklistWorld() {
        return this.blacklistWorld;
    }

    public boolean isFirstJoinRTP() {
        return this.firstJoinRTP;
    }

    public int getCooldownSecondTeleport() {
        return this.cooldownSecondTeleport;
    }

    public HashMap<String, Integer> getHeightWorlds() {
        return this.heightWorlds;
    }

    public List<String> getBlacklistWorldList() {
        return this.blacklistWorldList;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraRTP/", "config.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ExtraRTP/", "config.json", el -> {
                ServerConfig config = UtilGson.newGson().fromJson(el, ServerConfig.class);

                this.defaultWorld = config.getDefaultWorld();
                this.countAttemptsTeleport = config.getCountAttemptsTeleport();
                this.throughLeaves = config.isThroughLeaves();
                this.blacklistWorld = config.isBlacklistWorld();
                this.blacklistWorldList = config.getBlacklistWorldList();
                this.heightWorlds = config.getHeightWorlds();
                this.cooldownSecondTeleport = config.getCooldownSecondTeleport();
                this.firstJoinRTP = config.isFirstJoinRTP();
            });
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ExtraRTP.getLogger().error("[ExtraRTP] Error in config.", e);
            write();
        }
    }
}
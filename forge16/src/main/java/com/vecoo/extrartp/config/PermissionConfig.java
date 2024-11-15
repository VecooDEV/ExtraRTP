package com.vecoo.extrartp.config;

import com.vecoo.extralib.gson.UtilGson;
import com.vecoo.extrartp.ExtraRTP;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class PermissionConfig {
    private HashMap<String, Integer> permissionCommands;

    public PermissionConfig() {
        this.permissionCommands = new HashMap<>();
        this.permissionCommands.put("minecraft.command.randomteleport", 0);

        this.permissionCommands.put("minecraft.command.randomteleport.dimension", 2);
        this.permissionCommands.put("minecraft.command.randomteleport.dimension.player", 2);
        this.permissionCommands.put("minecraft.command.randomteleport.reload", 2);
        this.permissionCommands.put("minecraft.command.randomteleport.cooldown", 2);
    }

    public HashMap<String, Integer> getPermissionCommand() {
        return this.permissionCommands;
    }

    private void write() {
        UtilGson.writeFileAsync("/config/ExtraRTP/", "permission.json", UtilGson.newGson().toJson(this)).join();
    }

    public void init() {
        try {
            CompletableFuture<Boolean> future = UtilGson.readFileAsync("/config/ExtraRTP/", "permission.json", el -> this.permissionCommands = UtilGson.newGson().fromJson(el, PermissionConfig.class).getPermissionCommand());
            if (!future.join()) {
                write();
            }
        } catch (Exception e) {
            ExtraRTP.getLogger().error("[ExtraRTP] Error in permissions config.");
            write();
        }
    }
}
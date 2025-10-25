package com.vecoo.extrartp.api.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class RandomTeleportEvent extends Event {
    private final ServerPlayer player;
    private ServerLevel level;
    private double x, y, z;
    private float yRot, xRot;

    public RandomTeleportEvent(@NotNull ServerPlayer player,@NotNull ServerLevel level, double x, double y, double z, float yRot, float xRot) {
        this.player = player;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    @NotNull
    public ServerPlayer getPlayer() {
        return this.player;
    }

    @NotNull
    public ServerLevel getLevel() {
        return this.level;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYRot() {
        return this.yRot;
    }

    public float getXRot() {
        return this.xRot;
    }

    public void setLevel(@NotNull ServerLevel level) {
        this.level = level;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYRot(float yRot) {
        this.yRot = yRot;
    }

    public void setXRot(float xRot) {
        this.xRot = xRot;
    }

    public static class Successful extends RandomTeleportEvent {
        public Successful(@NotNull ServerPlayer player, @NotNull ServerLevel level, double x, double y, double z, float yRot, float xRot) {
            super(player, level, x, y, z, yRot, xRot);
        }
    }
}

package com.vecoo.extrartp.api.events;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class RandomTeleportEvent extends Event {
    private final ServerPlayerEntity player;
    private ServerWorld world;
    private double x;
    private double y;
    private double z;
    private float yRot;
    private float xRot;

    public RandomTeleportEvent(ServerPlayerEntity player, ServerWorld world, double x, double y, double z, float yRot, float xRot) {
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public ServerWorld getWorld() {
        return this.world;
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

    public void setWorld(ServerWorld world) {
        this.world = world;
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
}

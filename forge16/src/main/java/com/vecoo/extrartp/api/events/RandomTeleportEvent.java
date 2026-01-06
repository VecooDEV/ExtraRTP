package com.vecoo.extrartp.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

@Getter
@Setter
@AllArgsConstructor
public class RandomTeleportEvent extends Event {
    @Nonnull
    private final ServerPlayerEntity player;
    @Nonnull
    private ServerWorld world;
    private double x, y, z;
    private float yRot, xRot;

    public static class Successful extends RandomTeleportEvent {
        public Successful(@Nonnull ServerPlayerEntity player, @Nonnull ServerWorld world, double x, double y, double z, float yRot, float xRot) {
            super(player, world, x, y, z, yRot, xRot);
        }
    }
}

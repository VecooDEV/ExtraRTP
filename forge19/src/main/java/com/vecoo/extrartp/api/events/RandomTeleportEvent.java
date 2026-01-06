package com.vecoo.extrartp.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class RandomTeleportEvent extends Event {
    @NotNull
    private final ServerPlayer player;
    @NotNull
    private ServerLevel level;
    private double x, y, z;
    private float yRot, xRot;

    public static class Successful extends RandomTeleportEvent {
        public Successful(@NotNull ServerPlayer player, @NotNull ServerLevel level, double x, double y, double z, float yRot, float xRot) {
            super(player, level, x, y, z, yRot, xRot);
        }
    }
}

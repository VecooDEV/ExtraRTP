package com.vecoo.extrartp.api.factory;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.events.RandomTeleportEvent;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtraRTPFactory {
    public static boolean randomTeleport(@Nonnull ServerWorld world, @Nonnull ServerPlayerEntity player) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        Random random = world.random;
        WorldBorder worldBorder = world.getWorldBorder();

        double minX = worldBorder.getMinX();
        double maxX = worldBorder.getMaxX();
        double minZ = worldBorder.getMinZ();
        double maxZ = worldBorder.getMaxZ();
        int y = Utils.heightStart(world.dimension().location().getPath());

        double deltaX = maxX - minX;
        double deltaZ = maxZ - minZ;

        AtomicBoolean teleportSuccess = new AtomicBoolean(false);

        for (int attempt = 0; attempt < config.getCountAttemptsTeleport() && !teleportSuccess.get(); attempt++) {
            BlockPos.Mutable blockPos = new BlockPos.Mutable(random.nextInt((int) deltaX) + (int) minX, y, random.nextInt((int) deltaZ) + (int) minZ);
            IChunk chunk = world.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

            if (chunk != null) {
                findPosition(blockPos, chunk).thenAccept(success -> {
                    if (success && teleportSuccess.compareAndSet(false, true)) {
                        ExtraRTP.getInstance().getServer().execute(() -> {
                            RandomTeleportEvent.Successful event = new RandomTeleportEvent.Successful(player, world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.yRot, player.xRot);

                            player.teleportTo(event.getWorld(), event.getX(), event.getY(), event.getZ(), event.getYRot(), event.getXRot());
                            player.setDeltaMovement(Vector3d.ZERO);
                        });
                    }
                });
            }
        }

        return teleportSuccess.get();
    }

    private static CompletableFuture<Boolean> findPosition(@Nonnull BlockPos.Mutable blockPos, @Nonnull IChunk chunk) {
        return CompletableFuture.supplyAsync(() -> {
            BlockState blockState;

            while (blockPos.getY() > 0) {
                blockState = chunk.getBlockState(blockPos);

                if (blockState.getMaterial() == Material.AIR) {
                    blockPos.move(Direction.DOWN);
                    continue;
                }

                if (!blockState.getFluidState().isEmpty()) {
                    return false;
                }

                if (!blockState.isCollisionShapeFullBlock(chunk, blockPos)) {
                    blockPos.move(Direction.DOWN);
                }

                return true;
            }

            return false;
        });
    }
}
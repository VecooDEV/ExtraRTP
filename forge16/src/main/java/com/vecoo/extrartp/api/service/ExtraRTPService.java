package com.vecoo.extrartp.api.service;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.events.RandomTeleportEvent;
import com.vecoo.extrartp.util.Utils;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtraRTPService {
    public static boolean randomTeleport(@Nonnull ServerPlayerEntity player, @Nonnull ServerWorld world) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val random = world.random;
        val worldBorder = world.getWorldBorder();

        val minX = worldBorder.getMinX();
        val maxX = worldBorder.getMaxX();
        val minZ = worldBorder.getMinZ();
        val maxZ = worldBorder.getMaxZ();
        val y = Utils.heightStart(world.dimension().location().getPath());

        double deltaX = maxX - minX;
        double deltaZ = maxZ - minZ;

        val teleportSuccess = new AtomicBoolean(false);

        for (int attempt = 0; attempt < serverConfig.getCountAttemptsTeleport() && !teleportSuccess.get(); attempt++) {
            val blockPos = new BlockPos.Mutable(random.nextInt((int) deltaX) + (int) minX, y, random.nextInt((int) deltaZ) + (int) minZ);
            val chunk = world.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

            if (chunk != null) {
                findPosition(blockPos, chunk).thenAccept(success -> {
                    if (success && teleportSuccess.compareAndSet(false, true)) {
                        ExtraRTP.getInstance().getServer().execute(() -> {
                            val event = new RandomTeleportEvent.Successful(player, world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.yRot, player.xRot);

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
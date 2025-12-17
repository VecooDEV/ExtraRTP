package com.vecoo.extrartp.api.service;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.Utils;
import lombok.val;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtraRTPService {
    public static boolean randomTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel level) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val random = level.getRandom();
        val worldBorder = level.getWorldBorder();

        val minX = worldBorder.getMinX();
        val maxX = worldBorder.getMaxX();
        val minZ = worldBorder.getMinZ();
        val maxZ = worldBorder.getMaxZ();
        val y = Utils.heightStart(level.dimension().location().getPath());

        double deltaX = maxX - minX;
        double deltaZ = maxZ - minZ;

        val teleportSuccess = new AtomicBoolean(false);

        for (int attempt = 0; attempt < serverConfig.getCountAttemptsTeleport() && !teleportSuccess.get(); attempt++) {
            val blockPos = new BlockPos.MutableBlockPos(random.nextInt((int) deltaX) + (int) minX, y, random.nextInt((int) deltaZ) + (int) minZ);
            val chunk = level.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

            if (chunk != null) {
                findPosition(blockPos, chunk).thenAccept(success -> {
                    if (success && teleportSuccess.compareAndSet(false, true)) {
                        ExtraRTP.getInstance().getServer().execute(() -> {
                            player.teleportTo(level, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.getYRot(), player.getXRot());
                            player.setDeltaMovement(Vec3.ZERO);
                        });
                    }
                });
            }
        }

        return teleportSuccess.get();
    }

    private static CompletableFuture<Boolean> findPosition(@NotNull BlockPos.MutableBlockPos blockPos, @NotNull ChunkAccess chunk) {
        return CompletableFuture.supplyAsync(() -> {
            BlockState blockState;

            while (blockPos.getY() > 0) {
                blockState = chunk.getBlockState(blockPos);

                if (blockState.isAir()) {
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
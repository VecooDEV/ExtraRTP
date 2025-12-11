package com.vecoo.extrartp.api.factory;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExtraRTPFactory {
    public static boolean randomTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel level) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        RandomSource random = level.getRandom();
        WorldBorder worldBorder = level.getWorldBorder();

        double minX = worldBorder.getMinX();
        double maxX = worldBorder.getMaxX();
        double minZ = worldBorder.getMinZ();
        double maxZ = worldBorder.getMaxZ();
        int y = Utils.heightStart(level.dimension().location().getPath());

        double deltaX = maxX - minX;
        double deltaZ = maxZ - minZ;

        AtomicBoolean teleportSuccess = new AtomicBoolean(false);

        for (int attempt = 0; attempt < config.getCountAttemptsTeleport() && !teleportSuccess.get(); attempt++) {
            BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(random.nextInt((int) deltaX) + (int) minX, y, random.nextInt((int) deltaZ) + (int) minZ);
            ChunkAccess chunk = level.getChunkSource().getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FEATURES, true);

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
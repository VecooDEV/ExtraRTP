package com.vecoo.extrartp.api.factory;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class ExtraRTPFactory {
    public static CompletableFuture<Boolean> randomTeleport(ServerLevel level, ServerPlayer player) {
        if (level == null || player == null) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            ServerConfig config = ExtraRTP.getInstance().getConfig();
            Random random = ThreadLocalRandom.current();
            WorldBorder worldBorder = level.getWorldBorder();

            int minX = (int) worldBorder.getMinX();
            int y = Utils.heightStart(level.dimension().location().getPath());
            int minZ = (int) worldBorder.getMinZ();

            for (int attempt = 0; attempt < config.getCountAttemptsTeleport(); attempt++) {
                BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(random.nextInt((int) (worldBorder.getMaxX() - minX)) + minX, y, random.nextInt((int) (worldBorder.getMaxZ() - minZ)) + minZ);

                while (blockPos.getY() > 0) {
                    Block block = level.getBlockState(blockPos).getBlock();

                    if (block.is(Blocks.AIR) || block.is(BlockTags.LEAVES) && config.isThroughLeaves() || !block.defaultBlockState().isCollisionShapeFullBlock(level, blockPos)) {
                        blockPos.move(Direction.DOWN);
                        continue;
                    }

                    Block checkBlock = level.getBlockState(blockPos.above()).getBlock();

                    if (checkBlock.is(Blocks.WATER) || checkBlock.is(Blocks.LAVA)) {
                        break;
                    }

                    if (!checkBlock.is(Blocks.AIR) || !level.getBlockState(blockPos.above(2)).getBlock().is(Blocks.AIR)) {
                        break;
                    }

                    ExtraRTP.getInstance().getServer().execute(() -> {
                        player.teleportTo(level, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.yRot, player.xRot);
                        player.setDeltaMovement(Vec3.ZERO);
                    });

                    return true;
                }
            }
            return false;
        }).exceptionally(e -> {
            ExtraRTP.getLogger().error("[ExtraRTP] Error", e);
            return false;
        });
    }
}
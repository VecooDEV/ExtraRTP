package com.vecoo.extrartp.api.factory;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.events.RandomTeleportEvent;
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
import net.neoforged.neoforge.common.NeoForge;

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

            double minX = worldBorder.getMinX();
            int y = Utils.heightStart(level.dimension().location().getPath());
            double minZ = worldBorder.getMinZ();

            for (int attempt = 0; attempt < config.getCountAttemptsTeleport(); attempt++) {
                BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(random.nextInt((int) (worldBorder.getMaxX() - minX)) + minX, y, random.nextInt((int) (worldBorder.getMaxZ() - minZ)) + minZ);

                while (blockPos.getY() > 0) {
                    Block block = level.getBlockState(blockPos).getBlock();

                    if (block.defaultBlockState().isAir() || block.defaultBlockState().is(BlockTags.LEAVES) && config.isThroughLeaves() || !block.defaultBlockState().isCollisionShapeFullBlock(level, blockPos)) {
                        blockPos.move(Direction.DOWN);
                        continue;
                    }

                    Block checkBlock = level.getBlockState(blockPos.above()).getBlock();

                    if (checkBlock.defaultBlockState().is(Blocks.WATER) || checkBlock.defaultBlockState().is(Blocks.LAVA)) {
                        break;
                    }

                    if (!checkBlock.defaultBlockState().isAir() || !level.getBlockState(blockPos.above(2)).getBlock().defaultBlockState().isAir()) {
                        break;
                    }

                    ExtraRTP.getInstance().getServer().execute(() -> {
                        RandomTeleportEvent.Successful event = new RandomTeleportEvent.Successful(player, level, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.getYRot(), player.getXRot());

                        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                            return;
                        }

                        player.teleportTo(event.getLevel(), event.getX(), event.getY(), event.getZ(), event.getYRot(), event.getXRot());
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
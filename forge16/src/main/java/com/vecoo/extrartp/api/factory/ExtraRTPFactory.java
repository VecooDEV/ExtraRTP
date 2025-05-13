package com.vecoo.extrartp.api.factory;

import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.events.RandomTeleportEvent;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class ExtraRTPFactory {
    public static CompletableFuture<Boolean> randomTeleport(ServerWorld world, ServerPlayerEntity player) {
        if (world == null || player == null) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            ServerConfig config = ExtraRTP.getInstance().getConfig();
            Random random = ThreadLocalRandom.current();
            WorldBorder worldBorder = world.getWorldBorder();

            int minX = (int) worldBorder.getMinX();
            int y = Utils.heightStart(world.dimension().location().getPath());
            int minZ = (int) worldBorder.getMinZ();

            for (int attempt = 0; attempt < config.getCountAttemptsTeleport(); attempt++) {
                BlockPos.Mutable blockPos = new BlockPos.Mutable(random.nextInt((int) (worldBorder.getMaxX() - minX)) + minX, y, random.nextInt((int) (worldBorder.getMaxZ() - minZ)) + minZ);

                while (blockPos.getY() > 0) {
                    Block block = world.getBlockState(blockPos).getBlock();

                    if (block.isAir(world.getBlockState(blockPos), world, blockPos) || block.is(BlockTags.LEAVES) && config.isThroughLeaves() || !block.defaultBlockState().isCollisionShapeFullBlock(world, blockPos)) {
                        blockPos.move(Direction.DOWN);
                        continue;
                    }

                    Block checkBlock = world.getBlockState(blockPos.above()).getBlock();

                    if (checkBlock.is(Blocks.WATER) || checkBlock.is(Blocks.LAVA)) {
                        break;
                    }

                    if (!checkBlock.is(Blocks.AIR) || !world.getBlockState(blockPos.above(2)).getBlock().is(Blocks.AIR)) {
                        break;
                    }

                    ExtraRTP.getInstance().getServer().execute(() -> {
                        RandomTeleportEvent event = new RandomTeleportEvent(player, world, blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5, player.yRot, player.xRot);

                        MinecraftForge.EVENT_BUS.post(event);

                        player.teleportTo(event.getWorld(), event.getX(), event.getY(), event.getZ(), event.getYRot(), event.getXRot());
                        player.setDeltaMovement(Vector3d.ZERO);
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
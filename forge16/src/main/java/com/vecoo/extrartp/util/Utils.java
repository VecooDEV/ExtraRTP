package com.vecoo.extrartp.util;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Utils {
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    public static boolean randomTeleport(ServerWorld world, ServerPlayerEntity player) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        Random random = new Random();
        int height = heightStart(world.dimension().location().getPath());

        for (int attempts = 0; attempts < config.getCountAttemptsTeleport(); attempts++) {
            BlockPos pos = new BlockPos(random.nextInt((int) world.getWorldBorder().getMaxX()), height, random.nextInt((int) world.getWorldBorder().getMaxZ()));

            while (pos.getY() > 0) {
                Block block = world.getBlockState(pos).getBlock();

                if (block.isAir(world.getBlockState(pos), world, pos) || block.is(BlockTags.LEAVES) && config.isThroughLeaves() || !block.defaultBlockState().isCollisionShapeFullBlock(world, pos)) {
                    pos = pos.below();
                    continue;
                }

                Block checkBlock = world.getBlockState(pos.above()).getBlock();

                if (checkBlock.is(Blocks.WATER) || checkBlock.is(Blocks.LAVA)) {
                    break;
                }

                if (!checkBlock.is(Blocks.AIR) || !world.getBlockState(pos.above(2)).getBlock().is(Blocks.AIR)) {
                    break;
                }

                player.teleportTo(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, player.yRot, player.xRot);
                return true;
            }
        }
        return false;
    }

    public static boolean hasRandomTeleportCooldown(ServerPlayerEntity player) {
        if (cooldown.containsKey(player.getUUID()) && !UtilPermission.hasPermission(player, "minecraft.command." + ExtraRTP.getInstance().getConfig().getRtpCommand() + ".cooldown")) {
            long currentTime = System.currentTimeMillis();
            long lastUsed = cooldown.get(player.getUUID());
            int cooldown = ExtraRTP.getInstance().getConfig().getCooldownSecondTeleport() * 1000;

            if (currentTime - lastUsed < cooldown) {
                player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getCooldownTeleport()
                        .replace("%cooldown%", String.valueOf((cooldown - (currentTime - lastUsed)) / 1000))), Util.NIL_UUID);
                return true;
            }
        }

        return false;
    }

    public static int heightStart(String dimension) {
        if (ExtraRTP.getInstance().getConfig().getHeightWorlds().containsKey(dimension)) {
            return ExtraRTP.getInstance().getConfig().getHeightWorlds().get(dimension);
        }

        return 256;
    }
}
package com.vecoo.extrartp.util;

import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.storage.LibFactory;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Random;

public class Utils {
    public static boolean randomTeleport(ServerLevel level, ServerPlayer player) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        Random random = new Random();
        int height = heightStart(level.dimension().location().getPath());

        for (int attempts = 0; attempts < config.getCountAttemptsTeleport(); attempts++) {
            BlockPos pos = new BlockPos(random.nextInt((int) level.getWorldBorder().getMaxX()), height, random.nextInt((int) level.getWorldBorder().getMaxZ()));

            while (pos.getY() > 0) {
                Block block = level.getBlockState(pos).getBlock();

                if (block.defaultBlockState().isAir() || block.defaultBlockState().is(BlockTags.LEAVES) && config.isThroughLeaves() || !block.defaultBlockState().isCollisionShapeFullBlock(level, pos)) {
                    pos = pos.below();
                    continue;
                }

                Block checkBlock = level.getBlockState(pos.above()).getBlock();

                if (checkBlock.defaultBlockState().is(Blocks.WATER) || checkBlock.defaultBlockState().is(Blocks.LAVA)) {
                    break;
                }

                if (!checkBlock.defaultBlockState().isAir() || !level.getBlockState(pos.above(2)).getBlock().defaultBlockState().isAir()) {
                    break;
                }

                player.teleportTo(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                return true;
            }
        }
        return false;
    }

    public static boolean hasRandomTeleportCooldown(ServerPlayer player) {
        String command = ExtraRTP.getInstance().getConfig().getRtpCommand();

        if (LibFactory.hasCommandCooldown(player.getUUID(), command) && !UtilPermission.hasPermission(player, PermissionNodes.RANDOMTELEPORT_COOLDOWN_COMMAND)) {
            long currentTime = System.currentTimeMillis();
            long lastUsed = ExtraLib.getInstance().getPlayerProvider().getPlayerStorage(player.getUUID()).getKeyCooldown().get(command);
            int cooldown = ExtraRTP.getInstance().getConfig().getCooldownSecondTeleport() * 1000;

            if (currentTime - lastUsed < cooldown) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getCooldownTeleport()
                        .replace("%cooldown%", String.valueOf((cooldown - (currentTime - lastUsed)) / 1000))));
                return true;
            }
        }
        return false;
    }

    public static int heightStart(String dimension) {
        if (ExtraRTP.getInstance().getConfig().getHeightWorlds().containsKey(dimension)) {
            return ExtraRTP.getInstance().getConfig().getHeightWorlds().get(dimension);
        }

        return 319;
    }
}
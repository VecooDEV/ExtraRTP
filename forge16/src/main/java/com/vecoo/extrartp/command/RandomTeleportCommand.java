package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.rtp"))
                .executes(e -> executeRTP(e.getSource().getPlayerOrException()))
                .then(Commands.argument("dimension", StringArgumentType.string())
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.rtp.dimension"))
                        .suggests((s, builder) -> {
                            for (ServerWorld dimensions : ExtraRTP.getInstance().getServer().getAllLevels()) {
                                String dimensionName = dimensions.dimension().location().getPath().toLowerCase();
                                if (dimensionName.startsWith(builder.getRemaining().toLowerCase())) {
                                    builder.suggest(dimensionName);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(s -> executeRTPDimension(s.getSource().getPlayerOrException(), StringArgumentType.getString(s, "dimension")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(s -> UtilPermission.hasPermission(s, "minecraft.command.rtp.dimension.player"))
                                .executes(e -> executeRTPDimensionPlayer(e.getSource(), StringArgumentType.getString(e, "dimension"), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("reload")
                        .requires(s -> UtilPermission.hasPermission(s, "minecraft.command.rtp.reload"))
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeRTP(ServerPlayerEntity player) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();
        ServerWorld world = UtilWorld.getWorldByName(config.getDefaultWorld());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", config.getDefaultWorld())), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(world, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeRTPDimension(ServerPlayerEntity player, String dimension) {
        ServerWorld world = UtilWorld.getWorldByName(dimension);
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            return 0;
        }

        ServerConfig config = ExtraRTP.getInstance().getConfig();

        if (config.isBlacklistWorld() && config.getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getDimensionBlacklist()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(world, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeRTPDimensionPlayer(CommandSource source, String dimension, ServerPlayerEntity player) {
        ServerWorld world = UtilWorld.getWorldByName(dimension);
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (world == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)), false);
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(world, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getSuccessfulTeleportPlayer()
                    .replace("%dimension%", dimension)
                    .replace("%player%", player.getName().getString())), false);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getFailedTeleport()), false);
        }

        return 1;
    }

    private static int executeReload(CommandSource source) {
        ExtraRTP.getInstance().loadConfig();

        source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getConfigReload()), false);
        return 1;
    }
}
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
import com.vecoo.extrartp.util.PermissionNodes;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.RANDOMTELEPORT_COMMAND))
                .executes(e -> executeRTP(e.getSource().getPlayerOrException()))
                .then(Commands.argument("dimension", StringArgumentType.string())
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.RANDOMTELEPORT_DIMENSION_COMMAND))
                        .suggests((s, builder) -> {
                            for (Level dimensions : ExtraRTP.getInstance().getServer().getAllLevels()) {
                                String dimensionName = dimensions.dimension().location().getPath().toLowerCase();
                                if (dimensionName.startsWith(builder.getRemaining().toLowerCase())) {
                                    builder.suggest(dimensionName);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(s -> executeRTPDimension(s.getSource().getPlayerOrException(), StringArgumentType.getString(s, "dimension")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND))
                                .executes(e -> executeRTPDimensionPlayer(e.getSource(), StringArgumentType.getString(e, "dimension"), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("reload")
                        .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_RELOAD_COMMAND))
                        .executes(e -> executeReload(e.getSource()))));
    }

    private static int executeRTP(ServerPlayer player) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        ServerLevel level = UtilWorld.getLevelByName(config.getDefaultWorld());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", config.getDefaultWorld())));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(level, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
        }

        return 1;
    }

    private static int executeRTPDimension(ServerPlayer player, String dimension) {
        ServerLevel level = UtilWorld.getLevelByName(dimension);

        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        ServerConfig config = ExtraRTP.getInstance().getConfig();

        if (config.isBlacklistWorld() && config.getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getDimensionBlacklist()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(level, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", dimension)));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
        }

        return 1;
    }

    private static int executeRTPDimensionPlayer(CommandSourceStack source, String dimension, ServerPlayer player) {
        ServerLevel world = UtilWorld.getLevelByName(dimension);

        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (world == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        if (ExtraRTPFactory.randomTeleport(world, player)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", dimension)));
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleportPlayer()
                    .replace("%dimension%", dimension)
                    .replace("%player%", player.getName().getString())));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
        }

        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        ExtraRTP.getInstance().loadConfig();

        source.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getConfigReload()));
        return 1;
    }
}
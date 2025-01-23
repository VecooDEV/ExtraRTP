package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.PermissionNodes;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(ExtraRTP.getInstance().getConfig().getRtpCommand())
                .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_COMMAND))
                .executes(p -> execute(p.getSource().getPlayerOrException()))
                .then(Commands.argument("dimension", StringArgumentType.string())
                        .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_DIMENSION_COMMAND))
                        .suggests((s, builder) -> {
                            for (ServerLevel dimensions : ExtraRTP.getInstance().getServer().getAllLevels()) {
                                String dimensionName = dimensions.dimension().location().getPath().toLowerCase();
                                if (dimensionName.startsWith(builder.getRemaining().toLowerCase())) {
                                    builder.suggest(dimensionName);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(s -> executeDimension(s.getSource().getPlayerOrException(), StringArgumentType.getString(s, "dimension")))
                        .then(Commands.argument("player", EntityArgument.player())
                                .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND))
                                .executes(e -> executeDimensionPlayer(e.getSource(), StringArgumentType.getString(e, "dimension"), EntityArgument.getPlayer(e, "player")))))
                .then(Commands.literal("reload")
                        .requires(s -> UtilPermission.hasPermission(s, PermissionNodes.RANDOMTELEPORT_RELOAD_COMMAND))
                        .executes(p -> executeReload(p.getSource()))));
    }

    private static int execute(ServerPlayer player) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld(), ExtraRTP.getInstance().getServer());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (!Utils.randomTeleport(level, player)) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()));
            return 0;
        }

        Utils.cooldown.put(player.getUUID(), System.currentTimeMillis());

        player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))));
        return 1;
    }

    private static int executeDimension(ServerPlayer player, String dimension) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(dimension, ExtraRTP.getInstance().getServer());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        if (ExtraRTP.getInstance().getConfig().isBlacklistWorld() && ExtraRTP.getInstance().getConfig().getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getDimensionBlacklist()
                    .replace("%dimension%", dimension.toLowerCase())));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (!Utils.randomTeleport(level, player)) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()));
            return 0;
        }

        Utils.cooldown.put(player.getUUID(), System.currentTimeMillis());

        player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", dimension)
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))));
        return 1;
    }

    private static int executeDimensionPlayer(CommandSourceStack source, String dimension, ServerPlayer player) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(dimension, ExtraRTP.getInstance().getServer());

        if (level == null) {
            source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", dimension)), false);
            return 0;
        }

        if (!Utils.randomTeleport(level, player)) {
            source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()), false);
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleportPlayer()
                .replace("%dimension%", dimension.toLowerCase())
                .replace("%player%", player.getGameProfile().getName())
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))), false);
        return 1;
    }

    private static int executeReload(CommandSourceStack source) {
        ExtraRTP.getInstance().loadConfig();

        source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getConfigReload()), false);
        return 1;
    }
}
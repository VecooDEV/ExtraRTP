package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.storage.player.LibPlayerFactory;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String command : ExtraRTP.getInstance().getConfig().getRtpCommands()) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> p.hasPermission(ExtraRTP.getInstance().getPermission().getPermissionCommand().get("minecraft.command.randomteleport")))
                    .executes(p -> execute(p.getSource().getPlayerOrException()))
                    .then(Commands.argument("dimension", StringArgumentType.string())
                            .requires(p -> p.hasPermission(ExtraRTP.getInstance().getPermission().getPermissionCommand().get("minecraft.command.randomteleport.dimension")))
                            .suggests((s, builder) -> {
                                for (ServerLevel dimensions : ExtraLib.getInstance().getServer().getAllLevels()) {
                                    String dimensionName = dimensions.dimension().location().getPath().toLowerCase();
                                    if (dimensionName.startsWith(builder.getRemaining().toLowerCase())) {
                                        builder.suggest(dimensionName);
                                    }
                                }
                                return builder.buildFuture();
                            })
                            .executes(p -> executeDimension(p.getSource().getPlayerOrException(), StringArgumentType.getString(p, "dimension")))
                            .then(Commands.argument("player", EntityArgument.player())
                                    .requires(e -> e.hasPermission(ExtraRTP.getInstance().getPermission().getPermissionCommand().get("minecraft.command.randomteleport.dimension.player")))
                                    .executes(e -> executeDimensionPlayer(e.getSource(), StringArgumentType.getString(e, "dimension"), EntityArgument.getPlayer(e, "player")))))
                    .then(Commands.literal("reload")
                            .requires(e -> e.hasPermission(ExtraRTP.getInstance().getPermission().getPermissionCommand().get("minecraft.command.randomteleport.reload")))
                            .executes(p -> executeReload(p.getSource()))));
        }
    }

    private static int execute(ServerPlayer player) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld());

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

        player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))));
        LibPlayerFactory.addCommandCooldown(player.getUUID(), "randomTeleport", System.currentTimeMillis());
        return 1;
    }

    private static int executeDimension(ServerPlayer player, String dimension) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(dimension);

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

        player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", dimension)
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))));
        LibPlayerFactory.addCommandCooldown(player.getUUID(), "randomTeleport", System.currentTimeMillis());
        return 1;
    }

    private static int executeDimensionPlayer(CommandSourceStack source, String dimension, ServerPlayer player) {
        ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(dimension);

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
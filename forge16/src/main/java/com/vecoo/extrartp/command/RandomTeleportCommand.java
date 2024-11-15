package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.ExtraLib;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermissions;
import com.vecoo.extralib.storage.player.LibPlayerFactory;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        for (String command : ExtraRTP.getInstance().getConfig().getRtpCommands()) {
            dispatcher.register(Commands.literal(command)
                    .requires(p -> UtilPermissions.hasPermission(p, "minecraft.command.randomteleport", ExtraRTP.getInstance().getPermission().getPermissionCommand()))
                    .executes(p -> execute(p.getSource().getPlayerOrException()))
                    .then(Commands.argument("dimension", StringArgumentType.string())
                            .requires(p -> UtilPermissions.hasPermission(p, "minecraft.command.randomteleport.dimension", ExtraRTP.getInstance().getPermission().getPermissionCommand()))
                            .suggests((s, builder) -> {
                                for (ServerWorld dimensions : ExtraLib.getInstance().getServer().getAllLevels()) {
                                    String dimensionName = dimensions.dimension().location().getPath().toLowerCase();
                                    if (dimensionName.startsWith(builder.getRemaining().toLowerCase())) {
                                        builder.suggest(dimensionName);
                                    }
                                }
                                return builder.buildFuture();
                            })
                            .executes(p -> executeDimension(p.getSource().getPlayerOrException(), StringArgumentType.getString(p, "dimension")))
                            .then(Commands.argument("player", EntityArgument.player())
                                    .requires(e -> UtilPermissions.hasPermission(e, "minecraft.command.randomteleport.dimension.player", ExtraRTP.getInstance().getPermission().getPermissionCommand()))
                                    .executes(e -> executeDimensionPlayer(e.getSource(), StringArgumentType.getString(e, "dimension"), EntityArgument.getPlayer(e, "player")))))
                    .then(Commands.literal("reload")
                            .requires(e -> UtilPermissions.hasPermission(e, "minecraft.command.randomteleport.reload", ExtraRTP.getInstance().getPermission().getPermissionCommand()))
                            .executes(p -> executeReload(p.getSource()))));
        }
    }

    private static int execute(ServerPlayerEntity player) {
        if (!UtilPermissions.hasPermission(player, "minecraft.command.randomteleport", ExtraRTP.getInstance().getPermission().getPermissionCommand())) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotPermission()), Util.NIL_UUID);
            return 0;
        }

        ServerWorld world = UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (!Utils.randomTeleport(world, player)) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))), Util.NIL_UUID);
        LibPlayerFactory.addCommandCooldown(player.getUUID(), "randomTeleport", System.currentTimeMillis());
        return 1;
    }

    private static int executeDimension(ServerPlayerEntity player, String dimension) {
        if (!UtilPermissions.hasPermission(player, "minecraft.command.randomteleport.dimension", ExtraRTP.getInstance().getPermission().getPermissionCommand())) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotPermission()), Util.NIL_UUID);
            return 0;
        }

        ServerWorld world = UtilWorld.getWorldByName(dimension);

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            return 0;
        }

        if (ExtraRTP.getInstance().getConfig().isBlacklistWorld() && ExtraRTP.getInstance().getConfig().getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getDimensionBlacklist()
                    .replace("%dimension%", dimension.toLowerCase())), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (!Utils.randomTeleport(world, player)) {
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()), Util.NIL_UUID);
            return 0;
        }

        player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                .replace("%dimension%", dimension)
                .replace("%x%", String.valueOf((int) player.getX()))
                .replace("%y%", String.valueOf((int) player.getY()))
                .replace("%z%", String.valueOf((int) player.getZ()))), Util.NIL_UUID);
        LibPlayerFactory.addCommandCooldown(player.getUUID(), "randomTeleport", System.currentTimeMillis());
        return 1;
    }

    private static int executeDimensionPlayer(CommandSource source, String dimension, ServerPlayerEntity player) {
        if (!UtilPermissions.hasPermission(player, "minecraft.command.randomteleport.dimension.player", ExtraRTP.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotPermission()), false);
            return 0;
        }

        ServerWorld world = UtilWorld.getWorldByName(dimension);

        if (world == null) {
            source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                    .replace("%dimension%", dimension)), false);
            return 0;
        }

        if (!Utils.randomTeleport(world, player)) {
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

    private static int executeReload(CommandSource source) {
        if (!UtilPermissions.hasPermission(source, "minecraft.command.randomteleport.reload", ExtraRTP.getInstance().getPermission().getPermissionCommand())) {
            source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotPermission()), false);
            return 0;
        }

        ExtraRTP.getInstance().loadConfig();

        source.sendSuccess(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getConfigReload()), false);
        return 1;
    }
}
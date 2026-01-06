package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.service.ExtraRTPService;
import com.vecoo.extrartp.util.Utils;
import lombok.val;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.rtp"))

                .executes(e -> executeRTP(e.getSource().getPlayerOrException()))
                .then(Commands.argument("dimension", StringArgumentType.string())
                        .requires(p -> UtilPermission.hasPermission(p, "minecraft.command.rtp.dimension"))
                        .suggests((s, builder) -> {
                            for (ServerWorld dimensions : ExtraRTP.getInstance().getServer().getAllLevels()) {
                                val dimensionName = dimensions.dimension().location().getPath().toLowerCase();

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

    private static int executeRTP(@Nonnull ServerPlayerEntity player) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();

        val world = UtilWorld.findWorldByName(serverConfig.getDefaultWorld());

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", serverConfig.getDefaultWorld())), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, world)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                    .replace("%dimension%", serverConfig.getDefaultWorld())), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeRTPDimension(@Nonnull ServerPlayerEntity player, @Nonnull String dimension) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val world = UtilWorld.findWorldByName(dimension);

        if (world == null) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            return 0;
        }

        val serverConfig = ExtraRTP.getInstance().getServerConfig();

        if (serverConfig.isBlacklistWorld() && serverConfig.getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getDimensionBlacklist()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, world)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                    .replace("%dimension%", dimension)), Util.NIL_UUID);
        } else {
            player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
        }

        return 1;
    }

    private static int executeRTPDimensionPlayer(@Nonnull CommandSource source, @Nonnull String dimension, @Nonnull ServerPlayerEntity player) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val world = UtilWorld.findWorldByName(dimension);

        if (world == null) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)), false);
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, world)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
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

    private static int executeReload(@Nonnull CommandSource source) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();

        try {
            ExtraRTP.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSuccess(UtilChat.formatMessage(localeConfig.getErrorReload()), false);
            ExtraRTP.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSuccess(UtilChat.formatMessage(localeConfig.getReload()), false);
        return 1;
    }
}
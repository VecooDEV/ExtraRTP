package com.vecoo.extrartp.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.service.ExtraRTPService;
import com.vecoo.extrartp.util.PermissionNodes;
import com.vecoo.extrartp.util.Utils;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RandomTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("rtp")
                .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.RANDOMTELEPORT_COMMAND))
                .executes(e -> executeRTP(e.getSource().getPlayerOrException()))

                .then(Commands.argument("dimension", StringArgumentType.string())
                        .requires(p -> UtilPermission.hasPermission(p, PermissionNodes.RANDOMTELEPORT_DIMENSION_COMMAND))
                        .suggests((s, builder) -> {
                            for (Level dimensions : ExtraRTP.getInstance().getServer().getAllLevels()) {
                                val dimensionName = dimensions.dimension().location().getPath().toLowerCase();

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

    private static int executeRTP(@NotNull ServerPlayer player) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();

        val level = UtilWorld.findLevelByName(serverConfig.getDefaultWorld());

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", serverConfig.getDefaultWorld())));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, level)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                    .replace("%dimension%", ExtraRTP.getInstance().getServerConfig().getDefaultWorld())));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
        }

        return 1;
    }

    private static int executeRTPDimension(@NotNull ServerPlayer player, @NotNull String dimension) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val level = UtilWorld.findLevelByName(dimension);

        if (level == null) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        val serverConfig = ExtraRTP.getInstance().getServerConfig();

        if (serverConfig.isBlacklistWorld() && serverConfig.getBlacklistWorldList().contains(dimension.toLowerCase())) {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getDimensionBlacklist()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        if (Utils.hasRandomTeleportCooldown(player)) {
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, level)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                    .replace("%dimension%", dimension)));
        } else {
            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
        }

        return 1;
    }

    private static int executeRTPDimensionPlayer(@NotNull CommandSourceStack source, @NotNull String dimension, @NotNull ServerPlayer player) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val level = UtilWorld.findLevelByName(dimension);

        if (level == null) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                    .replace("%dimension%", dimension)));
            return 0;
        }

        if (ExtraRTPService.randomTeleport(player, level)) {
            Utils.COOLDOWN.put(player.getUUID(), System.currentTimeMillis());

            player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
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

    private static int executeReload(@NotNull CommandSourceStack source) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();

        try {
            ExtraRTP.getInstance().loadConfig();
        } catch (Exception e) {
            source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getErrorReload()));
            ExtraRTP.getLogger().error(e.getMessage());
            return 0;
        }

        source.sendSystemMessage(UtilChat.formatMessage(localeConfig.getReload()));
        return 1;
    }
}
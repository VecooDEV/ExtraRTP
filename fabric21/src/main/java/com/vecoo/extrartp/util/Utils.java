package com.vecoo.extrartp.util;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrartp.ExtraRTP;
import lombok.val;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static HashMap<UUID, Long> COOLDOWN = new HashMap<>();

    public static boolean hasRandomTeleportCooldown(@NotNull ServerPlayer player) {
        if (UtilPermission.hasPermission(player, "minecraft.command.rtp.cooldown")) {
            return false;
        }

        val playerUUID = player.getUUID();

        if (!COOLDOWN.containsKey(playerUUID)) {
            return false;
        }

        val cooldownMillis = TimeUnit.SECONDS.toMillis(ExtraRTP.getInstance().getServerConfig().getCooldownSecondTeleport());
        long timePassed = System.currentTimeMillis() - COOLDOWN.get(playerUUID);

        if (timePassed < cooldownMillis) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getCooldownTeleport()
                    .replace("%cooldown%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(cooldownMillis - timePassed)))));
            return true;
        }

        COOLDOWN.remove(playerUUID);
        return false;
    }

    public static int heightStart(@NotNull String dimension) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();

        if (serverConfig.getHeightWorlds().containsKey(dimension)) {
            return serverConfig.getHeightWorlds().get(dimension);
        }

        return 319;
    }
}
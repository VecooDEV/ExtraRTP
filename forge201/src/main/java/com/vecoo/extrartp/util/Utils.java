package com.vecoo.extrartp.util;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static HashMap<UUID, Long> cooldown = new HashMap<>();

    public static boolean hasRandomTeleportCooldown(ServerPlayer player) {
        if (UtilPermission.hasPermission(player, PermissionNodes.RANDOMTELEPORT_COOLDOWN)) {
            return false;
        }

        UUID playerUUID = player.getUUID();

        if (!cooldown.containsKey(playerUUID)) {
            return false;
        }

        long cooldownMillis = TimeUnit.SECONDS.toMillis(ExtraRTP.getInstance().getConfig().getCooldownSecondTeleport());
        long timePassed = System.currentTimeMillis() - cooldown.get(playerUUID);

        if (timePassed < cooldownMillis) {
            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getCooldownTeleport()
                    .replace("%cooldown%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(cooldownMillis - timePassed)))));
            return true;
        }

        cooldown.remove(playerUUID);
        return false;
    }

    public static int heightStart(String dimension) {
        ServerConfig config = ExtraRTP.getInstance().getConfig();

        if (config.getHeightWorlds().containsKey(dimension)) {
            return config.getHeightWorlds().get(dimension);
        }

        return 319;
    }
}
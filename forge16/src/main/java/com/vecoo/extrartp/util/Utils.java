package com.vecoo.extrartp.util;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.permission.UtilPermission;
import com.vecoo.extrartp.ExtraRTP;
import lombok.val;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static final Map<UUID, Long> COOLDOWN = new HashMap<>();

    public static boolean hasRandomTeleportCooldown(@Nonnull ServerPlayerEntity player) {
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
            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getCooldownTeleport()
                    .replace("%cooldown%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(cooldownMillis - timePassed)))), Util.NIL_UUID);
            return true;
        }

        COOLDOWN.remove(playerUUID);
        return false;
    }

    public static int heightStart(@Nonnull String dimension) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();

        if (serverConfig.getHeightWorlds().containsKey(dimension)) {
            return serverConfig.getHeightWorlds().get(dimension);
        }

        return 256;
    }
}
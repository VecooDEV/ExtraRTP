package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.storage.LibFactory;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (!UsernameCache.containsUUID(player.getUUID()) && ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            ServerLevel level = (ServerLevel) UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld(), ExtraRTP.getInstance().getServer());

            if (level == null) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
                return;
            }

            if (!Utils.randomTeleport(level, player)) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()));
                return;
            }

            player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())
                    .replace("%x%", String.valueOf((int) player.getX()))
                    .replace("%y%", String.valueOf((int) player.getY()))
                    .replace("%z%", String.valueOf((int) player.getZ()))));
            LibFactory.addCommandCooldown(player.getUUID(), ExtraRTP.getInstance().getConfig().getRtpCommand(), System.currentTimeMillis());
        }
    }
}
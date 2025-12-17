package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.service.ExtraRTPService;
import lombok.val;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val player = (ServerPlayer) event.getEntity();
        val config = ExtraRTP.getInstance().getServerConfig();

        if (!UsernameCache.containsUUID(player.getUUID()) && config.isFirstJoinRTP()) {
            val level = UtilWorld.findLevelByName(config.getDefaultWorld());

            if (level == null) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                        .replace("%dimension%", config.getDefaultWorld())));
                return;
            }

            if (ExtraRTPService.randomTeleport(player, level)) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                        .replace("%dimension%", config.getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
            }
        }
    }
}
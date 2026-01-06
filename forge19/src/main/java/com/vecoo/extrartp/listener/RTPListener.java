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
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val player = (ServerPlayer) event.getEntity();

        if (!UsernameCache.containsUUID(player.getUUID()) && serverConfig.isFirstJoinRTP()) {
            val level = UtilWorld.findLevelByName(serverConfig.getDefaultWorld());

            if (level == null) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                        .replace("%dimension%", serverConfig.getDefaultWorld())));
                return;
            }

            if (ExtraRTPService.randomTeleport(player, level)) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                        .replace("%dimension%", serverConfig.getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
            }
        }
    }
}
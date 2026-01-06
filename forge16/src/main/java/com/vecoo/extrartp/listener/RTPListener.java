package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.service.ExtraRTPService;
import lombok.val;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        val serverConfig = ExtraRTP.getInstance().getServerConfig();
        val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
        val player = (ServerPlayerEntity) event.getPlayer();

        if (!UsernameCache.containsUUID(player.getUUID()) && serverConfig.isFirstJoinRTP()) {
            val world = UtilWorld.findWorldByName(serverConfig.getDefaultWorld());

            if (world == null) {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                        .replace("%dimension%", serverConfig.getDefaultWorld())), Util.NIL_UUID);
                return;
            }

            if (ExtraRTPService.randomTeleport(player, world)) {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                        .replace("%dimension%", serverConfig.getDefaultWorld())), Util.NIL_UUID);
            } else {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
            }
        }
    }
}
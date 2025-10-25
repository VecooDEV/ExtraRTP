package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (!UsernameCache.containsUUID(player.getUUID()) && config.isFirstJoinRTP()) {
            ServerLevel world = UtilWorld.getLevelByName(config.getDefaultWorld());

            if (world == null) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                        .replace("%dimension%", config.getDefaultWorld())));
                return;
            }

            if (ExtraRTPFactory.randomTeleport(world, player)) {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                        .replace("%dimension%", config.getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()));
            }
        }
    }
}
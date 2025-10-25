package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (!UsernameCache.containsUUID(player.getUUID()) && config.isFirstJoinRTP()) {
            ServerWorld world = UtilWorld.getWorldByName(config.getDefaultWorld());

            if (world == null) {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getNotDimensionFound()
                        .replace("%dimension%", config.getDefaultWorld())), Util.NIL_UUID);
                return;
            }

            if (ExtraRTPFactory.randomTeleport(world, player)) {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getSuccessfulTeleport()
                        .replace("%dimension%", config.getDefaultWorld())), Util.NIL_UUID);
            } else {
                player.sendMessage(UtilChat.formatMessage(localeConfig.getFailedTeleport()), Util.NIL_UUID);
            }
        }
    }
}
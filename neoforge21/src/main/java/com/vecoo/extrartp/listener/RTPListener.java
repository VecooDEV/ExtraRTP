package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.UsernameCache;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class RTPListener {
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer) event.getEntity();

        if (!UsernameCache.containsUUID(player.getUUID()) && ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            ServerLevel world = UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld());

            if (world == null) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
                return;
            }

            ExtraRTPFactory.randomTeleport(world, player).thenAccept(success -> {
                if (!success) {
                    player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()));
                } else {
                    player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                            .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
                }
            });
        }
    }
}
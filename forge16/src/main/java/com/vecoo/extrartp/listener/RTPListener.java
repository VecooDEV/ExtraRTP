package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.storage.LibFactory;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.util.Utils;
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

        if (!UsernameCache.containsUUID(player.getUUID()) && ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            ServerWorld world = UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld(), ExtraRTP.getInstance().getServer());

            if (world == null) {
                player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())), Util.NIL_UUID);
                return;
            }

            if (!Utils.randomTeleport(world, player)) {
                player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()), Util.NIL_UUID);
                return;
            }

            player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                    .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())
                    .replace("%x%", String.valueOf((int) player.getX()))
                    .replace("%y%", String.valueOf((int) player.getY()))
                    .replace("%z%", String.valueOf((int) player.getZ()))), Util.NIL_UUID);
            LibFactory.addCommandCooldown(player.getUUID(), "randomTeleport", System.currentTimeMillis());
        }
    }
}
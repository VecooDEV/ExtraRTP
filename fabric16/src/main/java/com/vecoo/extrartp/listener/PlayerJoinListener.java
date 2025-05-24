package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;

public class PlayerJoinListener {
    public static void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        ServerPlayer player = serverGamePacketListener.player;

        if (!ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            return;
        }

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            ServerLevel world = UtilWorld.getWorldByName(ExtraRTP.getInstance().getConfig().getDefaultWorld(), ExtraRTP.getInstance().getServer());

            if (world == null) {
                player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())), Util.NIL_UUID);
                return;
            }

            ExtraRTPFactory.randomTeleport(world, player).thenAccept(success -> {
                if (!success) {
                    player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()), Util.NIL_UUID);
                } else {
                    player.sendMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                            .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())), Util.NIL_UUID);
                }
            });
        }
    }
}

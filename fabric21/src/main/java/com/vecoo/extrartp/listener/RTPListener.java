package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;

public class RTPListener {
    public static void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        ServerPlayer player = serverGamePacketListener.player;

        if (!ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            return;
        }

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            ServerLevel world = UtilWorld.getLevelByName(ExtraRTP.getInstance().getConfig().getDefaultWorld());

            if (world == null) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
                return;
            }

            if (ExtraRTPFactory.randomTeleport(world, player)) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getSuccessfulTeleport()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocale().getFailedTeleport()));
            }
        }
    }
}

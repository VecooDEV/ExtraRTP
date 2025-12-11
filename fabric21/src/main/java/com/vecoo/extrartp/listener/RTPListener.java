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
        if (!ExtraRTP.getInstance().getConfig().isFirstJoinRTP()) {
            return;
        }

        ServerPlayer player = serverGamePacketListener.player;

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            ServerLevel level = UtilWorld.findLevelByName(ExtraRTP.getInstance().getConfig().getDefaultWorld());

            if (level == null) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
                return;
            }

            if (ExtraRTPFactory.randomTeleport(player, level)) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getSuccessfulTeleport()
                        .replace("%dimension%", ExtraRTP.getInstance().getConfig().getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getFailedTeleport()));
            }
        }
    }
}

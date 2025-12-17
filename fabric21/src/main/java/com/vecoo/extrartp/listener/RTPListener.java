package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.service.ExtraRTPService;
import lombok.val;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;

public class RTPListener {
    public static void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        if (!ExtraRTP.getInstance().getServerConfig().isFirstJoinRTP()) {
            return;
        }

        val player = serverGamePacketListener.player;

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            val level = UtilWorld.findLevelByName(ExtraRTP.getInstance().getServerConfig().getDefaultWorld());

            if (level == null) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getNotDimensionFound()
                        .replace("%dimension%", ExtraRTP.getInstance().getServerConfig().getDefaultWorld())));
                return;
            }

            if (ExtraRTPService.randomTeleport(player, level)) {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getSuccessfulTeleport()
                        .replace("%dimension%", ExtraRTP.getInstance().getServerConfig().getDefaultWorld())));
            } else {
                player.sendSystemMessage(UtilChat.formatMessage(ExtraRTP.getInstance().getLocaleConfig().getFailedTeleport()));
            }
        }
    }
}

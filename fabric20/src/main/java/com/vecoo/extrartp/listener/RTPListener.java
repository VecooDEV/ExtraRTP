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
        val serverConfig = ExtraRTP.getInstance().getServerConfig();

        if (!serverConfig.isFirstJoinRTP()) {
            return;
        }

        val player = serverGamePacketListener.player;

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
            val localeConfig = ExtraRTP.getInstance().getLocaleConfig();
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
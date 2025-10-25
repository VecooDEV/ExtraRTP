package com.vecoo.extrartp.listener;

import com.vecoo.extralib.chat.UtilChat;
import com.vecoo.extralib.world.UtilWorld;
import com.vecoo.extrartp.ExtraRTP;
import com.vecoo.extrartp.api.factory.ExtraRTPFactory;
import com.vecoo.extrartp.config.LocaleConfig;
import com.vecoo.extrartp.config.ServerConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;

public class RTPListener {
    public static void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer server) {
        ServerPlayer player = serverGamePacketListener.player;
        ServerConfig config = ExtraRTP.getInstance().getConfig();
        LocaleConfig localeConfig = ExtraRTP.getInstance().getLocale();

        if (!config.isFirstJoinRTP()) {
            return;
        }

        if (player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0) {
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

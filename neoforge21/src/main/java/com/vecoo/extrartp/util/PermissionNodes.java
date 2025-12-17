package com.vecoo.extrartp.util;

import com.vecoo.extralib.permission.UtilPermission;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class PermissionNodes {
    private static final Set<PermissionNode<?>> PERMISSION_LIST = new HashSet<>();

    public static PermissionNode<Boolean> RANDOMTELEPORT_COMMAND = UtilPermission.getPermissionNode("minecraft.command.rtp");
    public static PermissionNode<Boolean> RANDOMTELEPORT_COOLDOWN = UtilPermission.getPermissionNode("minecraft.command.rtp.cooldown");
    public static PermissionNode<Boolean> RANDOMTELEPORT_RELOAD_COMMAND = UtilPermission.getPermissionNode("minecraft.command.rtp.reload");
    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_COMMAND = UtilPermission.getPermissionNode("minecraft.command.rtp.dimension");
    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND = UtilPermission.getPermissionNode("minecraft.command.rtp.dimension.player");

    public static void registerPermission(@NotNull PermissionGatherEvent.Nodes event) {
        PERMISSION_LIST.add(RANDOMTELEPORT_COMMAND);
        PERMISSION_LIST.add(RANDOMTELEPORT_COOLDOWN);
        PERMISSION_LIST.add(RANDOMTELEPORT_RELOAD_COMMAND);
        PERMISSION_LIST.add(RANDOMTELEPORT_DIMENSION_COMMAND);
        PERMISSION_LIST.add(RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND);

        for (PermissionNode<?> node : PERMISSION_LIST) {
            if (!event.getNodes().contains(node)) {
                event.addNodes(node);
            }
        }
    }
}

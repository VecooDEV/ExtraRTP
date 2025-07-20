package com.vecoo.extrartp.util;

import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<?>> PERMISSION_LIST = new ArrayList<>();

    public static PermissionNode<Boolean> RANDOMTELEPORT_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.rtp",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_COOLDOWN = new PermissionNode<>(
            "minecraft",
            "command.rtp.cooldown",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_RELOAD_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.rtp.reload",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.rtp.dimension",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND = new PermissionNode<>(
            "minecraft",
            "command.rtp.dimension.player",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}
package com.vecoo.extrartp.util;

import com.vecoo.extrartp.ExtraRTP;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.ArrayList;
import java.util.List;

public class PermissionNodes {
    public static List<PermissionNode<Boolean>> permissionList = new ArrayList<>();

    public static PermissionNode<Boolean> RANDOMTELEPORT_COMMAND = new PermissionNode<>(
            "minecraft",
            "command." + ExtraRTP.getInstance().getConfig().getRtpCommand(),
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_COOLDOWN_COMMAND = new PermissionNode<>(
            "minecraft",
            "command." + ExtraRTP.getInstance().getConfig().getRtpCommand() + ".cooldown",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_RELOAD_COMMAND = new PermissionNode<>(
            "minecraft",
            "command." + ExtraRTP.getInstance().getConfig().getRtpCommand() + ".reload",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_COMMAND = new PermissionNode<>(
            "minecraft",
            "command." + ExtraRTP.getInstance().getConfig().getRtpCommand() + ".dimension",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);

    public static PermissionNode<Boolean> RANDOMTELEPORT_DIMENSION_PLAYER_COMMAND = new PermissionNode<>(
            "minecraft",
            "command." + ExtraRTP.getInstance().getConfig().getRtpCommand() + ".dimension.player",
            PermissionTypes.BOOLEAN,
            (p, uuid, permissionDynamicContexts) -> false);
}

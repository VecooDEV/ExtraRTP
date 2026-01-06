package com.vecoo.extrartp.config;

import com.google.common.collect.Sets;
import com.vecoo.extralib.shade.spongepowered.configurate.objectmapping.ConfigSerializable;
import lombok.Getter;

import java.util.HashMap;
import java.util.Set;

@Getter
@ConfigSerializable
public class ServerConfig {
    private String defaultWorld = "overworld";
    private int countAttemptsTeleport = 5;
    private int cooldownSecondTeleport = 60;
    private boolean firstJoinRTP = false;
    private boolean blacklistWorld = false;
    private Set<String> blacklistWorldList = Sets.newHashSet("the_nether", "the_end");
    private HashMap<String, Integer> heightWorlds = new HashMap<>();

    public ServerConfig() {
        this.heightWorlds.put("overworld", 319);
        this.heightWorlds.put("the_nether", 120);
        this.heightWorlds.put("the_end", 319);
    }
}
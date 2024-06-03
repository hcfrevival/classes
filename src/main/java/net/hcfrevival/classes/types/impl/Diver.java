package net.hcfrevival.classes.types.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Time;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.impl.DiverConfig;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
public class Diver implements IClass {
    public final ClassService service;
    public final String name = "Diver";
    public final String description = "hey";
    public final Material icon = Material.TRIDENT;
    public final Material helmet = Material.TURTLE_HELMET;
    public final Material chestplate = Material.DIAMOND_CHESTPLATE;
    public final Material leggings = Material.DIAMOND_LEGGINGS;
    public final Material boots = Material.DIAMOND_BOOTS;
    public final boolean emptyArmorEnforced = true;
    public final Set<UUID> activePlayers;
    public final DiverConfig config;
    public final Map<UUID, Long> riptideCooldowns;
    public final Map<UUID, Long> callOfTheSeaCooldowns;

    public Diver(ClassService service) {
        this.service = service;
        this.config = new DiverConfig(this);
        this.activePlayers = Sets.newConcurrentHashSet();
        this.riptideCooldowns = Maps.newConcurrentMap();
        this.callOfTheSeaCooldowns = Maps.newConcurrentMap();
    }

    public boolean hasRiptideCooldown(Player player) {
        return riptideCooldowns.containsKey(player.getUniqueId());
    }

    public boolean hasCallOfTheSeaCooldown(Player player) {
        return callOfTheSeaCooldowns.containsKey(player.getUniqueId());
    }

    public long getRiptideCooldown(Player player) {
        return riptideCooldowns.getOrDefault(player.getUniqueId(), -1L);
    }

    public long getCallOfTheSeaCooldown(Player player) {
        return callOfTheSeaCooldowns.getOrDefault(player.getUniqueId(), -1L);
    }

    public void setRiptideCooldown(Player player) {
        long expire = Time.now() + (config.getRiptideCooldown() * 1000L);
        riptideCooldowns.put(player.getUniqueId(), expire);
    }

    public void setCallOfTheSeaCooldown(Player player) {
        long expire = Time.now() + (config.getCallOfTheSeaCooldown() * 1000L);
        callOfTheSeaCooldowns.put(player.getUniqueId(), expire);
    }
}

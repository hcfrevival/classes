package net.hcfrevival.classes.types.impl;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.impl.GenericConfig;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;

import java.util.Set;
import java.util.UUID;

@Getter
public class Miner implements IClass {
    public final ClassService service;
    public final String name = "Miner";
    public final String description = "hey x2";
    public final Material icon = Material.DIAMOND_PICKAXE;
    public final Material helmet = Material.IRON_HELMET;
    public final Material chestplate = Material.IRON_CHESTPLATE;
    public final Material leggings = Material.IRON_LEGGINGS;
    public final Material boots = Material.IRON_BOOTS;
    public final boolean emptyArmorEnforced = true;
    public final Set<UUID> activePlayers;
    public final GenericConfig config;

    public Miner(ClassService service) {
        this.service = service;
        this.activePlayers = Sets.newConcurrentHashSet();
        this.config = new GenericConfig(this);
    }
}

package net.hcfrevival.classes.types.impl;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.impl.BardConfig;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class Bard implements IClass {
    public final ClassService service;
    public final String name = "Bard";
    public final String description = "hey x3";
    public final Material icon = Material.GOLDEN_HELMET;
    public final Material helmet = Material.GOLDEN_HELMET;
    public final Material chestplate = Material.GOLDEN_CHESTPLATE;
    public final Material leggings = Material.GOLDEN_LEGGINGS;
    public final Material boots = Material.GOLDEN_BOOTS;
    public final boolean emptyArmorEnforced = true;
    public final Set<UUID> activePlayers;
    public final BardConfig config;

    public Bard(ClassService service) {
        this.service = service;
        this.activePlayers = Sets.newConcurrentHashSet();
        this.config = new BardConfig(this);
    }
}

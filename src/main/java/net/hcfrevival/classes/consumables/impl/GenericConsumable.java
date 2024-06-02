package net.hcfrevival.classes.consumables.impl;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.hcfrevival.classes.consumables.EConsumableApplicationType;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

@Getter
public class GenericConsumable implements IClassConsumable {
    public final IClass parent;
    public final Material material;
    public final EConsumableApplicationType applicationType;
    public final PotionEffectType effectType;
    public final int duration;
    public final int cooldown;
    public final int amplifier;
    public final Map<UUID, Long> cooldowns;

    public GenericConsumable(
            IClass parent,
            Material material,
            EConsumableApplicationType applicationType,
            PotionEffectType effectType,
            int duration,
            int cooldown,
            int amplifier
    ) {
        this.parent = parent;
        this.material = material;
        this.applicationType = applicationType;
        this.effectType = effectType;
        this.duration = duration;
        this.cooldown = cooldown;
        this.amplifier = amplifier;
        this.cooldowns = Maps.newConcurrentMap();
    }
}

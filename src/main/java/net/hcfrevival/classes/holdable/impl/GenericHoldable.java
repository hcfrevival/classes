package net.hcfrevival.classes.holdable.impl;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.hcfrevival.classes.holdable.IClassHoldable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

@Getter
public class GenericHoldable implements IClassHoldable {
    public final IClass parent;
    public final Material material;
    public final PotionEffectType effectType;
    public final int amplifier;
    public final int duration;
    public final Map<UUID, Long> currentHolders;

    public GenericHoldable(
            IClass parent,
            Material material,
            PotionEffectType effectType,
            int amplifier,
            int duration
    ) {
        this.parent = parent;
        this.material = material;
        this.effectType = effectType;
        this.amplifier = amplifier;
        this.duration = duration;
        this.currentHolders = Maps.newConcurrentMap();
    }
}

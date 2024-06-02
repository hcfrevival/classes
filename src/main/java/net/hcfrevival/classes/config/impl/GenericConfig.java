package net.hcfrevival.classes.config.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.config.IClassConfig;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public class GenericConfig implements IClassConfig {
    @Getter public IClass parent;
    @Getter public final Map<PotionEffectType, Integer> passiveEffects;
    @Getter public final List<IClassConsumable> consumables;
    @Getter @Setter public int warmup;

    public GenericConfig(IClass parent) {
        this.parent = parent;
        this.warmup = 10;
        this.passiveEffects = Maps.newHashMap();
        this.consumables = Lists.newArrayList();
    }

    @Override
    public void load() {
        IClassConfig.super.load();
    }
}
package net.hcfrevival.classes.config.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

@Getter
public class ArcherConfig extends GenericConfig {
    public Map<PotionEffectType, Integer> passiveEffects;
    public List<IClassConsumable> consumables;
    @Setter public int warmup;

    @Getter double maxDamageDealt;
    @Getter double consecutiveBase;
    @Getter double consecutiveMultiplier;
    @Getter double damagePerBlock;
    @Getter double markDamagePercentage;
    @Getter int markDuration;

    public ArcherConfig(IClass parent) {
        super(parent);
        this.passiveEffects = Maps.newHashMap();
        this.consumables = Lists.newArrayList();
        this.warmup = 10;
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes.yml");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.maxDamageDealt = conf.getDouble(key + "max-damage-dealt", 30);
        this.consecutiveBase = conf.getDouble(key + "consecutive-base-damage", 5.0);
        this.consecutiveMultiplier = conf.getDouble(key + "consecutive-multiplier", 1.25);
        this.damagePerBlock = conf.getDouble(key + "damage-per-block", 0.1);
        this.markDuration = conf.getInt(key + "mark-duration", 10);
        this.markDamagePercentage = conf.getDouble(key + "mark-percentage", 0.15);
    }
}

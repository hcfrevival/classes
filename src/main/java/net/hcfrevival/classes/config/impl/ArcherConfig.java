package net.hcfrevival.classes.config.impl;

import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public final class ArcherConfig extends GenericConfig {
    double maxDamageDealt;
    double consecutiveBase;
    double consecutiveMultiplier;
    double damagePerBlock;
    double markDamagePercentage;
    int markDuration;

    public ArcherConfig(IClass parent) {
        super(parent);
        this.warmup = 10;
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.maxDamageDealt = conf.getDouble(key + "max-damage-dealt", 30);
        this.consecutiveBase = conf.getDouble(key + "consecutive-base-damage", 5.0);
        this.consecutiveMultiplier = conf.getDouble(key + "consecutive-multiplier", 1.25);
        this.damagePerBlock = conf.getDouble(key + "damage-per-block", 0.1);
        this.markDuration = conf.getInt(key + "mark-duration", 10);
        this.markDamagePercentage = conf.getDouble(key + "mark-percentage", 0.15);
    }
}

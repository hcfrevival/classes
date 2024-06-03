package net.hcfrevival.classes.config.impl;

import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public final class DiverConfig extends GenericConfig {
    double damagePerBlock;
    double maxDamageDealt;
    int riptideCooldown;
    int callOfTheSeaCooldown;
    int callOfTheSeaDuration;
    boolean riptideResetOnHit;

    public DiverConfig(IClass parent) {
        super(parent);
        this.warmup = 10;
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.damagePerBlock = conf.getDouble(key + "damage-per-block", 0.1);
        this.maxDamageDealt = conf.getDouble(key + "max-damage-dealt", 15.0);
        this.riptideCooldown = conf.getInt(key + "riptide-cooldown", 5);
        this.callOfTheSeaCooldown = conf.getInt(key + "call-of-the-sea-cooldown", 300);
        this.callOfTheSeaDuration = conf.getInt(key + "call-of-the-sea-duration", 300);
        this.riptideResetOnHit = conf.getBoolean(key + "riptide-reset-on-hit", true);
    }
}

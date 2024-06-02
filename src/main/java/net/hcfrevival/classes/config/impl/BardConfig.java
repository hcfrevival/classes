package net.hcfrevival.classes.config.impl;

import lombok.Setter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;

@Setter
public class BardConfig extends GenericConfig {
    public int warmup;
    public int holdableUpdateRate;
    public double effectRange;

    public BardConfig(IClass parent) {
        super(parent);
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.holdableUpdateRate = conf.getInt(key + "holdable-update-rate", 3);
        this.effectRange = conf.getDouble(key + "effect-range", 32.0);
    }
}

package net.hcfrevival.classes.config.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.holdable.IClassHoldable;
import net.hcfrevival.classes.holdable.IHoldableClass;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

@Getter
@Setter
public final class BardConfig extends GenericConfig implements IHoldableClass {
    public int warmup;
    public double effectRange;
    public int holdableUpdateRate;
    public List<IClassHoldable> holdables;

    public BardConfig(IClass parent) {
        super(parent);
        this.holdables = Lists.newArrayList();
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.holdableUpdateRate = conf.getInt(key + "holdable-update-rate", 3);
        this.effectRange = conf.getDouble(key + "effect-range", 32.0);

        loadHoldables(conf, (key + "holdables"));
        parent.getService().getPlugin().getAresLogger().info("Loaded {} Class Holdables", getHoldables().size());
    }
}

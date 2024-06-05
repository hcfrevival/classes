package net.hcfrevival.classes.holdable;

import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.utils.Effects;
import net.hcfrevival.classes.holdable.impl.GenericHoldable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IHoldableClass {
    IClass getParent();
    List<IClassHoldable> getHoldables();
    int getHoldableUpdateRate();

    void setHoldableUpdateRate(int rate);

    default Optional<IClassHoldable> getHoldable(Material mat) {
        return getHoldables().stream().filter(h -> h.getMaterial().equals(mat)).findFirst();
    }

    default void resetHoldables(UUID uniqueId) {
        getHoldables().forEach(h -> h.getCurrentHolders().remove(uniqueId));
    }

    default boolean shouldReapplyHoldable(UUID uniqueId, IClassHoldable holdable) {
        final long prev = holdable.getTimeSinceLastHold(uniqueId);

        if (prev <= -1L) {
            return true;
        }

        final long future = Time.now() + (getHoldableUpdateRate() * 1000L);

        return future <= prev;
    }

    default void loadHoldables(YamlConfiguration conf, String key) {
        ConfigurationSection holdableSection = conf.getConfigurationSection(key);

        if (holdableSection == null) {
            return;
        }

        for (String effectName : holdableSection.getKeys(false)) {
            PotionEffectType effectType = Effects.getEffectByName(effectName);
            String materialName = conf.getString(key + "." + effectName + ".material");
            int amplifier = conf.getInt(key + "." + effectName + ".amplifier");
            int duration = conf.getInt(key + "." + effectName + ".duration");
            Material material;

            if (effectType == null || materialName == null) {
                continue;
            }

            try {
                material = Material.getMaterial(materialName);
            } catch (IllegalArgumentException e) {
                continue;
            }

            getHoldables().add(new GenericHoldable(getParent(), material, effectType, amplifier, duration));
        }
    }
}

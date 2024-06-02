package net.hcfrevival.classes.config;

import gg.hcfactions.libs.bukkit.utils.Effects;
import net.hcfrevival.classes.consumables.EConsumableApplicationType;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.consumables.impl.GenericConsumable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public interface IClassConfig {
    IClass getParent();
    Map<PotionEffectType, Integer> getPassiveEffects();
    List<IClassConsumable> getConsumables();
    int getWarmup();

    void setWarmup(int i);

    default void load() {
        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        if (!getPassiveEffects().isEmpty()) {
            getPassiveEffects().clear();
        }

        if (!getConsumables().isEmpty()) {
            getConsumables().clear();
        }

        int warmup = conf.getInt(key + "warmup", 10);
        setWarmup(warmup);

        // Passives Start
        ConfigurationSection passiveSection = conf.getConfigurationSection(key + "passive");
        if (passiveSection != null) {
            for (String effectName : passiveSection.getKeys(false)) {
                PotionEffectType effectType = Effects.getEffectByName(effectName);
                int amplifier = conf.getInt(key + "passive." + effectName);

                if (effectType == null) {
                    getParent().getService().getPlugin().getAresLogger().error("Invalid passive effect type for {}: {}", getParent().getName(), effectName);
                    continue;
                }

                getPassiveEffects().put(effectType, (amplifier - 1));
            }

            getParent().getService().getPlugin().getAresLogger().info("Loaded {} Passive Effects for {}", getPassiveEffects().size(), getParent().getName());
        } else {
            getParent().getService().getPlugin().getAresLogger().warn("Could not find passive section for {}", getParent().getName());
        }
        // Passives End

        // Consumable Start
        ConfigurationSection consumeSection = conf.getConfigurationSection(key + "consumables");
        if (consumeSection != null) {
            for (String effectName : consumeSection.getKeys(false)) {
                String consumeKey = key + "consumables." + effectName + ".";
                String materialName = conf.getString(consumeKey + "material");
                String applicationMethodName = conf.getString(consumeKey + "application");
                int amplifier = conf.getInt(consumeKey + "amplifier");
                int duration = conf.getInt(consumeKey + "duration");
                int cooldown = conf.getInt(consumeKey + "cooldown");
                PotionEffectType effect = Effects.getEffectByName(effectName);
                Material material;
                EConsumableApplicationType applicationType;

                try {
                    material = Material.valueOf(materialName);
                } catch (IllegalArgumentException e) {
                    getParent().getService().getPlugin().getAresLogger().error("Invalid consumable material for {}: {}", getParent().getName(), materialName);
                    continue;
                }

                try {
                    applicationType = EConsumableApplicationType.valueOf(applicationMethodName);
                } catch (IllegalArgumentException e) {
                    getParent().getService().getPlugin().getAresLogger().error("Invalid consumable application type for {}: {}", getParent().getName(), applicationMethodName);
                    continue;
                }

                GenericConsumable consumable = new GenericConsumable(
                        getParent(),
                        material,
                        applicationType,
                        effect,
                        duration,
                        cooldown,
                        amplifier
                );

                getConsumables().add(consumable);
            }

            getParent().getService().getPlugin().getAresLogger().info("Loaded {} Consumables for {}", getConsumables().size(), getParent().getName());
        } else {
            getParent().getService().getPlugin().getAresLogger().warn("Could not find consumable section for {}", getParent().getName());
        }
        // Consumable End
    }
}

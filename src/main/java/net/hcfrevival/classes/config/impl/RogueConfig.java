package net.hcfrevival.classes.config.impl;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.configuration.file.YamlConfiguration;

@Setter
@Getter
public final class RogueConfig extends GenericConfig {
    public int backstabCooldown;
    public int grappleCooldown;
    public int cloakCooldown;
    public double fullInvisibilityRadius;
    public double partialInvisibilityRadius;
    public double grappleHorizontalSpeed;
    public double grappleVerticalSpeed;
    public double backstabDamage;

    public RogueConfig(IClass parent) {
        super(parent);
    }

    @Override
    public void load() {
        super.load();

        YamlConfiguration conf = getParent().getService().getPlugin().loadConfiguration("classes");
        String key = "data." + getParent().getName().toLowerCase() + ".";

        this.backstabCooldown = conf.getInt(key + "backstab-cooldown", 3);
        this.grappleCooldown = conf.getInt(key + "grapple-cooldown", 5);
        this.cloakCooldown = conf.getInt(key + "cloak-cooldown", 300);
        this.backstabDamage = conf.getDouble(key + "backstab-damage", 6.0);
        this.fullInvisibilityRadius = conf.getDouble(key + "invisibility-radius.full", 16.0);
        this.partialInvisibilityRadius = conf.getDouble(key + "invisibility-radius.partial", 8.0);
        this.grappleHorizontalSpeed = conf.getDouble(key + "grapple.horizontal-speed", 1.25);
        this.grappleVerticalSpeed = conf.getDouble(key + "grapple.vertical-speed", 0.25);
    }
}

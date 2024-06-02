package net.hcfrevival.classes.config;

import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import org.bukkit.configuration.file.YamlConfiguration;

@Getter
public class ClassGlobalConfig {
    public final ClassService service;
    public int consumableRadius = 32;

    public ClassGlobalConfig(ClassService service) {
        this.service = service;
    }

    public void loadConfig() {
        YamlConfiguration conf = service.getPlugin().loadConfiguration("classes");
        String key = "global.";

        consumableRadius = conf.getInt(key + "consumable-radius", 32);
    }
}

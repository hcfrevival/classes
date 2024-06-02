package net.hcfrevival.classes;

import com.google.common.collect.Lists;
import gg.hcfactions.libs.bukkit.AresPlugin;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.services.IAresService;
import lombok.Getter;
import net.hcfrevival.classes.config.ClassGlobalConfig;
import net.hcfrevival.classes.events.ClassDeactivateEvent;
import net.hcfrevival.classes.events.ClassReadyEvent;
import net.hcfrevival.classes.events.ClassUnreadyEvent;
import net.hcfrevival.classes.listener.ArcherListener;
import net.hcfrevival.classes.listener.ClassArmorListener;
import net.hcfrevival.classes.listener.ConsumableListener;
import net.hcfrevival.classes.task.ConsumableCooldownTask;
import net.hcfrevival.classes.types.IClass;
import net.hcfrevival.classes.types.impl.Archer;
import net.hcfrevival.classes.types.impl.Bard;
import net.hcfrevival.classes.types.impl.Miner;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Optional;

@Getter
public class ClassService implements IAresService {
    public final String name = "Classes";
    public final AresPlugin plugin;
    public final NamespacedKey namespacedKey;
    public ClassGlobalConfig globalConfig;
    public List<IClass> classRepository;
    public BukkitTask cooldownCleanupTask;

    public ClassService(AresPlugin plugin) {
        this.plugin = plugin;
        this.namespacedKey = new NamespacedKey(plugin, "classes");
        this.classRepository = Lists.newArrayList();
    }

    @Override
    public void onEnable() {
        this.globalConfig = new ClassGlobalConfig(this);
        this.globalConfig.loadConfig();

        this.cooldownCleanupTask = new Scheduler(plugin).async(new ConsumableCooldownTask(this)).repeat(0L, 1L).run();

        // Class initialization
        classRepository.add(new Archer(this));
        classRepository.add(new Miner(this));
        classRepository.add(new Bard(this));
        classRepository.forEach(toLoad -> toLoad.getConfig().load());

        plugin.registerListener(new ArcherListener(this));
        plugin.registerListener(new ClassArmorListener(this));
        plugin.registerListener(new ConsumableListener(this));
    }

    @Override
    public void onDisable() {
        if (this.cooldownCleanupTask != null) {
            this.cooldownCleanupTask.cancel();
            this.cooldownCleanupTask = null;
        }
    }

    @Override
    public void onReload() {
        this.globalConfig.loadConfig();

        classRepository.forEach(playerClass -> playerClass.getConfig().load());
    }

    public Optional<IClass> getCurrentClass(Player player) {
        return classRepository.stream().filter(c -> c.getActivePlayers().contains(player.getUniqueId())).findFirst();
    }

    public Optional<IClass> getClassByArmor(Player player) {
        return classRepository.stream().filter(c -> c.hasArmorRequirements(player)).findFirst();
    }

    public Optional<IClass> getClassByName(String className) {
        return classRepository.stream().filter(c -> c.getName().equalsIgnoreCase(className)).findFirst();
    }

    public void validateClass(Player player) {
        IClass actualClass = getCurrentClass(player).orElse(null);
        IClass expectedClass = getClassByArmor(player).orElse(null);

        if (expectedClass != null && expectedClass == actualClass) {
            return;
        }

        if (expectedClass != null) {
            if (actualClass != null) {
                final ClassDeactivateEvent deactivateEvent = new ClassDeactivateEvent(player, actualClass);
                Bukkit.getPluginManager().callEvent(deactivateEvent);
                actualClass.deactivate(player);
            }

            ClassReadyEvent readyEvent = new ClassReadyEvent(player, expectedClass);
            readyEvent.setMessagePrinted(true);
            Bukkit.getPluginManager().callEvent(readyEvent);
            return;
        }

        if (actualClass != null) {
            ClassDeactivateEvent deactivateEvent = new ClassDeactivateEvent(player, actualClass);
            Bukkit.getPluginManager().callEvent(deactivateEvent);
            actualClass.deactivate(player);
        } else {
            ClassUnreadyEvent unreadyEvent = new ClassUnreadyEvent(player, null);
            Bukkit.getPluginManager().callEvent(unreadyEvent);
        }
    }
}

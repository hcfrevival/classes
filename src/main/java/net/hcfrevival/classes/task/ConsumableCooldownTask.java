package net.hcfrevival.classes.task;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassMessages;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.events.ClassConsumableUnlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
public final class ConsumableCooldownTask extends BukkitRunnable {
    public ClassService service;

    @Override
    public void run() {
        service.getClassRepository().forEach(playerClass -> playerClass.getConfig().getConsumables().forEach(consumable -> {
            Set<UUID> toRemove = Sets.newHashSet();
            consumable.getCooldowns().forEach((uuid, expire) -> {
                if (expire <= Time.now()) {
                    toRemove.add(uuid);
                }
            });

            toRemove.forEach(removed -> {
                new Scheduler(service.getPlugin()).sync(() -> {
                    Player player = Bukkit.getPlayer(removed);

                    if (player != null) {
                        ClassMessages.printConsumableUnlocked(
                                player,
                                Strings.capitalize(consumable.getEffectType().getKey().getKey().toLowerCase().replaceAll("_", " "))
                        );

                        ClassConsumableUnlockEvent unlockEvent = new ClassConsumableUnlockEvent(player, playerClass, consumable);
                        Bukkit.getPluginManager().callEvent(unlockEvent);
                    }
                }).run();

                consumable.getCooldowns().remove(removed);
            });
        }));
    }
}

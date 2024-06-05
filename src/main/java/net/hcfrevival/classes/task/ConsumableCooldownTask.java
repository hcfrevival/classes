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
import net.hcfrevival.classes.events.PlayerCallOfTheSeaCooldownExpireEvent;
import net.hcfrevival.classes.events.PlayerRiptideCooldownExpireEvent;
import net.hcfrevival.classes.types.impl.Diver;
import net.hcfrevival.classes.types.impl.Rogue;
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
        // Special Cooldowns
        service.getClassRepository().forEach(playerClass -> {
            // Diver Start
            if (playerClass instanceof Diver diver) {
                Set<UUID> toRemoveRiptide = Sets.newHashSet();
                Set<UUID> toRemoveCOTS = Sets.newHashSet();

                diver.getRiptideCooldowns().forEach((uuid, expire) -> {
                    if (expire <= Time.now()) {
                        toRemoveRiptide.add(uuid);
                    }
                });

                diver.getCallOfTheSeaCooldowns().forEach((uuid, expire) -> {
                    if (expire <= Time.now()) {
                        toRemoveCOTS.add(uuid);
                    }
                });

                toRemoveRiptide.forEach(removedUUID -> {
                    diver.getRiptideCooldowns().remove(removedUUID);
                    new Scheduler(service.getPlugin()).sync(() -> {
                        Player player = Bukkit.getPlayer(removedUUID);

                        if (player != null && player.isOnline()) {
                            Bukkit.getPluginManager().callEvent(new PlayerRiptideCooldownExpireEvent(player));
                        }
                    }).run();
                });

                toRemoveCOTS.forEach(removedUUID -> {
                    diver.getCallOfTheSeaCooldowns().remove(removedUUID);
                    new Scheduler(service.getPlugin()).sync(() -> {
                        Player player = Bukkit.getPlayer(removedUUID);

                        if (player != null && player.isOnline()) {
                            Bukkit.getPluginManager().callEvent(new PlayerCallOfTheSeaCooldownExpireEvent(player));
                        }
                    }).run();
                });
            }
            // Diver End

            // Rogue Start
            if (playerClass instanceof Rogue rogue) {
                if (!rogue.getBackstabCooldowns().isEmpty()) {
                    Set<UUID> toRemove = Sets.newHashSet();

                    rogue.getBackstabCooldowns().forEach((uuid, expire) -> {
                        if (expire <= Time.now()) {
                            toRemove.add(uuid);
                        }
                    });

                    toRemove.forEach(removedUUID -> {
                        rogue.getBackstabCooldowns().remove(removedUUID);
                        new Scheduler(service.getPlugin()).sync(() -> {
                            Player player = Bukkit.getPlayer(removedUUID);

                            if (player != null && player.isOnline()) {
                                ClassMessages.printConsumableUnlocked(player, "Backstab");
                            }
                        }).run();
                    });
                }

                if (!rogue.getCloakCooldowns().isEmpty()) {
                    Set<UUID> toRemove = Sets.newHashSet();

                    rogue.getCloakCooldowns().forEach((uuid, expire) -> {
                        if (expire <= Time.now()) {
                            toRemove.add(uuid);
                        }
                    });

                    toRemove.forEach(removedUUID -> {
                        rogue.getCloakCooldowns().remove(removedUUID);
                        new Scheduler(service.getPlugin()).sync(() -> {
                            Player player = Bukkit.getPlayer(removedUUID);

                            if (player != null && player.isOnline()) {
                                ClassMessages.printConsumableUnlocked(player, "Cloak");
                            }
                        }).run();
                    });
                }

                if (!rogue.getGrappleCooldowns().isEmpty()) {
                    Set<UUID> toRemove = Sets.newHashSet();

                    rogue.getGrappleCooldowns().forEach((uuid, expire) -> {
                        if (expire <= Time.now()) {
                            toRemove.add(uuid);
                        }
                    });

                    toRemove.forEach(removedUUID -> {
                        rogue.getBackstabCooldowns().remove(removedUUID);
                        new Scheduler(service.getPlugin()).sync(() -> {
                            Player player = Bukkit.getPlayer(removedUUID);

                            if (player != null && player.isOnline()) {
                                ClassMessages.printConsumableUnlocked(player, "Grapple");
                            }
                        }).run();
                    });
                }
            }
            // Rogue End
        });

        // Consumables
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

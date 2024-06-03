package net.hcfrevival.classes.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.events.ClassDeactivateEvent;
import net.hcfrevival.classes.events.ClassReadyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.UUID;

@Getter
public final class ClassArmorListener implements Listener {
    public final ClassService service;
    public final Set<UUID> recentlyLoggedIn;

    public ClassArmorListener(ClassService service) {
        this.service = service;
        this.recentlyLoggedIn = Sets.newConcurrentHashSet();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        recentlyLoggedIn.add(player.getUniqueId());

        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null) {
                continue;
            }

            final ItemMeta meta = armor.getItemMeta();

            if (meta == null) {
                continue;
            }

            if (!meta.getPersistentDataContainer().has(service.getNamespacedKey(), PersistentDataType.STRING)) {
                continue;
            }

            final String value = meta.getPersistentDataContainer().get(service.getNamespacedKey(), PersistentDataType.STRING);

            if (value == null || !value.equalsIgnoreCase("removeOnLogin")) {
                continue;
            }

            player.getInventory().remove(armor);
        }

        new Scheduler(service.getPlugin()).sync(() -> {
            service.getClassByArmor(player).ifPresent(playerClass -> {
                final ClassReadyEvent readyEvent = new ClassReadyEvent(player, playerClass);
                readyEvent.setMessagePrinted(false);
                Bukkit.getPluginManager().callEvent(readyEvent);
            });

            recentlyLoggedIn.remove(player.getUniqueId());

            player.getActivePotionEffects()
                    .stream()
                    .filter(e -> e.getDuration() >= 10000 || e.getDuration() == -1)
                    .forEach(infE -> player.removePotionEffect(infE.getType()));
        }).delay(3L).run();
    }

    @EventHandler /* Removes player from class upon disconnect */
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        service.getCurrentClass(player).ifPresent(playerClass -> {
            player.getActivePotionEffects()
                    .stream()
                    .filter(e -> e.getDuration() >= 10000 || e.getDuration() == -1)
                    .forEach(infE -> player.removePotionEffect(infE.getType()));

            final ClassDeactivateEvent deactivateEvent = new ClassDeactivateEvent(player, playerClass);
            Bukkit.getPluginManager().callEvent(deactivateEvent);

            playerClass.deactivate(player);
        });
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        if (event.getOldItem().getType().isAir() && event.getNewItem().getType().isAir() && event.getOldItem().getType().equals(event.getNewItem().getType())) {
            return;
        }

        final Player player = event.getPlayer();

        if (recentlyLoggedIn.contains(player.getUniqueId())) {
            return;
        }

        service.validateClass(player);
    }
}

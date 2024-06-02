package net.hcfrevival.classes.consumables;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import net.hcfrevival.classes.events.ClassConsumeItemEvent;
import net.hcfrevival.classes.types.IClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface IClassConsumable {
    IClass getParent();
    Material getMaterial();
    EConsumableApplicationType getApplicationType();
    PotionEffectType getEffectType();
    int getDuration();
    int getCooldown();
    int getAmplifier();
    Map<UUID, Long> getCooldowns();

    default boolean hasCooldown(Player player) {
        return hasCooldown(player.getUniqueId());
    }

    default boolean hasCooldown(UUID uuid) {
        return getCooldowns().containsKey(uuid);
    }

    default long getCooldown(Player player) {
        return getCooldowns().getOrDefault(player.getUniqueId(), 0L);
    }

    default void consume(Player player, ItemStack item) {
        Set<UUID> nearbyPlayers = Sets.newHashSet();
        player.getWorld().getNearbyPlayers(
                player.getLocation(),
                getParent().getService().getGlobalConfig().getConsumableRadius()).forEach(nearbyPlayer ->
                    nearbyPlayers.add(nearbyPlayer.getUniqueId()));

        ClassConsumeItemEvent consumeEvent = new ClassConsumeItemEvent(player, this, nearbyPlayers);
        Bukkit.getPluginManager().callEvent(consumeEvent);
        if (consumeEvent.isCancelled()) {
            return;
        }

        // take item
        item.subtract(1);

        // give cooldown
        getCooldowns().put(player.getUniqueId(), (Time.now() + (getCooldown() * 1000L)));

        // apply & message players
        Component receivedComponent = Component.text("You now have", NamedTextColor.LIGHT_PURPLE)
                .appendSpace().append(Component.text(Strings.capitalize(getEffectType().getKey().getKey().toLowerCase().replaceAll("_", " ")), NamedTextColor.AQUA))
                .appendSpace().append(Component.text(getAmplifier() + 1, NamedTextColor.AQUA))
                .appendSpace().append(Component.text("for", NamedTextColor.LIGHT_PURPLE))
                .appendSpace().append(Component.text(getDuration() + " seconds", NamedTextColor.AQUA));

        consumeEvent.getAffectedPlayers().forEach(affectedUUID -> {
            Player affectedPlayer = Bukkit.getPlayer(affectedUUID);

            if (affectedPlayer == null) {
                return;
            }

            if (affectedPlayer.getUniqueId().equals(player.getUniqueId()) && getApplicationType().equals(EConsumableApplicationType.ENEMY)) {
                return;
            }

            PotionEffect previousEffect = affectedPlayer.getPotionEffect(getEffectType());
            boolean wasClassPassive = getParent().getConfig().getPassiveEffects().containsKey(getEffectType());

            if (affectedPlayer.hasPotionEffect(getEffectType())) {
                affectedPlayer.removePotionEffect(getEffectType());
            }

            affectedPlayer.addPotionEffect(new PotionEffect(getEffectType(), (getDuration() * 20), getAmplifier()));
            affectedPlayer.sendMessage(receivedComponent);

            if (previousEffect != null) {
                new Scheduler(getParent().getService().getPlugin()).sync(() -> {
                    if (wasClassPassive && !getParent().getActivePlayers().contains(affectedUUID)) {
                        return;
                    }

                    affectedPlayer.addPotionEffect(previousEffect);
                }).delay((getDuration() * 20L) + 1L).run();
            }
        });
    }
}

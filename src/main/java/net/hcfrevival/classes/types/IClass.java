package net.hcfrevival.classes.types;

import net.hcfrevival.classes.ClassMessages;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.IClassConfig;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.events.ClassActivateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IClass {
    ClassService getService();
    String getName();
    String getDescription();
    Material getIcon();
    default Material getHelmet() { return null; }
    default Material getChestplate() { return null; }
    default Material getLeggings() { return null; }
    default Material getBoots() { return null; }
    default Material getOffHand() { return null; }
    IClassConfig getConfig();
    Set<UUID> getActivePlayers();
    boolean isEmptyArmorEnforced();

    default Optional<IClassConsumable> getConsumableByMaterial(Material material) {
        return getConfig().getConsumables().stream().filter(c -> c.getMaterial().equals(material)).findFirst();
    }

    default void activate(Player player, boolean printMessage) {
        if (!hasArmorRequirements(player)) {
            player.sendMessage(Component.text("You do not meet the armor requirement to use this class", NamedTextColor.RED));
            return;
        }

        if (getActivePlayers().contains(player.getUniqueId())) {
            return;
        }

        ClassActivateEvent activateEvent = new ClassActivateEvent(player, this);
        Bukkit.getPluginManager().callEvent(activateEvent);
        if (activateEvent.isCancelled()) {
            return;
        }

        if (printMessage) {
            ClassMessages.printClassActivated(player, getName(), getDescription());
        }

        getConfig().getPassiveEffects().forEach((effect, amplifier) -> {
            if (player.hasPotionEffect(effect)) {
                player.removePotionEffect(effect);
            }

            player.addPotionEffect(new PotionEffect(effect, PotionEffect.INFINITE_DURATION, amplifier));
        });

        getActivePlayers().add(player.getUniqueId());
    }

    default void activate(Player player) {
        activate(player, true);
    }

    default void deactivate(Player player, boolean printMessage) {
        getConfig().getPassiveEffects().keySet().forEach(player::removePotionEffect);
        getActivePlayers().remove(player.getUniqueId());

        if (printMessage) {
            ClassMessages.printClassDeactivated(player, getName());
        }
    }

    default void deactivate(Player player) {
        deactivate(player, true);
    }

    default boolean hasArmorRequirements(Player player) {
        // edge cases for more abstract classes
        // this exemption allows tank to have a banner set
        // to their head once the class activates without breaking
        // the armor change detection
        if (this instanceof Guardian) {
            if (player.getEquipment().getHelmet() != null) {
                final ItemStack helmet = player.getEquipment().getHelmet();

                if (!helmet.getType().name().contains("_BANNER")) {
                    return false;
                }
            }
        }

        if (isEmptyArmorEnforced()) {
            if (getHelmet() == null && player.getEquipment().getHelmet() != null) {
                return false;
            }

            if (getChestplate() == null && player.getEquipment().getChestplate() != null) {
                return false;
            }

            if (getLeggings() == null && player.getEquipment().getLeggings() != null) {
                return false;
            }

            if (getBoots() == null && player.getEquipment().getBoots() != null) {
                return false;
            }
        }

        if (getHelmet() != null) {
            if (player.getEquipment().getHelmet() == null && isEmptyArmorEnforced()) {
                return false;
            }

            if (player.getEquipment().getHelmet() == null || !player.getEquipment().getHelmet().getType().equals(getHelmet())) {
                return false;
            }
        }

        if (getChestplate() != null) {
            if (player.getEquipment().getChestplate() == null && isEmptyArmorEnforced()) {
                return false;
            }

            if (player.getEquipment().getChestplate() == null || !player.getEquipment().getChestplate().getType().equals(getChestplate())) {
                return false;
            }
        }

        if (getLeggings() != null) {
            if (player.getEquipment().getLeggings() == null && isEmptyArmorEnforced()) {
                return false;
            }

            if (player.getEquipment().getLeggings() == null || !player.getEquipment().getLeggings().getType().equals(getLeggings())) {
                return false;
            }
        }

        if (getBoots() != null) {
            if (player.getEquipment().getBoots() == null && isEmptyArmorEnforced()) {
                return false;
            }

            if (player.getEquipment().getBoots() == null || !player.getEquipment().getBoots().getType().equals(getBoots())) {
                return false;
            }
        }

        if (getOffHand() != null) {
            if (this instanceof final Guardian guardian) {
               /* if (!guardian.hasDrainedStamina(player)) {
                    return player.getEquipment().getItemInOffHand().getType().equals(getOffhand());
                } TODO: Add this back when guardian is implemented */
            } else {
                return player.getEquipment().getItemInOffHand().getType().equals(getOffHand());
            }
        }

        return true;
    }
}

package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.types.IClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Optional;

@AllArgsConstructor
public final class ConsumableListener implements Listener {
    @Getter public final ClassService service;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.isEmpty()) {
            return;
        }

        service.getCurrentClass(player).flatMap(playerClass -> playerClass.getConsumableByMaterial(item.getType())).ifPresent(consumable -> {
            if (consumable.hasCooldown(player)) {
                String timeDisplay = Time.convertToDecimal(consumable.getCooldown(player) - Time.now());

                Component component = Component.text(Strings.capitalize(consumable.getEffectType().getKey().getKey().toLowerCase().replaceAll("_", " ")), NamedTextColor.RED)
                                .appendSpace().append(Component.text("is locked for", NamedTextColor.RED))
                                .appendSpace().append(Component.text(timeDisplay, NamedTextColor.RED).decorate(TextDecoration.BOLD))
                                .append(Component.text("s", NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));

                player.sendMessage(component);
                return;
            }

            consumable.consume(player, item);
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEffectExpire(EntityPotionEffectEvent event) {
        final PotionEffect effect = event.getOldEffect();

        if (effect == null) {
            return;
        }

        if (!(event.getEntity() instanceof final Player player)) {
            return;
        }

        final EntityPotionEffectEvent.Action action = event.getAction();

        if (!action.equals(EntityPotionEffectEvent.Action.REMOVED) && !action.equals(EntityPotionEffectEvent.Action.CLEARED)) {
            return;
        }

        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!playerClass.getConfig().getPassiveEffects().containsKey(effect.getType())) {
                return;
            }

            final int amplifier = playerClass.getConfig().getPassiveEffects().get(effect.getType());

            new Scheduler(service.getPlugin()).sync(() -> {
                Optional<IClass> futureClassQuery = service.getCurrentClass(player);

                if (futureClassQuery.isPresent() && futureClassQuery.get() == playerClass) {
                    player.addPotionEffect(new PotionEffect(effect.getType(), PotionEffect.INFINITE_DURATION, amplifier));
                }
            }).delay(1L).run();
        });
    }
}

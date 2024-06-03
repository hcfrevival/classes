package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.base.util.Strings;
import gg.hcfactions.libs.base.util.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
}

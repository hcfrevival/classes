package net.hcfrevival.classes.listener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.holdable.IClassHoldable;
import net.hcfrevival.classes.holdable.IHoldableClass;
import net.hcfrevival.classes.types.impl.Bard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
@AllArgsConstructor
public final class HoldableListener implements Listener {
    public final ClassService service;

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerHeldItemChange(PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item == null) {
            return;
        }

        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!(playerClass.getConfig() instanceof final IHoldableClass holdableClass)) {
                return;
            }

            final Optional<IClassHoldable> holdableQuery = holdableClass.getHoldable(item.getType());

            if (holdableQuery.isEmpty()) {
                return;
            }

            final IClassHoldable holdable = holdableQuery.get();

            if (!holdableClass.shouldReapplyHoldable(player.getUniqueId(), holdable)) {
                return;
            }

            final double range = (playerClass instanceof final Bard bard) ? bard.getConfig().getEffectRange() : 16.0;
            holdable.apply(player, holdableClass.getHoldableUpdateRate(), range, true);
        });
    }
}

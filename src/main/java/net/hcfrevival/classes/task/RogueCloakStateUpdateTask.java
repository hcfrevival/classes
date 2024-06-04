package net.hcfrevival.classes.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassMessages;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.types.impl.Rogue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
public final class RogueCloakStateUpdateTask implements Runnable {
    @Getter public final ClassService service;

    @Override
    public void run() {
        service.getClassByName("Rogue").ifPresent(playerClass -> {
            Rogue rogue = (Rogue) playerClass;

            rogue.getInvisiblePlayers().forEach((uuid, currentState) -> {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null) {
                    return;
                }

                final Rogue.EInvisibilityState expectedState = rogue.getExpectedInvisibilityState(player);
                ClassMessages.renderVanishState(player, expectedState);

                if (expectedState == currentState) {
                    return;
                }

                rogue.updateInvisibilityState(player, expectedState);
            });
        });
    }
}

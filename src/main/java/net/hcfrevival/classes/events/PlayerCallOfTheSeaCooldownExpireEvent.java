package net.hcfrevival.classes.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerCallOfTheSeaCooldownExpireEvent extends PlayerEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();

    public PlayerCallOfTheSeaCooldownExpireEvent(Player who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

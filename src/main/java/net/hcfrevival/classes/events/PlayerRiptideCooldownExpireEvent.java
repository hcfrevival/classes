package net.hcfrevival.classes.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerRiptideCooldownExpireEvent extends PlayerEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();

    public PlayerRiptideCooldownExpireEvent(Player who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

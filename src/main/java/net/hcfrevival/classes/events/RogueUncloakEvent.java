package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class RogueUncloakEvent extends PlayerEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter @Setter public String reason;

    public RogueUncloakEvent(@NotNull Player who, String reason) {
        super(who);
        this.reason = reason;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

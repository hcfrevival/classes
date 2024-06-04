package net.hcfrevival.classes.events;

import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public final class RogueInvisibilityQueryEvent extends ClassEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final Collection<Player> withinFullRadiusPlayers;
    @Getter public final Collection<Player> withinPartialRadiusPlayers;

    public RogueInvisibilityQueryEvent(
            @NotNull Player who,
            @NotNull IClass playerClass,
            Collection<Player> withinFullRadiusPlayers,
            Collection<Player> withinPartialRadiusPlayers
    ) {
        super(who, playerClass);
        this.withinFullRadiusPlayers = withinFullRadiusPlayers;
        this.withinPartialRadiusPlayers = withinPartialRadiusPlayers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

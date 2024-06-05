package net.hcfrevival.classes.events;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.holdable.IClassHoldable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public final class ClassHoldableUpdateEvent extends ClassEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final Set<UUID> affectedPlayers;
    @Getter public final IClassHoldable holdable;
    @Getter @Setter public boolean cancelled;

    public ClassHoldableUpdateEvent(Player player, IClass playerClass, IClassHoldable holdable, Collection<UUID> affectedPlayers) {
        super(player, playerClass);
        this.affectedPlayers = Sets.newHashSet(affectedPlayers);
        this.holdable = holdable;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

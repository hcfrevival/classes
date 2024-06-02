package net.hcfrevival.classes.events;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.consumables.IClassConsumable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class ClassConsumeItemEvent extends ClassEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final IClassConsumable consumable;
    @Getter public final Set<UUID> affectedPlayers;
    @Getter @Setter public boolean cancelled;

    public ClassConsumeItemEvent(@NotNull Player who, @NotNull IClassConsumable consumable, Set<UUID> affectedPlayers) {
        super(who, consumable.getParent());
        this.consumable = consumable;
        this.affectedPlayers = affectedPlayers;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

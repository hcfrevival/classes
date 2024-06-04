package net.hcfrevival.classes.events;

import com.google.common.collect.Sets;
import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public final class RogueVanishPlayerUpdateEvent extends ClassEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final Set<UUID> toHideFrom;

    public RogueVanishPlayerUpdateEvent(@NotNull Player who, @NotNull IClass playerClass) {
        super(who, playerClass);
        this.toHideFrom = Sets.newHashSet();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

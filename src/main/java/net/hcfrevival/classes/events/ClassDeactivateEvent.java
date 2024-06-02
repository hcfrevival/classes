package net.hcfrevival.classes.events;

import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ClassDeactivateEvent extends ClassEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();

    public ClassDeactivateEvent(@NotNull Player who, @NotNull IClass playerClass) {
        super(who, playerClass);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

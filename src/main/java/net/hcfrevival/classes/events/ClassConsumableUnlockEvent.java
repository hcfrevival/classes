package net.hcfrevival.classes.events;

import lombok.Getter;
import net.hcfrevival.classes.consumables.IClassConsumable;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ClassConsumableUnlockEvent extends ClassEvent {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final IClassConsumable consumable;

    public ClassConsumableUnlockEvent(@NotNull Player who, @NotNull IClass playerClass, IClassConsumable consumable) {
        super(who, playerClass);
        this.consumable = consumable;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class RogueBackstabEvent extends ClassEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final LivingEntity damagedEntity;
    @Getter @Setter public boolean cancelled;

    public RogueBackstabEvent(@NotNull Player who, @NotNull IClass playerClass, @NonNull LivingEntity damagedEntity) {
        super(who, playerClass);
        this.damagedEntity = damagedEntity;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

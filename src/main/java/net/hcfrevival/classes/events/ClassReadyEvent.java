package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ClassReadyEvent extends ClassEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter @Setter public boolean cancelled;
    @Getter @Setter public boolean messagePrinted;

    public ClassReadyEvent(Player who, IClass playerClass) {
        super(who, playerClass);
        this.cancelled = false;
        this.messagePrinted = true;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.Setter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Setter
public final class ClassActivateEvent extends ClassEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public boolean messagePrinted;
    @Getter public boolean cancelled;

    public ClassActivateEvent(Player who, IClass playerClass) {
        super(who, playerClass);
        this.messagePrinted = true;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

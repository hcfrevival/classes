package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public final class DiverSeaCallEvent extends PlayerEvent implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter @Setter public Component displayName;
    @Getter @Setter public boolean cancelled;

    public DiverSeaCallEvent(Player who) {
        super(who);
        this.displayName = Component.text(who.getName(), NamedTextColor.WHITE);
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

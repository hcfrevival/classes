package net.hcfrevival.classes.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ArcherTagEvent extends Event implements Cancellable {
    @Getter public static final HandlerList handlerList = new HandlerList();
    @Getter public final Player archer;
    @Getter public final LivingEntity victim;
    @Getter @Setter public double finalDamage;
    @Getter @Setter public double distance;
    @Getter @Setter public double hitCount;
    @Getter @Setter public boolean cancelled;

    public ArcherTagEvent(Player archer, LivingEntity victim, double finalDamage, double distance, double hitCount) {
        this.archer = archer;
        this.victim = victim;
        this.finalDamage = finalDamage;
        this.distance = distance;
        this.hitCount = hitCount;
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}

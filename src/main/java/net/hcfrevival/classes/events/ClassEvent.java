package net.hcfrevival.classes.events;

import lombok.Getter;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class ClassEvent extends PlayerEvent {
    public final IClass playerClass;

    public ClassEvent(@NotNull Player who, @NotNull IClass playerClass) {
        super(who);
        this.playerClass = playerClass;
    }
}

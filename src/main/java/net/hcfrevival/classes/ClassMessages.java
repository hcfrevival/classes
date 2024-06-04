package net.hcfrevival.classes;

import net.hcfrevival.classes.types.impl.Rogue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class ClassMessages {
    public static final TextColor LAYER1 = NamedTextColor.GOLD;
    public static final TextColor LAYER2 = NamedTextColor.YELLOW;

    public static void printClassActivated(Player viewer, String className, String description) {
        Component component = Component.text("Class Activated", LAYER1)
                .append(Component.text(": " + className, LAYER2));

        viewer.sendMessage(component);

        if (description != null) {
            viewer.sendMessage(Component.text(description, LAYER2));
        }
    }

    public static void printClassDeactivated(Player viewer, String className) {
        Component component = Component.text("Class Deactivated", LAYER1)
                .append(Component.text(": " + className, LAYER2));

        viewer.sendMessage(component);
    }

    public static void printConsumableUnlocked(Player viewer, String itemName) {
        Component component = Component.text("Your " + itemName + " has been unlocked", NamedTextColor.GREEN);
        viewer.sendMessage(component);
    }

    public static void printRogueUncloak(Player viewer, String reason) {
        Component component = Component.text("Uncloaked!", NamedTextColor.RED).decorate(TextDecoration.BOLD)
                .appendSpace().append(Component.text("You are now visible because you", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                .appendSpace().append(Component.text(reason, NamedTextColor.GOLD).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));
    }

    public static void renderVanishState(Player player, Rogue.EInvisibilityState state) {
        Component stateComponent = Component.empty();

        switch (state) {
            case FULL -> stateComponent = Component.text("Fully Cloaked", NamedTextColor.DARK_PURPLE);
            case PARTIAL -> stateComponent = Component.text("Partially Cloaked", NamedTextColor.LIGHT_PURPLE);
            case NONE -> stateComponent = Component.text("Cloak Disabled", NamedTextColor.GRAY);
        }

        player.sendActionBar(stateComponent);
    }
}

package net.hcfrevival.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
}

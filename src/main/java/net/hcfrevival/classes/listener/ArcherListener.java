package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.events.ArcherMarkEvent;
import net.hcfrevival.classes.events.ArcherTagEvent;
import net.hcfrevival.classes.types.impl.Archer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
public final class ArcherListener implements Listener {
    @Getter public final ClassService service;

    @EventHandler
    public void onArcherTag(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Arrow arrow)) {
            return;
        }

        if (arrow instanceof SpectralArrow) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity damaged)) {
            return;
        }

        if (!(arrow.getShooter() instanceof Player damager)) {
            return;
        }

        service.getCurrentClass(damager).ifPresent(playerClass -> {
            if (!(playerClass instanceof Archer archer)) {
                return;
            }

            double damage = event.getFinalDamage();
            double maxDamage = archer.getConfig().getMaxDamageDealt();
            double damagePerBlock = archer.getConfig().getDamagePerBlock();
            Location locA = damager.getLocation().clone();
            Location locB = damaged.getLocation().clone();

            locA.setY(0.0);
            locB.setY(0.0);

            double distance = locA.distance(locB);
            int hitCount = archer.getTagCount(damager, damaged);
            double distanceDamage = (damagePerBlock * distance);
            double consecutiveDamage = (archer.getConfig().getConsecutiveBase() * ((hitCount + 1) * archer.getConfig().getConsecutiveMultiplier()));
            final double finalDamage = Math.min((distanceDamage + consecutiveDamage + damage), maxDamage);

            ArcherTagEvent tagEvent = new ArcherTagEvent(damager, damaged, finalDamage, distance, hitCount);
            Bukkit.getPluginManager().callEvent(tagEvent);
            if (tagEvent.isCancelled()) {
                return;
            }

            archer.tag(damager, damaged, 10);
            event.setDamage(tagEvent.getFinalDamage());

            if (damaged instanceof Player damagedPlayer) {
                Component component = Component.text("You have been shot by an", NamedTextColor.RED)
                        .appendSpace().append(Component.text("ARCHER!", NamedTextColor.DARK_RED)).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE);

                damagedPlayer.sendMessage(component);
            }

            final double preHealth = damaged.getHealth();
            new Scheduler(service.getPlugin()).sync(() -> {
                final double postHealth = damaged.getHealth();
                final double diff = (preHealth - postHealth) / 2;
                final Component victimNameComponent = damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                        ? Component.text("? ? ?", NamedTextColor.GRAY)
                        : Component.text(damaged.getName(), NamedTextColor.GOLD
                );

                Component component = Component.text("Your arrow has", NamedTextColor.YELLOW)
                        .appendSpace().append(Component.text("pierced", NamedTextColor.RED))
                        .appendSpace().append(victimNameComponent)
                        .appendSpace().append(Component.text("from a distance of", NamedTextColor.YELLOW))
                        .appendSpace().append(Component.text(String.format("%.2f", distance) + " blocks", NamedTextColor.DARK_AQUA)
                        .appendSpace().append(Component.text("(", NamedTextColor.YELLOW))
                        .append(Component.text(String.format("%.2f", diff) + " ❤", NamedTextColor.RED)));

                if ((hitCount + 1) > 1) {
                    component = component.appendSpace().append(Component.text("x" + (hitCount + 1), NamedTextColor.GOLD));
                }

                component = component.append(Component.text(")", NamedTextColor.YELLOW));

                damager.sendMessage(component);
            }).delay(1L).run();
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onArcherMark(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof SpectralArrow spectralArrow)) {
            return;
        }

        if (!(spectralArrow.getShooter() instanceof Player shooter)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity damaged)) {
            return;
        }

        service.getCurrentClass(shooter).ifPresent(playerClass -> {
            if (!(playerClass instanceof Archer archer)) {
                return;
            }

            if (archer.getMarkedEntities().contains(damaged.getUniqueId())) {
                return;
            }

            ArcherMarkEvent markEvent = new ArcherMarkEvent(shooter, damaged, (archer.getConfig().getMarkDuration() * 20));
            Bukkit.getPluginManager().callEvent(markEvent);
            if (markEvent.isCancelled()) {
                return;
            }

            spectralArrow.setGlowingTicks(markEvent.getTicks());
            archer.mark(damaged);

            Component damagedNameComponent = (damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                    ? Component.text("? ? ?", NamedTextColor.GRAY)
                    : Component.text(damaged.getName(), NamedTextColor.GOLD));

            int percent = (int)Math.round(archer.getConfig().getMarkDamagePercentage() * 100);

            Component shooterComponent = Component.text("Your arrow has", NamedTextColor.YELLOW)
                    .appendSpace().append(Component.text("marked", NamedTextColor.RED))
                    .appendSpace().append(damagedNameComponent)
                    .appendSpace().append(Component.text("for", NamedTextColor.YELLOW))
                    .appendSpace().append(Component.text(archer.getConfig().getMarkDuration() + " seconds", NamedTextColor.DARK_AQUA));

            shooter.sendMessage(shooterComponent);

            if (damaged instanceof Player damagedPlayer) {
                Component damagedComponent = Component.text("MARKED!", NamedTextColor.RED).decorate(TextDecoration.BOLD)
                        .appendSpace().append(Component.text("You will take", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .appendSpace().append(Component.text(percent + "% Increased Damage", NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .appendSpace().append(Component.text("for", NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
                        .appendSpace().append(Component.text(archer.getConfig().getMarkDuration() + " seconds", NamedTextColor.DARK_AQUA).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE));

                damagedPlayer.sendMessage(damagedComponent);
            }
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onArcherMarkDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity damaged)) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        service.getClassByName("Archer").ifPresent(playerClass -> {
            Archer archer = (Archer) playerClass;

            if (archer.getMarkedEntities().contains(damaged.getUniqueId())) {
                event.setDamage(event.getDamage() + (event.getDamage() * archer.getConfig().getMarkDamagePercentage()));
            }
        });
    }
}

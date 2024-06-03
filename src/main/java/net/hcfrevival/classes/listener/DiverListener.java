package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.utils.Players;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.events.DiverSeaCallEvent;
import net.hcfrevival.classes.events.PlayerCallOfTheSeaCooldownExpireEvent;
import net.hcfrevival.classes.events.PlayerRiptideCooldownExpireEvent;
import net.hcfrevival.classes.types.IClass;
import net.hcfrevival.classes.types.impl.Diver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

@AllArgsConstructor
public final class DiverListener implements Listener {
    @Getter public final ClassService service;

    @EventHandler
    public void onRiptideUnlock(PlayerRiptideCooldownExpireEvent event) {
        event.getPlayer().sendMessage(Component.text("Your Riptide Trident has been unlocked", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onCOTSUnlock(PlayerCallOfTheSeaCooldownExpireEvent event) {
        event.getPlayer().sendMessage(Component.text("Your Heart of the Sea has been unlocked", NamedTextColor.GREEN));
    }

    @EventHandler
    public void onCallOfTheSea(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();
        ItemStack item = event.getItem();

        if (item == null || !item.getType().equals(Material.HEART_OF_THE_SEA)) {
            return;
        }

        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!(playerClass instanceof Diver diver)) {
                return;
            }

            if (diver.hasCallOfTheSeaCooldown(player)) {
                long expire = diver.getCallOfTheSeaCooldown(player);
                String formatted = Time.convertToHHMMSS(expire - Time.now());
                event.setUseItemInHand(Event.Result.DENY);
                player.sendMessage(Component.text("You can not use Heart of the Sea for another", NamedTextColor.RED)
                        .appendSpace().append(Component.text(formatted, NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)));
                return;
            }

            if (!world.getEnvironment().equals(World.Environment.NORMAL)) {
                player.sendMessage(Component.text("It can not rain in this world", NamedTextColor.RED));
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }

            if (world.hasStorm()) {
                player.sendMessage(Component.text("It is already raining", NamedTextColor.RED));
                return;
            }

            DiverSeaCallEvent seaCallEvent = new DiverSeaCallEvent(player);
            Bukkit.getPluginManager().callEvent(seaCallEvent);
            if (seaCallEvent.isCancelled()) {
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }

            Component component = Component.text("The sea calls for", TextColor.color(0x007291))
                    .appendSpace().append(seaCallEvent.getDisplayName());

            item.subtract(1);
            world.setStorm(true);
            diver.setCallOfTheSeaCooldown(player);

            world.getPlayers().forEach(worldPlayer -> {
                worldPlayer.sendMessage(component);
                Players.playSound(worldPlayer, Sound.ITEM_GOAT_HORN_SOUND_3);
            });
        });
    }

    @EventHandler
    public void onTridentLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Trident trident)) {
            return;
        }

        if (!(trident.getShooter() instanceof Player shooter)) {
            return;
        }

        service.getCurrentClass(shooter).ifPresentOrElse(playerClass -> {
            if (!(playerClass instanceof Diver)) {
                shooter.sendMessage(Component.text("Tridents can only be used by the Diver class", NamedTextColor.RED));
                event.setCancelled(true);
            }
        }, () -> {
            shooter.sendMessage(Component.text("Tridents can only be used by the Diver class", NamedTextColor.RED));
            event.setCancelled(true);
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTridentHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Trident trident)) {
            return;
        }

        if (!(trident.getShooter() instanceof Player attacker)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity damaged)) {
            return;
        }

        Optional<IClass> classQuery = service.getCurrentClass(attacker);
        if (classQuery.isEmpty()) {
            service.getPlugin().getAresLogger().error("EntityDamageByEntity event fired with trident but could not find an attached diver class");
            return;
        }

        IClass playerClass = classQuery.get();
        if (!(playerClass instanceof Diver diver)) {
            service.getPlugin().getAresLogger().error("EntityDamageByEntity event fired with trident but class was not diver");
            return;
        }

        if (diver.hasRiptideCooldown(attacker)) {
            diver.getRiptideCooldowns().remove(attacker.getUniqueId());

            PlayerRiptideCooldownExpireEvent expireEvent = new PlayerRiptideCooldownExpireEvent(attacker);
            Bukkit.getPluginManager().callEvent(expireEvent);
        }

        final Location locA = attacker.getLocation().clone();
        final Location locB = damaged.getLocation().clone();

        locA.setY(0.0);
        locB.setY(0.0);
        final double dist = locA.distance(locB);
        final double damage = (diver.getConfig().getDamagePerBlock() * dist) + event.getFinalDamage();

        event.setDamage(damage);

        if (damaged instanceof Player damagedPlayer) {
            Component component = Component.text("You have been pierced by a", NamedTextColor.RED)
                    .appendSpace().append(Component.text("DIVER!", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE));

            damagedPlayer.sendMessage(component);
        }

        final double preHealth = damaged.getHealth();
        new Scheduler(service.getPlugin()).sync(() -> {
            final double postHealth = preHealth - damage;
            final double diff = (preHealth - postHealth) / 2;
            final Component victimNameComponent = damaged.hasPotionEffect(PotionEffectType.INVISIBILITY)
                    ? Component.text("? ? ?", NamedTextColor.GRAY)
                    : Component.text(damaged.getName(), NamedTextColor.GOLD
            );

            Component component = Component.text("Your trident has", NamedTextColor.YELLOW)
                    .appendSpace().append(Component.text("pierced", NamedTextColor.RED))
                    .appendSpace().append(victimNameComponent)
                    .appendSpace().append(Component.text("from a distance of", NamedTextColor.YELLOW))
                    .appendSpace().append(Component.text(String.format("%.2f", dist) + " blocks", NamedTextColor.DARK_AQUA)
                            .appendSpace().append(Component.text("(", NamedTextColor.YELLOW))
                            .append(Component.text(String.format("%.2f", diff) + " ❤", NamedTextColor.RED)))
                    .append(Component.text(")", NamedTextColor.YELLOW));

            attacker.sendMessage(component);
        }).delay(1L).run();
    }

    @EventHandler
    public void onRiptide(PlayerRiptideEvent event) {
        Player player = event.getPlayer();
        final Location prevLoc = player.getLocation();

        service.getCurrentClass(player).ifPresentOrElse(playerClass -> {
            if (!(playerClass instanceof Diver diver)) {
                new Scheduler(service.getPlugin()).sync(() -> {
                    player.teleport(prevLoc);
                    player.sendMessage(Component.text("Riptide can only be used by the Diver class", NamedTextColor.RED));
                }).delay(1L).run();

                return;
            }

            if (diver.hasRiptideCooldown(player)) {
                final long expire = diver.getRiptideCooldown(player);
                String formatted = Time.convertToDecimal(expire - Time.now());

                new Scheduler(service.getPlugin()).sync(() -> {
                    player.teleport(prevLoc);
                    player.sendMessage(Component.text("You can not use Riptide for another", NamedTextColor.RED)
                            .appendSpace().append(Component.text(formatted, NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                            .append(Component.text("s", NamedTextColor.RED)));
                }).delay(1L).run();

                return;
            }

            diver.setRiptideCooldown(player);
        }, () -> new Scheduler(service.getPlugin()).sync(() -> {
            player.teleport(prevLoc);
            player.sendMessage(Component.text("Riptide can only be used by the Diver class", NamedTextColor.RED));
        }).delay(1L).run());
    }
}

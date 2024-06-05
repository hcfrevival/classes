package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.base.util.Time;
import gg.hcfactions.libs.bukkit.events.impl.PlayerLingeringSplashEvent;
import gg.hcfactions.libs.bukkit.events.impl.PlayerSplashPlayerEvent;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import gg.hcfactions.libs.bukkit.utils.Worlds;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.events.RogueBackstabEvent;
import net.hcfrevival.classes.types.impl.Rogue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

@AllArgsConstructor
public final class RogueListener implements Listener {
    @Getter public final ClassService service;

    private void handleUncloak(Player player, String reason) {
        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!(playerClass instanceof Rogue rogue)) {
                return;
            }

            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                if (!onlinePlayer.canSee(player)) {
                    onlinePlayer.showPlayer(service.getPlugin(), player);
                }
            });

            if (!rogue.getInvisibilityStates().containsKey(player.getUniqueId()) || rogue.getInvisibilityStates().get(player.getUniqueId()).equals(Rogue.EInvisibilityState.NONE)) {
                return;
            }

            rogue.unvanishPlayer(player, reason);
            rogue.getInvisibilityStates().remove(player.getUniqueId());
        });
    }

    @EventHandler
    public void onPlayerCloak(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.getType().equals(Material.ENDER_EYE)) {
            return;
        }

        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!(playerClass instanceof Rogue rogue)) {
                return;
            }

            if (rogue.hasCloakCooldown(player)) {
                long expire = rogue.getCloakCooldown(player);
                String formatted = Time.convertToHHMMSS((expire - Time.now()));
                player.sendMessage(Component.text("You can not use your cloak for another", NamedTextColor.RED)
                        .appendSpace().append(Component.text(formatted, NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)));
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }

            if (rogue.isInvisible(player)) {
                player.sendMessage(Component.text("You are already cloaked", NamedTextColor.RED));
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }

            Rogue.EInvisibilityState entryState = rogue.getExpectedInvisibilityState(player);

            if (!entryState.equals(Rogue.EInvisibilityState.FULL)) {
                player.sendMessage(Component.text("You can not cloak while enemies are nearby", NamedTextColor.RED));
                event.setUseItemInHand(Event.Result.DENY);
                return;
            }

            event.setUseItemInHand(Event.Result.DENY);
            item.subtract(1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
            player.sendMessage(Component.text("You are now hidden from other players", NamedTextColor.LIGHT_PURPLE));

            rogue.getInvisibilityStates().put(player.getUniqueId(), entryState);
            rogue.updateInvisibilityState(player, entryState);
            rogue.setInvisibilityCooldown(player);
            rogue.vanishPlayer(player);
        });
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof final Player player) {
            handleUncloak(player, "took damage");
            return;
        }

        if (event.getDamager() instanceof final Player player) {
            handleUncloak(player, "inflicted damage");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleUncloak(event.getPlayer(), "broke a block");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        handleUncloak(event.getPlayer(), "placed a block");
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        if (!event.getClickedBlock().getType().isInteractable()) {
            return;
        }

        handleUncloak(event.getPlayer(), "interacted with a block");
    }

    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }

        handleUncloak(player, "fired a projectile");
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        handleUncloak(event.getPlayer(), "changed worlds");
    }

    @EventHandler
    public void onPlayerSplash(PlayerSplashPlayerEvent event) {
        Player damaged = event.getDamaged();
        Player damager = event.getDamager();

        handleUncloak(damaged, "were impacted by a potion");
        handleUncloak(damager, "impacted a player with a potion");
    }

    @EventHandler
    public void onLingeringSplash(PlayerLingeringSplashEvent event) {
        Player damaged = event.getDamaged();
        Player damager = event.getDamager();

        handleUncloak(damaged, "were impacted by a lingering potion");
        handleUncloak(damager, "impacted a player with a lingering potion");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        handleUncloak(event.getPlayer(), "died");
    }

    @EventHandler
    public void onEntityTargetRogue(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }

        service.getCurrentClass(player).ifPresent(playerClass -> {
            if (!(playerClass instanceof Rogue rogue)) {
                return;
            }

            if (rogue.isInvisible(player)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onRogueBackstab(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamager() instanceof Player attacker)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity damaged)) {
            return;
        }

        if (attacker.getUniqueId().equals(damaged.getUniqueId())) {
            return;
        }

        ItemStack hand = attacker.getInventory().getItemInMainHand();
        if (!hand.getType().equals(Material.GOLDEN_SWORD)) {
            return;
        }

        service.getCurrentClass(attacker).ifPresent(playerClass -> {
            if (!(playerClass instanceof Rogue rogue)) {
                return;
            }

            Location locA = attacker.getLocation().clone();
            Location locB = damaged.getLocation().clone();
            locA.setPitch(0F); locB.setPitch(0F);

            Vector attackerDirection = locA.getDirection();
            Vector attackedDirection = locB.getDirection();
            final double dot = attackerDirection.dot(attackedDirection);
            final UUID attackerUUID = attacker.getUniqueId();

            if (damaged.getHealth() <= 0.0 || damaged.isDead()) {
                return;
            }

            if (rogue.hasBackstabCooldown(attacker)) {
                long expire = rogue.getBackstabCooldown(attacker);
                String formatted = Time.convertToDecimal(expire - Time.now());
                attacker.sendMessage(Component.text("You can not Backstab for another", NamedTextColor.RED)
                        .appendSpace().append(Component.text(formatted, NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE))
                        .append(Component.text("s", NamedTextColor.RED).decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)));
                return;
            }

            if (dot >= 0.825 && dot <= 1.0) {
                RogueBackstabEvent backstabEvent = new RogueBackstabEvent(attacker, rogue, damaged);
                Bukkit.getPluginManager().callEvent(backstabEvent);

                if (backstabEvent.isCancelled()) {
                    return;
                }

                double toSubtract = rogue.getConfig().getBackstabDamage();
                double totalAbsorption = damaged.getAbsorptionAmount();

                if (totalAbsorption > 0) {
                    if (totalAbsorption >= toSubtract) {
                        damaged.setAbsorptionAmount((totalAbsorption - toSubtract));
                    } else {
                        damaged.setAbsorptionAmount(0);
                        toSubtract -= totalAbsorption;
                    }
                }

                // update health
                double newHealth = Math.max(damaged.getHealth() - toSubtract, 0.0);
                damaged.setHealth(newHealth);
                event.setDamage(0.0);

                // take item
                hand.subtract(1);

                // play effect
                Worlds.playSound(damaged.getLocation(), Sound.ENTITY_ITEM_BREAK);
                damaged.getWorld().spawnParticle(Particle.RAID_OMEN, locB.getX(), locB.getY() + 1.5, locB.getZ(), 8, 0.5, 0.5, 0.5, 1);

                // send messages
                Component victimNameComponent = Component.text(damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? "? ? ?" : damaged.getName())
                                .color(damaged.hasPotionEffect(PotionEffectType.INVISIBILITY) ? NamedTextColor.GRAY : NamedTextColor.GOLD);

                damaged.sendMessage(Component.text("You have been", NamedTextColor.RED)
                        .appendSpace().append(Component.text("BACKSTABBED!", NamedTextColor.DARK_RED).decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)));

                attacker.sendMessage(Component.text("You have backstabbed", NamedTextColor.YELLOW)
                        .appendSpace().append(victimNameComponent));

                // apply backstab cooldown
                rogue.setBackstabCooldown(attacker);
            }
        });
    }
}

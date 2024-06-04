package net.hcfrevival.classes.listener;

import gg.hcfactions.libs.bukkit.events.impl.PlayerLingeringSplashEvent;
import gg.hcfactions.libs.bukkit.events.impl.PlayerSplashPlayerEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.types.impl.Rogue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent event) {

    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {

    }

    @EventHandler
    public void onPlayerSplash(PlayerSplashPlayerEvent event) {

    }

    @EventHandler
    public void onLingeringSplash(PlayerLingeringSplashEvent event) {

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

    }

    @EventHandler
    public void onEntityTargetRogue(EntityTargetLivingEntityEvent event) {

    }
}

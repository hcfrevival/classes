package net.hcfrevival.classes.types.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.utils.Worlds;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.impl.RogueConfig;
import net.hcfrevival.classes.events.RogueInvisibilityQueryEvent;
import net.hcfrevival.classes.events.RogueUncloakEvent;
import net.hcfrevival.classes.events.RogueVanishPlayerUpdateEvent;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

@Getter
public class Rogue implements IClass {
    public final ClassService service;
    public final String name = "Miner";
    public final String description = "hey x2";
    public final Material icon = Material.GOLDEN_SWORD;
    public final Material helmet = Material.CHAINMAIL_HELMET;
    public final Material chestplate = Material.CHAINMAIL_CHESTPLATE;
    public final Material leggings = Material.CHAINMAIL_LEGGINGS;
    public final Material boots = Material.CHAINMAIL_BOOTS;
    public final boolean emptyArmorEnforced = true;
    public final Set<UUID> activePlayers;
    public final RogueConfig config;
    public final Map<UUID, Long> backstabCooldowns;
    public final Map<UUID, Long> cloakCooldowns;
    public final Map<UUID, Long> grappleCooldowns;
    public final Map<UUID, EInvisibilityState> invisibilityStates;

    public Rogue(ClassService service) {
        this.service = service;
        this.activePlayers = Sets.newConcurrentHashSet();
        this.config = new RogueConfig(this);
        this.backstabCooldowns = Maps.newConcurrentMap();
        this.cloakCooldowns = Maps.newConcurrentMap();
        this.grappleCooldowns = Maps.newConcurrentMap();
        this.invisibilityStates = Maps.newConcurrentMap();
    }

    public boolean isInvisible(Player player) {
        return invisibilityStates.containsKey(player.getUniqueId()) && !invisibilityStates.get(player.getUniqueId()).equals(EInvisibilityState.NONE);
    }

    public Map<UUID, EInvisibilityState> getInvisiblePlayers() {
        Map<UUID, EInvisibilityState> res = Maps.newHashMap();

        invisibilityStates.forEach((uuid, state) -> {
            if (!state.equals(EInvisibilityState.NONE)) {
                res.put(uuid, state);
            }
        });

        return res;
    }

    public EInvisibilityState getExpectedInvisibilityState(Player player) {
        Collection<Player> withinFullRadius = player.getWorld().getNearbyPlayers(player.getLocation(), config.getFullInvisibilityRadius());
        Collection<Player> withinPartialRadius = player.getWorld().getNearbyPlayers(player.getLocation(), config.getPartialInvisibilityRadius());

        RogueInvisibilityQueryEvent queryEvent = new RogueInvisibilityQueryEvent(player, this, withinFullRadius, withinPartialRadius);
        Bukkit.getPluginManager().callEvent(queryEvent);

        if (!queryEvent.getWithinPartialRadiusPlayers().isEmpty()) {
            return EInvisibilityState.NONE;
        }

        if (!queryEvent.getWithinFullRadiusPlayers().isEmpty()) {
            return EInvisibilityState.PARTIAL;
        }

        return EInvisibilityState.FULL;
    }

    public void updateInvisibilityState(Player player, EInvisibilityState expectedState) {
        EInvisibilityState currentState = invisibilityStates.get(player.getUniqueId());

        if (expectedState == currentState) {
            return;
        }

        invisibilityStates.put(player.getUniqueId(), expectedState);

        player.getWorld().spawnParticle(
                Particle.WITCH,
                player.getLocation().getX(),
                player.getLocation().getY() + 1.5,
                player.getLocation().getZ(),
                32,
                0.35, 0.35, 0.35,
                0.01
        );

        Worlds.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT);

        if (expectedState.equals(EInvisibilityState.FULL)) {
            // TODO: vanishPlayer(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
            return;
        }

        if (expectedState.equals(EInvisibilityState.PARTIAL)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
            return;
        }

        PotionEffect existingInvisEffect = player.getPotionEffect(PotionEffectType.INVISIBILITY);

        if (existingInvisEffect != null && existingInvisEffect.isInfinite()) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    public void vanishPlayer(Player player) {
        RogueVanishPlayerUpdateEvent vanishUpdateEvent = new RogueVanishPlayerUpdateEvent(player, this);
        Bukkit.getPluginManager().callEvent(vanishUpdateEvent);

        vanishUpdateEvent.getToHideFrom().forEach(uuid -> {
            Player toHideFromPlayer = Bukkit.getPlayer(uuid);

            if (toHideFromPlayer != null && toHideFromPlayer.isOnline()) {
                toHideFromPlayer.hidePlayer(service.getPlugin(), player);
            }
        });
    }

    public void unvanishPlayer(Player player, String reason) {
        Bukkit.getPluginManager().callEvent(new RogueUncloakEvent(player, reason));
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.showPlayer(service.getPlugin(), player));
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public enum EInvisibilityState {
        FULL, PARTIAL, NONE
    }
}

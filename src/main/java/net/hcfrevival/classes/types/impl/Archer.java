package net.hcfrevival.classes.types.impl;

import com.google.common.collect.Sets;
import gg.hcfactions.libs.bukkit.scheduler.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.hcfrevival.classes.config.impl.ArcherConfig;
import net.hcfrevival.classes.types.IClass;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

@Getter
public class Archer implements IClass {
    public final ClassService service;
    public final String name = "Archer";
    public final String description = "hey";
    public final Material icon = Material.BOW;
    public final Material helmet = Material.LEATHER_HELMET;
    public final Material chestplate = Material.LEATHER_CHESTPLATE;
    public final Material leggings = Material.LEATHER_LEGGINGS;
    public final Material boots = Material.LEATHER_BOOTS;
    public final boolean emptyArmorEnforced = true;
    public final Set<UUID> activePlayers;
    public final Set<Tag> archerTags;
    public final Set<UUID> markedEntities;
    public final ArcherConfig config;

    public Archer(ClassService service) {
        this.service = service;
        this.config = new ArcherConfig(this);
        this.activePlayers = Sets.newConcurrentHashSet();
        this.archerTags = Sets.newConcurrentHashSet();
        this.markedEntities = Sets.newConcurrentHashSet();
    }

    public int getTagCount(Player player, LivingEntity victim) {
        return (int)(archerTags.stream().filter(tag -> tag.getArcher().equals(player.getUniqueId()) && tag.getVictim().equals(victim.getUniqueId())).count());
    }

    public void tag(Player player, LivingEntity victim, int duration) {
        Tag tag = new Tag(player.getUniqueId(), victim.getUniqueId());
        archerTags.removeIf(existing -> existing.getArcher().equals(player.getUniqueId()) && !existing.getVictim().equals(victim.getUniqueId()));
        archerTags.add(tag);
        new Scheduler(service.getPlugin()).async(() -> archerTags.remove(tag)).delay(duration * 20L).run();
    }

    public void mark(LivingEntity victim) {
        if (markedEntities.contains(victim.getUniqueId())) {
            return;
        }

        UUID uuid = victim.getUniqueId();
        markedEntities.add(uuid);

        new Scheduler(service.getPlugin()).async(() -> markedEntities.remove(uuid)).delay((config.getMarkDuration() * 20L)).run();
    }

    @AllArgsConstructor
    public final class Tag {
        @Getter public UUID archer;
        @Getter public UUID victim;
    }
}

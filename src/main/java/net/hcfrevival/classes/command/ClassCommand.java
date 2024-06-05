package net.hcfrevival.classes.command;

import gg.hcfactions.libs.acf.BaseCommand;
import gg.hcfactions.libs.acf.annotation.CommandAlias;
import gg.hcfactions.libs.acf.annotation.CommandPermission;
import gg.hcfactions.libs.acf.annotation.Description;
import gg.hcfactions.libs.acf.annotation.Subcommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hcfrevival.classes.ClassService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

@Getter
@AllArgsConstructor
@CommandAlias("class|classes")
public class ClassCommand extends BaseCommand {
    public final ClassService service;

    @Subcommand("reload")
    @CommandPermission(ClassService.ADMIN_PERMISSION)
    @Description("Reload class configuration")
    public void onReloadClassService(CommandSender sender) {
        service.onReload();
        sender.sendMessage(Component.text("Classes have been reloaded", NamedTextColor.GREEN));
    }
}

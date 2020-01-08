package com.codeitforyou.portals.commands.sub;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.portals.CIFYPortals;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HelpCommand {
    @Command(aliases = {"help"}, about = "View list of commands.", permission = "portals.help", usage = "help")
    public static void execute(final CommandSender sender, final CIFYPortals plugin, final String[] args) {
        final List<Method> commandMethods = new ArrayList<Method>();
        for (final Method method : plugin.getCommandManager().getCommands().values()) {
            if (!commandMethods.contains(method)) {
                commandMethods.add(method);
            }
        }
        sender.sendMessage(" ");
        sender.sendMessage(StringUtil.translate("&b&lPortals &7(By CodeItForYou.com)"));
        sender.sendMessage(" ");
        for (final Method commandMethod : commandMethods) {
            final Command commandAnnotation = commandMethod.getAnnotation(Command.class);
            if (!sender.hasPermission(commandAnnotation.permission())) {
                continue;
            }
            sender.sendMessage(StringUtil.translate(" &b/portals " + commandAnnotation.usage() + " &8- &7" + commandAnnotation.about()));
        }
        sender.sendMessage(" ");
        sender.sendMessage(StringUtil.translate("&7A total of &b" + commandMethods.size() + " &7command(s)."));
        sender.sendMessage(" ");
    }
}

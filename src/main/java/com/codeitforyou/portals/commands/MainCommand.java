package com.codeitforyou.portals.commands;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.lib.api.general.StringUtil;
import com.codeitforyou.portals.CIFYPortals;
import org.bukkit.command.CommandSender;

public class MainCommand {
    @Command(about = "The main command.")
    public static void execute(final CommandSender sender, final CIFYPortals plugin, final String[] args) {
        sender.sendMessage(StringUtil.translate("&b[&lPortals&b] &7Use &f/portals help &7for commands."));
    }
}

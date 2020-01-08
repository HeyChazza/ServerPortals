package com.codeitforyou.portals.commands.sub;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.config.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand {
    @Command(aliases = {"reload"}, about = "Reload the configuration files.", permission = "portals.reload", usage = "reload")
    public static void execute(final CommandSender sender, final CIFYPortals plugin, final String[] args) {
        plugin.loadConfigFiles();
        plugin.loadPortalsData();
        Lang.RELOADED.send(sender);
    }
}

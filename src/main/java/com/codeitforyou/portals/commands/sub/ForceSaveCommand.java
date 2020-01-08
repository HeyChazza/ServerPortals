package com.codeitforyou.portals.commands.sub;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.config.Lang;
import org.bukkit.command.CommandSender;

public class ForceSaveCommand {
    @Command(aliases = {"forcesave"}, about = "Force-save portals.", permission = "portals.forcesave", usage = "forcesave")
    public static void execute(final CommandSender sender, final CIFYPortals plugin, final String[] args) {
        plugin.savePortalsData();
        Lang.FORCE_SAVED.send(sender);
    }
}

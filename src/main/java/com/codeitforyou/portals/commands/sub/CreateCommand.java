package com.codeitforyou.portals.commands.sub;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.api.Portal;
import com.codeitforyou.portals.config.Lang;
import com.codeitforyou.portals.util.Common;
import com.codeitforyou.portals.util.LocationUtil;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateCommand {
    @Command(aliases = {"create"}, about = "Create a portal.", permission = "portals.create", usage = "create <id> <filter,list>", requiredArgs = 1)
    public static void execute(final Player player, final CIFYPortals plugin, final String[] args) {
        final String portalId = args[0];

        if (plugin.getPortalManager().getPortal(portalId) != null) {
            Lang.ERROR_ALREADY_EXISTS.send(player);
            return;
        }

        if (Common.select(player, ((args.length >= 2) ? args[1] : null))) {
            List<String> selection = Common.selections.get(player.getName());

            Portal portal = new Portal(portalId);
            for (String block : selection) {
                portal.addLocation(LocationUtil.fromString(block));
            }
            plugin.getPortalManager().addPortal(portal);
            Lang.PORTAL_CREATED.send(player, portalId);
            Common.selections.remove(player.getName());
            return;
        }

        Lang.MUST_CREATE_SELECTION.send(player);
    }
}

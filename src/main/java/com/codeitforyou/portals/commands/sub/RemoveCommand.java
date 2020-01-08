package com.codeitforyou.portals.commands.sub;

import com.codeitforyou.lib.api.command.Command;
import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.api.Portal;
import com.codeitforyou.portals.config.Lang;
import com.codeitforyou.portals.util.Common;
import com.codeitforyou.portals.util.LocationUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RemoveCommand {
    @Command(aliases = {"remove"}, about = "Remove a portal in a selection.", permission = "portals.remove", usage = "remove")
    public static void execute(final Player player, final CIFYPortals plugin, final String[] args) {
        if (Common.select(player, null)) {
            int count = 0;

            List<Portal> portalsPendingRemoval = new ArrayList<>();
            for (String block : Common.selections.get(player.getName())) {
                for (final Portal portal : plugin.getPortalManager().getPortals()) {
                    if (portal.getLocations().contains(LocationUtil.fromString(block))) {
                        portalsPendingRemoval.add(portal);
                        count++;
                    }
                }
            }

            for (final Portal portalToRemove : portalsPendingRemoval) {
                plugin.getPortalsFile().set("portals." + portalToRemove.getId(), null);
                plugin.getPortalManager().getPortals().remove(portalToRemove);
            }

            plugin.savePortalsFile();

            Lang.PORTALS_REMOVED.send(player, count);
            Common.selections.remove(player.getName());
        } else {
            Lang.NO_PORTALS_IN_REGION.send(player);
        }
    }
}

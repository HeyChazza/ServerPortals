package com.codeitforyou.portals.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Common {
    private static final CIFYPortals plugin = (CIFYPortals) JavaPlugin.getProvidingPlugin(CIFYPortals.class);
    public static Map<String, List<String>> selections = new HashMap<>();

    public static List<Location> getLocationsFromCuboid(CuboidRegion cuboid) {
        List<Location> locations = new ArrayList<>();
        BlockVector3 minLocation = cuboid.getMinimumPoint();
        BlockVector3 maxLocation = cuboid.getMaximumPoint();
        for (int i1 = minLocation.getBlockX(); i1 <= maxLocation.getBlockX(); i1++) {
            for (int i2 = minLocation.getBlockY(); i2 <= maxLocation.getBlockY(); i2++) {
                for (int i3 = minLocation.getBlockZ(); i3 <= maxLocation.getBlockZ(); i3++) {
                    locations.add(new Location(Bukkit.getWorld(cuboid.getWorld().getName()), i1, i2, i3));
                }
            }
        }
        return locations;
    }

    public static boolean select(CommandSender sender, String args) {
        Player player = (Player) sender;
        String playerName = player.getName();
        LocalSession session = plugin.worldEdit.getSession(player);

        if (session == null) return false;

        Region selection = null;
        try {
            if (session.getSelectionWorld() == null) return false;
            selection = session.getSelection(session.getSelectionWorld());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
        if (selection != null) {
            if (selection instanceof CuboidRegion) {
                List<Location> locations = getLocationsFromCuboid((CuboidRegion) selection);
                List<String> blocks = new ArrayList<>();
                String[] ids = {};
                int count = 0;
                int filtered = 0;
                boolean filter = false;
                if (args != null) {
                    ids = args.split(",");
                    filter = true;
                }
                for (Location location : locations) {
                    Block block = player.getWorld().getBlockAt(location);
                    if (filter) {
                        boolean found = false;
                        for (final String id : ids) {
                            String[] parts = id.split(":");
                            if (parts.length == 2) {
                                if (parts[0].equals(String.valueOf(block.getType())) && parts[1].equals(String.valueOf(block.getData()))) {
                                    found = true;
                                    break;
                                }
                            } else {
                                if (parts[0].equals(String.valueOf(block.getType()))) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) {
                            blocks.add(LocationUtil.toString(block.getLocation()));
                            count++;
                        } else {
                            filtered++;
                        }
                    } else {
                        blocks.add(LocationUtil.toString(block.getLocation()));
                        count++;
                    }
                }
                selections.put(playerName, blocks);
                return true;
            } else {
                Lang.MUST_BE_CUBOID_SELECTION.send(sender);
            }
        } else {
            Lang.MUST_CREATE_SELECTION.send(sender);
        }
        return false;
    }
}

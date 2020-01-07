package net.yofuzzy3.portals.commands;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.yofuzzy3.portals.Portals;
import net.yofuzzy3.portals.api.Portal;
import net.yofuzzy3.portals.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandPortals implements CommandExecutor {

    private Portals plugin;
    public static Map<String, List<String>> selections = new HashMap<>();

    public CommandPortals(Portals plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (commandLabel.equalsIgnoreCase("portals")) {
            if (sender.hasPermission("portals.command.portals")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String playerName = sender.getName();
                    if (args.length >= 1) {
                        switch (args[0].toLowerCase()) {
                            case "reload":
                                plugin.loadConfigFiles();
                                plugin.loadPortalsData();
                                sender.sendMessage(ChatColor.GREEN + "All configuration files and data have been reloaded.");
                                break;
                            case "forcesave":
                                plugin.savePortalsData();
                                sender.sendMessage(ChatColor.GREEN + "Portal data saved!");
                                break;
                            case "create":
                                final String portalId = args[1];
                                if (select(player, ((args.length >= 3) ? args[2] : null))) {
                                    List<String> selection = selections.get(playerName);

                                    Portal portal = new Portal(portalId);
                                    for (String block : selection) {
                                        System.out.println("block = " + block);
                                        Location loc = LocationUtil.fromString(block);
                                        portal.addLocation(loc);
//                                        plugin.portalData.put(block, portalId);
                                    }
                                    plugin.getPortalManager().addPortal(portal);
                                    player.sendMessage(ChatColor.GREEN + String.valueOf(selection.size()) + " portals have been created.");
                                    selections.remove(playerName);
                                }
                                break;
                            case "remove":
                                if (select(player, null)) {
                                    int count = 0;
                                    for (String block : selections.get(playerName)) {
                                        if (plugin.portalData.containsKey(block)) {
                                            plugin.portalData.remove(block);
                                            count++;
                                        }
                                    }
                                    sender.sendMessage(ChatColor.GREEN + String.valueOf(count) + " portals have been removed.");
                                    selections.remove(playerName);
                                }
                                break;
                            default:
                                help(sender);
                        }
                    } else help(sender);
                } else sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            } else sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        return false;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + "Portals v" + plugin.getDescription().getVersion() + " by CodeItForYou");
        sender.sendMessage(ChatColor.GREEN + "/portals reload " + ChatColor.RED + "Reload all files and data.");
        sender.sendMessage(ChatColor.GREEN + "/portals forcesave " + ChatColor.RED + "Force-save portals.");
        sender.sendMessage(ChatColor.GREEN + "/portals create <destination> <filter,list> " + ChatColor.RED + "Create portals.");
        sender.sendMessage(ChatColor.GREEN + "/portals remove " + ChatColor.RED + "Remove portals.");
        sender.sendMessage(ChatColor.BLUE + "Visit www.spigotmc.org/resources/bungeeportals.19 for help.");
    }

    private List<Location> getLocationsFromCuboid(CuboidRegion cuboid) {
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

    private boolean select(CommandSender sender, String args) {
        Player player = (Player) sender;
        String playerName = player.getName();
        LocalSession session = plugin.worldEdit.getSession(player);
        Region selection = null;
        try {
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
                sender.sendMessage(ChatColor.GREEN + String.valueOf(count) + " blocks have been selected, " + filtered + " filtered.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Must be a cuboid selection!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "You have to first create a WorldEdit selection!");
        }
        return false;
    }
}

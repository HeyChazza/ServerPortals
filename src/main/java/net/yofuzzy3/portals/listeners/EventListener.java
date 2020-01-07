package net.yofuzzy3.portals.listeners;

import net.yofuzzy3.portals.Portals;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventListener implements Listener {

    private Portals plugin;
    private Map<String, Boolean> statusData = new HashMap<>();
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();

    public EventListener(Portals plugin) {
        this.plugin = plugin;
    }

    private boolean CheckCooldown(Player player) {
        final int cooldelay = plugin.getConfig().getInt("CooldownSeconds");
        int diff = (int) ((System.currentTimeMillis() - cooldown.get(player)) / 1000);
        if (diff < cooldelay) {
            player.sendMessage(ChatColor.RED + "Please wait " + ChatColor.YELLOW + (cooldelay - diff) + ChatColor.RED + " seconds until attempting to teleport again.");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        cooldown.put(event.getPlayer(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Cleanup to prevent a memory leak
        Player player = event.getPlayer();
        String playerName = player.getName();
        cooldown.remove(player);
        statusData.remove(playerName);
        net.yofuzzy3.portals.commands.CommandPortals.selections.remove(playerName);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!statusData.containsKey(playerName)) {
            statusData.put(playerName, false);
        }
        Block block = player.getWorld().getBlockAt(player.getLocation());
        String data = block.getWorld().getName() + "#" + block.getX() + "#" + block.getY() + "#" + block.getZ();
        if (plugin.portalData.containsKey(data)) {
            if (!statusData.get(playerName)) {
                statusData.put(playerName, true);
                if (CheckCooldown(player)) return;
                String destination = plugin.portalData.get(data);
                if (player.hasPermission("portals.portal." + destination) || player.hasPermission("portals.portal.*")) {
                    // Do action stuff here

                    cooldown.put(player, System.currentTimeMillis());
                } else {
                    player.sendMessage(plugin.configFile.getString("NoPortalPermissionMessage").replace("{destination}", destination).replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
                }
            }
        } else {
            if (statusData.get(playerName)) {
                statusData.put(playerName, false);
            }
        }
    }
}

package com.codeitforyou.portals.listeners;

import com.codeitforyou.portals.CIFYPortals;
import com.codeitforyou.portals.api.Portal;
import com.codeitforyou.portals.config.Lang;
import com.codeitforyou.portals.util.Common;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class EventListener implements Listener {

    private CIFYPortals plugin;
    private Map<String, Boolean> statusData = new HashMap<>();
    private HashMap<Player, Long> cooldown = new HashMap<Player, Long>();

    public EventListener(CIFYPortals plugin) {
        this.plugin = plugin;
    }

    private boolean CheckCooldown(Player player) {
        final int cooldelay = plugin.getConfig().getInt("CooldownSeconds");
        int diff = (int) ((System.currentTimeMillis() - cooldown.get(player)) / 1000);
        if (diff < cooldelay) {
            final int cooldownTime = cooldelay - diff;
            String cooldownMsg = Lang.COOLDOWN.asString(cooldownTime);

            if (cooldownTime == 1) cooldownMsg = cooldownMsg.replace("seconds", "second");
            player.sendMessage(cooldownMsg);
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
        Common.selections.remove(playerName);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (!statusData.containsKey(playerName)) {
            statusData.put(playerName, false);
        }
        Block block = player.getWorld().getBlockAt(player.getLocation());
        Portal portal = plugin.getPortalManager().getPortal(block.getLocation());

        if (portal != null) {
            if (!statusData.get(playerName)) {
                statusData.put(playerName, true);
                if (CheckCooldown(player)) return;
                if (player.hasPermission("portals.portal." + portal.getId()) || player.hasPermission("portals.portal.*")) {
                    plugin.getActionManager().runActions(player, portal.getActions());
                    cooldown.put(player, System.currentTimeMillis());
                } else {
                    Lang.NO_PERMISSION_PORTAL.send(player, portal.getId());
                }
            }
        } else if (statusData.get(playerName)) statusData.put(playerName, false);
    }
}

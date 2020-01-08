package com.codeitforyou.portals.tasks;

import com.codeitforyou.portals.CIFYPortals;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {

    private CIFYPortals plugin;

    public SaveTask(CIFYPortals plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (plugin.configFile.getBoolean("SaveTask.Enabled")) {
            plugin.savePortalsData();
        }
    }

}

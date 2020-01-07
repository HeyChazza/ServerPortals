package net.yofuzzy3.portals.tasks;

import net.yofuzzy3.portals.Portals;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveTask extends BukkitRunnable {

    private Portals plugin;

    public SaveTask(Portals plugin) {
        this.plugin = plugin;
    }

    public void run() {
        if (plugin.configFile.getBoolean("SaveTask.Enabled")) {
            plugin.savePortalsData();
        }
    }

}

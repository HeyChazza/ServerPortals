package net.yofuzzy3.portals;

import com.codeitforyou.lib.api.actions.ActionManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.yofuzzy3.portals.api.Portal;
import net.yofuzzy3.portals.api.PortalManager;
import net.yofuzzy3.portals.listeners.EventListener;
import net.yofuzzy3.portals.tasks.SaveTask;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Portals extends JavaPlugin {
    private final ActionManager ACTION_MANAGER = new ActionManager(this);
    private final PortalManager PORTAL_MANAGER = new PortalManager();

    private Logger logger = Bukkit.getLogger();
    public Map<String, String> portalData = new HashMap<>();
    public WorldEditPlugin worldEdit;
    public YamlConfiguration configFile;
    private YamlConfiguration portalsFile;

    public ActionManager getActionManager() {
        return ACTION_MANAGER;
    }

    public PortalManager getPortalManager() {
        return PORTAL_MANAGER;
    }


    public void onEnable() {
        long time = System.currentTimeMillis();
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            getPluginLoader().disablePlugin(this);
            throw new NullPointerException("[Portals] WorldEdit not found, disabling...");
        }
        ACTION_MANAGER.addDefaults();

        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        startMetrics();
        getCommand("portals").setExecutor(new net.yofuzzy3.portals.commands.CommandPortals(this));
        logger.log(Level.INFO, "[Portals] Commands registered!");

        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        logger.log(Level.INFO, "[Portals] Events registered!");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        logger.log(Level.INFO, "[Portals] Plugin channel registered!");

        loadConfigFiles();
        loadPortalsData();

        int interval = configFile.getInt("SaveTask.Interval") * 20;
        new SaveTask(this).runTaskTimer(this, interval, interval);
        logger.log(Level.INFO, "[Portals] Save task started!");

        logger.log(Level.INFO, "[Portals] Version " + getDescription().getVersion() + " has been enabled. (" + (System.currentTimeMillis() - time) + "ms)");
    }

    public void onDisable() {
        long time = System.currentTimeMillis();
        savePortalsData();
        logger.log(Level.INFO, "[Portals] Version " + getDescription().getVersion() + " has been disabled. (" + (System.currentTimeMillis() - time) + "ms)");
    }

    private void startMetrics() {
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
            logger.log(Level.INFO, "[Portals] Metrics initiated!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfigFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfigFiles() {
        File cFile = new File(getDataFolder(), "config.yml");
        if (!cFile.exists()) {
            cFile.getParentFile().mkdirs();
            createConfigFile(getResource("config.yml"), cFile);
            logger.log(Level.INFO, "[Portals] Configuration file config.yml created!");
        }
        configFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        logger.log(Level.INFO, "[Portals] Configuration file config.yml loaded!");
        File pFile = new File(getDataFolder(), "portals.yml");
        if (!pFile.exists()) {
            pFile.getParentFile().mkdirs();
            createConfigFile(getResource("portals.yml"), pFile);
            logger.log(Level.INFO, "[Portals] Configuration file portals.yml created!");
        }
        portalsFile = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "portals.yml"));
        logger.log(Level.INFO, "[Portals] Configuration file portals.yml loaded!");
    }

    public void loadPortalsData() {
        try {
            long time = System.currentTimeMillis();

            final ConfigurationSection portalsSection = portalsFile.getConfigurationSection("portals");

            for (final String portalId : portalsSection.getKeys(false)) {
                final ConfigurationSection portalSection = portalsSection.getConfigurationSection(portalId);

                Portal portal = new Portal(portalId);
            }

            for (String key : portalsFile.getKeys(false)) {
                String value = portalsFile.getString(key);
                portalData.put(key, value);
            }
            logger.log(Level.INFO, "[Portals] Portal data loaded! (" + (System.currentTimeMillis() - time) + "ms)");
        } catch (NullPointerException e) {

        }
    }

    public void savePortalsData() {
        long time = System.currentTimeMillis();
        for (Entry<String, String> entry : portalData.entrySet()) {
            portalsFile.set(entry.getKey(), entry.getValue());
        }
        try {
            portalsFile.save(new File(getDataFolder(), "portals.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "[Portals] Portal data saved! (" + (System.currentTimeMillis() - time) + "ms)");
    }

}

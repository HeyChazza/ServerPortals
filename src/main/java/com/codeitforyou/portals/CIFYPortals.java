package com.codeitforyou.portals;

import com.codeitforyou.lib.api.actions.ActionManager;
import com.codeitforyou.lib.api.command.CommandManager;
import com.codeitforyou.portals.api.Portal;
import com.codeitforyou.portals.api.PortalManager;
import com.codeitforyou.portals.commands.MainCommand;
import com.codeitforyou.portals.commands.sub.*;
import com.codeitforyou.portals.config.Lang;
import com.codeitforyou.portals.listeners.EventListener;
import com.codeitforyou.portals.tasks.SaveTask;
import com.codeitforyou.portals.util.LocationUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CIFYPortals extends JavaPlugin {
    private final ActionManager ACTION_MANAGER = new ActionManager(this);
    private final PortalManager PORTAL_MANAGER = new PortalManager();

    private CommandManager COMMAND_MANAGER;

    private Logger logger = Bukkit.getLogger();
    public WorldEditPlugin worldEdit;
    public YamlConfiguration configFile;
    private YamlConfiguration portalsFile;

    public ActionManager getActionManager() {
        return ACTION_MANAGER;
    }

    public PortalManager getPortalManager() {
        return PORTAL_MANAGER;
    }

    public YamlConfiguration getPortalsFile() {
        return portalsFile;
    }

    public CommandManager getCommandManager() {
        return COMMAND_MANAGER;
    }

    public void onEnable() {
        long time = System.currentTimeMillis();
        if (getServer().getPluginManager().getPlugin("WorldEdit") == null) {
            getPluginLoader().disablePlugin(this);
            throw new NullPointerException("[Portals] WorldEdit not found, disabling...");
        }

        ACTION_MANAGER.addDefaults();

        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");

        Lang.init(this);
        logger.log(Level.INFO, "[Portals] Lang registered!");

        COMMAND_MANAGER = new CommandManager(Arrays.asList(CreateCommand.class, ForceSaveCommand.class, HelpCommand.class, ReloadCommand.class, RemoveCommand.class), getDescription().getName().toLowerCase(), this);
        COMMAND_MANAGER.setMainCommand(MainCommand.class);
        CommandManager.Locale locale = CommandManager.getLocale();
        locale.setNoPermission(Lang.NO_PERMISSION.asString());
        locale.setPlayerOnly(Lang.PLAYER_ONLY.asString());
        locale.setUnknownCommand(Lang.ERROR_INVALID_COMMAND.asString());
        locale.setUsage(Lang.COMMAND_USAGE.asString("{usage}"));

//        getCommand("portals").setExecutor(new net.yofuzzy3.portals.commands.CommandPortals(this));
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

            System.out.println("Loading data..");

            final ConfigurationSection portalsSection = portalsFile.getConfigurationSection("portals");

            for (final String portalId : portalsSection.getKeys(false)) {
                final ConfigurationSection portalSection = portalsSection.getConfigurationSection(portalId);

                System.out.println("Loading portal " + portalId + "!");

                Portal portal = getPortalManager().getPortal(portalId);
                if (portal == null) portal = new Portal(portalId);
                portal.setActions(portalSection.getStringList("actions"));
                portal.setLocations(portalSection.getStringList(("locations")).stream().map(LocationUtil::fromString).collect(Collectors.toList()));
                PORTAL_MANAGER.addPortal(portal);
            }

            logger.log(Level.INFO, "[Portals] Portal data loaded! (" + (System.currentTimeMillis() - time) + "ms)");
        } catch (NullPointerException e) {

        }
    }

    public void savePortalsFile() {
        try {
            portalsFile.save(new File(getDataFolder(), "portals.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePortalsData() {
        long time = System.currentTimeMillis();

        portalsFile.set("portals", null);

        for (final Portal portal : getPortalManager().getPortals()) {
            List<String> locs = portal.getLocations().stream().map(LocationUtil::toString).collect(Collectors.toList());
            portalsFile.set("portals." + portal.getId() + ".actions", portal.getActions());
            portalsFile.set("portals." + portal.getId() + ".locations", locs);
        }

        savePortalsFile();
        logger.log(Level.INFO, "[Portals] Portal data saved! (" + (System.currentTimeMillis() - time) + "ms)");
    }

}

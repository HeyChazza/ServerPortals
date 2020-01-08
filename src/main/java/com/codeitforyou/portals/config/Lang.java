package com.codeitforyou.portals.config;

import com.codeitforyou.portals.CIFYPortals;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public enum Lang {
    PORTAL_CREATED("&7The &b{0} &7portal has been created!"),
    PORTALS_REMOVED("&7You've removed &b{0} &7portal(s)!"),

    RELOADED("&7All config and portal data has been reloaded!"),
    FORCE_SAVED("&7You've force saved the config data!"),
    NO_PORTALS_IN_REGION("&7There are no portals in that region!"),

    NO_PERMISSION("&cYou don't have permission to use that command!"),
    NO_PERMISSION_PORTAL("&7You do not have permission to use the &c{0} &7portal!"),

    PLAYER_ONLY("&7Only players can use this command!"),
    MUST_BE_CUBOID_SELECTION("&cMust be a cuboid selection!"),
    MUST_CREATE_SELECTION("&cYou must first create a selection!"),

    COOLDOWN("&7Please wait &c{0} seconds &7until attempting to teleport again."),

    ERROR_INVALID_COMMAND("&7That's an invalid command, use &f/portals help&7."),
    ERROR_ALREADY_EXISTS("&7A portal by that id already exists!"),

    COMMAND_USAGE("&7Usage: &b{0}");

    ;

    private String message;
    private static FileConfiguration c;

    Lang(final String... def) {
        this.message = String.join("\n", def);
    }

    private String getMessage() {
        return this.message;
    }

    public String getPath() {
        return "message." + this.name().toLowerCase().toLowerCase();
    }

    public static String format(String s, final Object... objects) {
        for (int i = 0; i < objects.length; ++i) {
            s = s.replace("{" + i + "}", String.valueOf(objects[i]));
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static boolean init(CIFYPortals CIFYPortals) {
        Lang.c = CIFYPortals.getConfig();
        for (final Lang value : values()) {
            if (value.getMessage().split("\n").length == 1) {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage());
            } else {
                Lang.c.addDefault(value.getPath().toLowerCase(), value.getMessage().split("\n"));
            }
        }
        Lang.c.options().copyDefaults(true);
        CIFYPortals.saveConfig();
        return true;
    }

    public void send(final Player player, final Object... args) {
        final String message = this.asString(args);
        Arrays.stream(message.split("\n")).forEach(player::sendMessage);
    }

    public void send(final CommandSender sender, final Object... args) {
        if (sender instanceof Player) {
            this.send((Player) sender, args);
        } else {
            Arrays.stream(this.asString(args).split("\n")).forEach(sender::sendMessage);
        }
    }

    public String asString(final Object... objects) {
        Optional<String> opt = Optional.empty();
        if (Lang.c.contains(this.getPath())) {
            if (Lang.c.isList(getPath())) {
                opt = Optional.of(String.join("\n", Lang.c.getStringList(this.getPath())));
            } else if (Lang.c.isString(this.getPath())) {
                opt = Optional.ofNullable(Lang.c.getString(this.getPath()));
            }
        }
        return this.format(opt.orElse(this.message), objects);
    }
}


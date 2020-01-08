package com.codeitforyou.portals.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static String toString(final Location location) {
        return location.getWorld().getName() + "#" + location.getX() + "#" + location.getY() + "#" + location.getZ();
    }

    public static Location fromString(String location) {
        String[] parts = location.split("#");
        return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}

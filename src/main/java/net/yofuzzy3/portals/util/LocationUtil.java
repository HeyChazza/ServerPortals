package net.yofuzzy3.portals.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
    public static String toString(final Location location) {
        return location.getWorld().getName() + "#" + location.getX() + "#" + location.getY() + "#" + location.getZ();
    }

    public static Location fromString(String location) {
        String[] parts = location.split("#");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }
}

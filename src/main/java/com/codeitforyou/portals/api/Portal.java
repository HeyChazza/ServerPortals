package com.codeitforyou.portals.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class Portal {

    private String id;
    private List<Location> locations;
    private List<String> actions;

    public Portal(final String id) {
        this.id = id;
        this.locations = new ArrayList<>();
        this.actions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void addLocation(final String world, final int x, final int y, final int z) {
        locations.add(new Location(Bukkit.getWorld(world), x, y, z));
    }

    public void addLocation(final Location location) {
        locations.add(location);
    }

    public void setActions(final List<String> actions) {
        this.actions = actions;
    }

    public void setLocations(final List<Location> locations) {
        this.locations = locations;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public List<String> getActions() {
        return actions;
    }
}

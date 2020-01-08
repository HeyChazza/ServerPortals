package com.codeitforyou.portals.api;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class PortalManager {
    private final List<Portal> portals = new ArrayList<>();

    public PortalManager() {
    }

    public void addPortal(final Portal portal) {
        portals.add(portal);
    }

    public void removePortal(final Portal portal) {
        portals.remove(portal);
    }

    public Portal getPortal(final Location location) {
        return portals.stream().filter(portal -> portal.getLocations().contains(location)).findFirst().orElse(null);
    }

    public Portal getPortal(final String id) {
        return portals.stream().filter(portal -> portal.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public List<Portal> getPortals() {
        return portals;
    }
}

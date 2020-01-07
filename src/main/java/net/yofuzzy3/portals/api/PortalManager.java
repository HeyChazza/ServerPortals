package net.yofuzzy3.portals.api;

import java.util.ArrayList;
import java.util.List;

public class PortalManager {
    private final List<Portal> portals = new ArrayList<>();

    public PortalManager() {
    }

    public void addPortal(Portal portal) {
        portals.add(portal);
    }

    public List<Portal> getPortals() {
        return portals;
    }
}

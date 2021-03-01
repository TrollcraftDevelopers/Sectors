package pl.trollcraft.sectors.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of sectors.
 * A group represents one "server"
 * Locations can be repeated.
 *
 * @author Jakub Zelmanowicz
 */
public class SectorsGroup {

    private final String name;
    private final List<Sector> sectors;

    public SectorsGroup(String name) {
        this.name = name;
        sectors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Sector> getSectors() {
        return sectors;
    }
}

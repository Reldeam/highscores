package online.meinkraft.highscores.block;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import online.meinkraft.highscores.table.Entry;

public abstract class HighScoreBlock implements ConfigurationSerializable {

    protected final Location location;
    protected final Integer rank;

    public HighScoreBlock(Location location, Integer rank) {
        this.location = location;
        this.rank = rank;
    }

    public HighScoreBlock(Map<String, Object> map) {
        location = (Location) map.get("location");
        rank = (Integer) map.get("rank");
    }

    public Location getLocation() {
        return location;
    }

    public Integer getRank() {
        return rank;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("location", location.serialize());
        map.put("rank", rank);
        return map;
    }

    public abstract boolean update(Entry entry);
    
}

package online.meinkraft.highscores.table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class Entry implements ConfigurationSerializable {

    private final UUID playerUUID;
    private Double score;

    public Entry(UUID playerUUID, Double score) {
        this.playerUUID = playerUUID;
        this.score = score;
    }

    public Entry(Map<String, Object> map) {
        playerUUID = UUID.fromString((String) map.get("playerUUID"));
        score = (Double) map.get("score");
    }

    @Override
    public String toString() {
        return getPlayerUUID().toString() + " (" + score + ")";
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUUID", playerUUID.toString());
        map.put("score", score);
        return map;
    }

    public static Entry deserialize(Map<String, Object> map) {
        return new Entry(map);
    }

}

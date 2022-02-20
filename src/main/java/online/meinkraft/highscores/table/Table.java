package online.meinkraft.highscores.table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import me.clip.placeholderapi.PlaceholderAPI;
import online.meinkraft.highscores.block.HighScoreSkull;
import online.meinkraft.highscores.exception.PlayerNotFoundException;
import online.meinkraft.highscores.exception.TableNotFoundException;

public class Table implements ConfigurationSerializable {

    private final String name;
    private String placeholder;

    Map<UUID, Entry> entries = new HashMap<>();
    TreeSet<Entry> ranks = new TreeSet<>(new EntryComparator());

    //TODO signs

    // UUID of the sign and the rank that the sign should display
    List<HighScoreSkull> skulls = new ArrayList<>();

    public Table(String tableName, String placeholder) {
        name = tableName;
        this.placeholder = placeholder;
    }

    public void setSkull(Location location, Integer rank) throws IndexOutOfBoundsException {
        HighScoreSkull block = new HighScoreSkull(location, rank);
        Entry entry = getEntry(rank);
        if(block.update(entry)) {
            skulls.add(block);
        }
    }

    @Override
    public String toString() {
        return ranks.parallelStream().collect(
            StringBuilder::new, 
            (x, y) -> x.append(y), 
            (a, b) -> a.append(",").append(b)
        ).toString(); 
    }

    public static Table getTable(File tableFolder, String tableName) throws 
    IOException,
    ClassNotFoundException, 
    TableNotFoundException 
    {
        File file = new File(tableFolder, tableName + ".table");
        if(file.exists()) {
            FileInputStream stream = new FileInputStream(file);
            BukkitObjectInputStream bukkitStream = new BukkitObjectInputStream(stream);

            Object data = (Object) bukkitStream.readObject();

            bukkitStream.close();

            if(data instanceof Table) {
                return (Table) data;
            }
        }
        throw new TableNotFoundException();
    }

    public static Set<String> listTables(File tableFolder) {

        Set<String> tableNames = new HashSet<>();

        if(!tableFolder.exists()) tableFolder.mkdir();

        for(String file : tableFolder.list()) {
            if(file.matches("(.*)\\.table$")) {
                tableNames.add(file.replaceAll("\\.table$", ""));
            }
        }
        return tableNames;

    }

    public void save(File tableDirectory) throws IOException {
        if(!tableDirectory.exists()) tableDirectory.mkdir();
        File file = new File(tableDirectory, getName() + ".table");
        if(!file.exists()) file.createNewFile();
        FileOutputStream stream = new FileOutputStream(file);
        BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(stream);
        bukkitStream.writeObject(this);
        bukkitStream.close();
    }

    public void delete(File tableDirectory) throws IOException {
        if(!tableDirectory.exists()) tableDirectory.mkdir();
        File file = new File(tableDirectory, getName() + ".table");
        if(file.exists()) file.delete();
    }

    public String getName() {
        return name;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setScore(Player player, Double score) {
        Entry entry;
        try {
            entry = getEntry(player);
        } 
        catch (PlayerNotFoundException e) {
            entry = new Entry(player.getUniqueId(), score);
        }
        entries.put(player.getUniqueId(), entry);
        ranks.remove(entry);
        ranks.add(entry);
    }

    public void updatePlayer(Player player) throws NumberFormatException {
        String parsedPlaceholder = PlaceholderAPI.setPlaceholders(
            player, 
            getPlaceholder()
        );
        Double score = Double.parseDouble(parsedPlaceholder);
        setScore(player, score);

        // update the skulls and remove any invalid ones
        skulls.removeIf(skull -> !skull.update(getEntry(skull.getRank())));
    }

    public Entry getEntry(Integer rank) throws IndexOutOfBoundsException {
        Iterator<Entry> iterator = ranks.iterator();
        int index = 0;
        while(iterator.hasNext() && index < rank) {
            iterator.next();
            index++;
        }
        if(iterator.hasNext()) {
            return iterator.next();
        }
        else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Entry getEntry(Player player) throws PlayerNotFoundException {
        Entry entry = entries.get(player.getUniqueId());
        if(entry != null) {
            return entry;
        }
        else {
            throw new PlayerNotFoundException(player.getUniqueId());
        }
    }

    public Double getScore(Player player) throws PlayerNotFoundException {
        Entry entry = entries.get(player.getUniqueId());
        if(entry != null) {
            return entry.getScore();
        }
        else {
            throw new PlayerNotFoundException(player.getUniqueId());
        }
    }

    public Integer getRank(Player player) throws PlayerNotFoundException {
        Entry entry = entries.get(player.getUniqueId());
        if(entry != null) {
            return ranks.headSet(entry).size();
        }
        else {
            throw new PlayerNotFoundException(player.getUniqueId());
        }
        
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("placeholder", placeholder);
        List<Map<String, Object>> entries = new ArrayList<>();
        this.ranks.stream().forEach(entry -> entries.add(entry.serialize()));
        map.put("entries", entries.toArray());

        if(skulls.size() > 0) {
            map.put("skulls", skulls.toArray());
        }
        
        return map;

    }

    @SuppressWarnings("unchecked") 
    public Table(Map<String, Object> map) {

        name = (String) map.get("name");
        placeholder = (String) map.get("placeholder");

        Object[] serialisedEntries = (Object[]) map.get("entries");
        for(Object serialisedEntry : serialisedEntries) {
            if(serialisedEntry instanceof Map<?,?>) {
                Entry entry = new Entry((Map<String, Object>) serialisedEntry);
                this.entries.put(entry.getPlayerUUID(), entry);
                this.ranks.add(entry);
            }
        }

        skulls = new ArrayList<>();
        if(map.containsKey("skulls")) {
            Collections.addAll(skulls, (HighScoreSkull[]) map.get("skulls"));
        }
        
    }
    
}
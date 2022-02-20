package online.meinkraft.highscores.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import online.meinkraft.highscores.table.Entry;

public class HighScoreSkull extends HighScoreBlock {

    public HighScoreSkull(Location location, Integer rank) {
        super(location, rank);
    }

    @Override
    public boolean update(Entry entry) {
        Block block = location.getWorld().getBlockAt(location);
        if(block.getType() == Material.PLAYER_HEAD) {
            Skull skull = (Skull) block.getState();
            skull.setOwningPlayer(entry.getPlayer());
            skull.update();
            return true;
        }
        else {
            return false;
        }
        
    }
    
}

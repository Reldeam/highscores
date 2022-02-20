package online.meinkraft.highscores.task;

import java.io.IOException;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import online.meinkraft.highscores.HighScores;
import online.meinkraft.highscores.exception.TableNotFoundException;
import online.meinkraft.highscores.table.Table;

public class UpdatePlayerTask extends BukkitRunnable {

    private final HighScores plugin;
    private final Player player;

    public UpdatePlayerTask(HighScores plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {

        Set<String> tableNames = Table.listTables(plugin.getTableFolder());

        //TODO handle all of these exceptions
        for(String tableName : tableNames) {
            try {
                Table table = Table.getTable(plugin.getTableFolder(), tableName);
                table.updatePlayer(player);
                table.save(plugin.getTableFolder());
            }
            catch(IOException exception) {
                exception.printStackTrace();
            }
            catch(ClassNotFoundException exception) {
                exception.printStackTrace();
            }
            catch(TableNotFoundException exception) {
                exception.printStackTrace();
            }
            catch(NumberFormatException exception) {
                exception.printStackTrace();
            }
        }

    }
    
}

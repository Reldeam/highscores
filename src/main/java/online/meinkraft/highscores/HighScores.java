package online.meinkraft.highscores;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import online.meinkraft.highscores.command.HighScoresCommand;
import online.meinkraft.highscores.table.Entry;
import online.meinkraft.highscores.table.Table;
import online.meinkraft.highscores.task.UpdatePlayerTask;

public class HighScores extends JavaPlugin implements Listener {

    @Override
    public void onLoad() {

        ConfigurationSerialization.registerClass(Table.class);
        ConfigurationSerialization.registerClass(Entry.class);

    }

    @Override 
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            saveConfig();
            getCommand("highscores").setExecutor(new HighScoresCommand(this));
            getServer().getPluginManager().registerEvents(this, this);
        }
        else {
            getLogger().severe("Missing dependency: PlaceholderAPI");
        }
    }

    @Override
    public void onDisable() {}

    public File getTableFolder() {
        return new File(getDataFolder(), "tables/");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitRunnable task = new UpdatePlayerTask(this, event.getPlayer());
        task.runTaskAsynchronously(this);
    }
    
}
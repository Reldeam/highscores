package online.meinkraft.highscores.command;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import online.meinkraft.highscores.HighScores;
import online.meinkraft.highscores.exception.PlayerNotFoundException;
import online.meinkraft.highscores.exception.TableNotFoundException;
import online.meinkraft.highscores.table.Table;

public class HighScoresCommand implements CommandExecutor{

    private final HighScores plugin;

    public HighScoresCommand(HighScores plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
         
        String action, tableName, placeholder, playerName;
        Integer rank;

        if(args.length < 1) action = "help";
        else action = args[0].toLowerCase();

        //TODO Lots of checks and catches needed for input parameters
        switch(action) {
            case "enable":
                enablePlugin(sender);
                break;
            case "disable":
                disablePlugin(sender);
                break;
            case "create":
            case "new":
            case "add":
            case "set":
                tableName = args[1];
                placeholder = args[2];
                createTable(sender, tableName, placeholder);
                break;
            case "get":
                tableName = args[1];
                if(args.length == 2) {
                    getTable(sender, args[1]);
                }
                else if(args.length == 3) {
                    if(isDouble(args[2])) {
                        rank = Integer.parseInt(args[2]);
                        getEntry(sender, tableName, rank);
                    }
                    else {
                        playerName = args[2];
                        getEntry(sender, tableName, playerName);
                    }
                }
                else {
                    //TODO smooth out the message a bit
                    sender.sendMessage("You fucked the get command");
                }
                break;
            case "destroy":
            case "delete":
            case "remove":
                tableName = args[1];
                destroyTable(sender, tableName);
                break;
            case "list":
                listTables(sender);
                break;
            case "sign":
                tableName = args[1];
                rank = Integer.parseInt(args[2]);
                addSign(sender, tableName, rank);
                break;
            case "head":
                tableName = args[1];
                rank = Integer.parseInt(args[2]);
                addHead(sender, tableName, rank);
                break;
            case "help":
            default:
                help(sender);
                break;
        }

        return true;

    }

    private void enablePlugin(CommandSender sender) {
        plugin.onEnable();
        sender.sendMessage("Plugin enabled");
    }

    private void disablePlugin(CommandSender sender) {
        plugin.onDisable();
        sender.sendMessage("Plugin disabled");
    }

    private void createTable(CommandSender sender, String tableName, String placeholder) {
        try {
            Table table = new Table(tableName, placeholder);
            table.save(plugin.getTableFolder());
            sender.sendMessage("Table created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void destroyTable(CommandSender sender, String tableName) {
        try {
            Table table = Table.getTable(plugin.getTableFolder(), tableName);
            table.delete(plugin.getTableFolder());
            sender.sendMessage("Table destroyed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (TableNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getTable(CommandSender sender, String tableName) {
        try {
            Table table = Table.getTable(plugin.getTableFolder(), tableName);
            sender.sendMessage(table.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TableNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void listTables(CommandSender sender) {
        sender.sendMessage(Table.listTables(plugin.getTableFolder()).toString());
    }

    private void addSign(CommandSender sender,  String tableName, Integer rank) {
        sender.sendMessage("Sign added");
    }

    private void addHead(CommandSender sender, String tableName, Integer rank) {
        sender.sendMessage("Player head added");
    }

    private void help(CommandSender sender) {
        sender.sendMessage("I believe in you");
    }

    private void getEntry(CommandSender sender, String tableName, Integer rank) {
        Table table;
        try {
            table = Table.getTable(plugin.getTableFolder(), tableName);
            sender.sendMessage(table.getEntry(rank).toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TableNotFoundException e) {
            e.printStackTrace();
        } catch(IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void getEntry(CommandSender sender, String tableName, String playerName) {
        try {
            Player player = Bukkit.getPlayer(playerName);
            Table table = Table.getTable(plugin.getTableFolder(), tableName);
            sender.sendMessage(table.getEntry(player).toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TableNotFoundException e) {
            e.printStackTrace();
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    private boolean isDouble(String value) {
        if (value == null) {
            return false;
        }
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            return false;
        }
        return true;
    }
    
}

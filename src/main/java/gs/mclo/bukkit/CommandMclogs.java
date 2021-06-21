package gs.mclo.bukkit;

import gs.mclo.java.APIResponse;
import gs.mclo.java.MclogsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandMclogs implements CommandExecutor, TabExecutor {

    private final MclogsBukkitLoader plugin;

    private final HashMap<String, SubCommand> subCommands = new HashMap<>();

    public CommandMclogs(MclogsBukkitLoader plugin) {
        this.plugin = plugin;
        this.registerSubCommands();
    }

    private void registerSubCommands() {
        this.subCommands.put("list", new MclogsListCommand(this));
        this.subCommands.put("share", new MclogsShareCommand(this));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("mclogs.upload")) {
                //share latest.log
                plugin.getLogger().log(Level.INFO,"Sharing latest.log...");
                share(sender,"latest.log");
            }
            else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to use the command!");
            }
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) return false;
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You don't have the permissions required to execute this command.");
            return true;
        }
        return subCommand.onCommand(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
    }

    public void share(CommandSender commandSender, String file) {
        Logger logger = plugin.getLogger();
        try {
            APIResponse response = MclogsAPI.share(plugin.getRunDir() + "/logs/", file);
            if (response.success) {
                commandSender.sendMessage(ChatColor.GREEN + "Your log has been uploaded: " + ChatColor.BLUE + response.url);
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
                logger.log(Level.SEVERE,"An error occurred while uploading your log: " + response.error);
            }
        }
        catch (FileNotFoundException e) {
            commandSender.sendMessage(ChatColor.RED + "The log file " + file + " doesn't exist. Use '/mclogs list' to list all logs.");
        }
        catch (IOException e) {
            commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
            logger.log(Level.SEVERE,"An error occurred while reading your log", e);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return new ArrayList<>(subCommands.keySet());
        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) return new ArrayList<>(subCommands.keySet());
        return subCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * @return log files
     */
    public String[] listLogs() {
        return MclogsAPI.listLogs(plugin.getRunDir());
    }

    /**
     * log a message
     * @param level log level
     * @param message log message
     */
    public void log(Level level, String message) {
        plugin.getLogger().log(level, message);
    }
}

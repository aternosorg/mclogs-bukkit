package gs.mclo.bukkit;

import gs.mclo.mclogs.APIResponse;
import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandMclogs implements CommandExecutor {

    private final String runDir;
    private final Logger logger;

    public CommandMclogs(String runDir, Logger logger) {
        this.logger = logger;
        this.runDir = runDir;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) {
            if (commandSender.hasPermission("mclogs.upload")) {
                //share latest.log
                logger.log(Level.INFO,"Sharing latest.log...");
                share(commandSender,"latest.log");
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You don't have the permission to use the command!");
            }
            return true;
        }
        else if (args.length == 1 && args[0].equals("list")) {
            //list logs

            if (commandSender.hasPermission("mclogs.list")) {
                String[] logs = MclogsAPI.listLogs(runDir);

                if (logs.length == 0) {
                    commandSender.sendMessage("No logs available!");
                    return true;
                }

                commandSender.sendMessage(ChatColor.GREEN + "Available logs:");

                if (commandSender.getName().equals("CONSOLE")) {
                    commandSender.sendMessage(logs);
                } else {
                    String base = "tellraw " + commandSender.getName();
                    for (String log : logs) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), base + " {\"text\":\"" + log + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mclogs share " + log + "\"}}");
                    }
                }
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You don't have the permission to use the command!");
            }
            return true;
        }
        else if (args.length == 2 && args[0].equals("share")) {
            //share args[1]
            if (commandSender.hasPermission("mclogs.share")) {
                if (args[1].contains("../") || !args[1].endsWith(".log") && !args[1].endsWith(".log.gz")) {
                    return false;
                }
                logger.log(Level.INFO, "Sharing " + args[1] + "...");
                share(commandSender, args[1]);
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You don't have the permission to use the command!");
            }
            return true;
        }
        else {
            //unknown command
            return false;
        }
    }

    private void share(CommandSender commandSender, String file) {
        try {
            APIResponse response = MclogsAPI.share(runDir + "/logs/" + file);
            if (response.success) {
                commandSender.sendMessage(ChatColor.GREEN + "Your log has been uploaded: " + ChatColor.BLUE + response.url);
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
                logger.log(Level.SEVERE,"An error occurred while uploading your log", response.error);
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
}

package gs.mclo.bukkit;

import gs.mclo.mclogs.APIResponse;
import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandMclogs implements CommandExecutor {

    private final String runDir;

    public CommandMclogs(String dataPath) {
        runDir = getRunDir(dataPath);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Logger logger = commandSender.getServer().getLogger();
        if (args.length == 0) {
            //share latest.log
            logger.log(Level.INFO,"Sharing latest.log...");
            share(commandSender,runDir +"/logs/latest.log");
            return true;
        }
        else if (args.length == 1 && args[0].equals("list")) {
            //list logs
            String[] logs = MclogsAPI.listLogs(runDir);

            commandSender.sendMessage(ChatColor.GREEN + "Available logs:");

            if (commandSender.getName().equals("CONSOLE")) {
                commandSender.sendMessage(logs);
            }
            else {
                String base = "tellraw " + commandSender.getName();
                for (String log : logs) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), base + " {\"text\":\"" + log + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/mclogs share " + log + "\"}}");
                }
            }
            return true;
        }
        else if (args.length == 2 && args[0].equals("share")) {
            //share args[1]
            if (args[1].contains("../") || !args[1].endsWith(".log") && !args[1].endsWith(".log.gz") ) {
                return false;
            }
            logger.log(Level.INFO,"Sharing "+args[1]+"...");
            share(commandSender,runDir +"/logs/"+args[1]);
            return true;
        }
        else {
            //unknown command
            return false;
        }
    }

    private void share(CommandSender commandSender, String file) {

        Logger logger = commandSender.getServer().getLogger();
        try {
            APIResponse response = MclogsAPI.share(file);
            if (response.success) {
                commandSender.sendMessage(ChatColor.GREEN + "Your log has been shared: " + ChatColor.BLUE + response.url);
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
                logger.log(Level.SEVERE,"An error occurred when sharing your log");
                logger.log(Level.SEVERE, response.error);
            }
        } catch (IOException e) {
            commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
            logger.log(Level.SEVERE,"An error occurred when reading your log");
            e.printStackTrace();
        }
    }

    private String getRunDir(String dataPath) {
        String[] dataDir = dataPath.split("/");
        String[] baseDir = new String[dataDir.length - 2];
        if (dataDir.length - 2 >= 0) System.arraycopy(dataDir, 0, baseDir, 0, dataDir.length - 2);
        return String.join("/",baseDir);
    }
}

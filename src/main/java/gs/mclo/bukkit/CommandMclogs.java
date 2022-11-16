package gs.mclo.bukkit;

import gs.mclo.java.APIResponse;
import gs.mclo.java.Log;
import gs.mclo.java.MclogsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandMclogs implements CommandExecutor, TabExecutor {

    public final MclogsBukkitLoader plugin;

    public final HashMap<String, SubCommand> subCommands = new HashMap<>();

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
        logger.log(Level.INFO, "Sharing " + file + "...");

        Path directory = Paths.get(plugin.getRunDir());
        Path logs = directory.resolve("logs");
        Path crashReports = directory.resolve("crash-reports");

        Path log = logs.resolve(file);
        if (!log.toFile().exists()) {
            log = crashReports.resolve(file);
        }

        boolean isInAllowedDirectory = false;
        try {
            Path logPath = log.toRealPath();
            isInAllowedDirectory = (logs.toFile().exists() && logPath.startsWith(logs.toRealPath()))
                    || (crashReports.toFile().exists() && logPath.startsWith(crashReports.toRealPath()));
        }
        catch (IOException ignored) {}

        if (!log.toFile().exists() || !isInAllowedDirectory
                || !log.getFileName().toString().matches(Log.ALLOWED_FILE_NAME_PATTERN.pattern())) {
            commandSender.sendMessage(ChatColor.RED + "There is no log or crash report with the name '" + file
                    + "'. Use '/mclogs list' to list all logs.");
            return;
        }

        try {
            APIResponse response = MclogsAPI.share(log);
            if (response.success) {
                commandSender.sendMessage(ChatColor.GREEN + "Your log has been uploaded: " + ChatColor.BLUE + this.getMclogsURL(response.id));
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
                logger.log(Level.SEVERE,"An error occurred while uploading your log: " + response.error);
            }
        }
        catch (IOException e) {
            commandSender.sendMessage(ChatColor.RED + "An error occurred. Check your log for more details");
            logger.log(Level.SEVERE,"An error occurred while reading your log", e);
        }
    }

    public String getMclogsURL(String id) {
        String protocol = plugin.getConfig().get("protocol", "https").toString();
        String host = plugin.getConfig().get("host", "mclo.gs").toString();
        return protocol + "://" + host + "/" + id;
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
     * @return crash reports
     */
    public String[] listCrashReports() {
        return MclogsAPI.listCrashReports(plugin.getRunDir());
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

package gs.mclo.bukkit;

import gs.mclo.api.Log;
import gs.mclo.api.response.UploadLogResponse;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandMclogs implements CommandExecutor, TabExecutor {

    public final MclogsPlugin plugin;

    public final HashMap<String, SubCommand> subCommands = new HashMap<>();


    public CommandMclogs(MclogsPlugin plugin) {
        this.plugin = plugin;
        this.registerSubCommands();
    }

    private void registerSubCommands() {
        this.subCommands.put("list", new MclogsListCommand(this));
        this.subCommands.put("share", new MclogsShareCommand(this));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("mclogs.upload")) {
                share(sender,"latest.log");
            }
            else {
                plugin.adventure().sender(sender).sendMessage(Component
                        .text("You don't have the permissions to use the command!")
                        .color(NamedTextColor.RED)
                );
            }
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) return false;
        if (subCommand.getPermission() != null && !sender.hasPermission(subCommand.getPermission())) {
            plugin.adventure().sender(sender).sendMessage(Component
                    .text("You don't have the permissions to use the command!")
                    .color(NamedTextColor.RED)
            );
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
            plugin.adventure().sender(commandSender).sendMessage(Component
                    .text("There is no log or crash report with the name '" + file + "'. Use '/mclogs list' to list all logs.")
                    .color(NamedTextColor.RED)
            );
            return;
        }

        try {
            CompletableFuture<UploadLogResponse> response = plugin.getMclogsClient().uploadLog(log);
            response.whenComplete((res, ex) -> {
                if(ex != null) {
                    plugin.adventure().sender(commandSender).sendMessage(Component
                            .text("An error occurred. Check your log for more details.")
                            .color(NamedTextColor.RED)
                    );
                    logger.log(Level.SEVERE,"An error occurred while reading your log", ex);
                    return;
                }
                res.setClient(plugin.getMclogsClient());
                plugin.adventure().sender(commandSender).sendMessage(Component
                        .text("Your log has been uploadded:")
                        .color(NamedTextColor.GREEN)
                        .appendSpace()
                        .append(Component.text(res.getUrl())
                                .clickEvent(ClickEvent.openUrl(res.getUrl()))
                                .color(NamedTextColor.AQUA)));

            });
        }
        catch (IOException e) {
            plugin.adventure().sender(commandSender).sendMessage(Component
                    .text("An error occurred. Check your log for more details.")
                    .color(NamedTextColor.RED)
            );
            logger.log(Level.SEVERE,"An error occurred while reading your log", e);
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 0) return new ArrayList<>(subCommands.keySet());
        SubCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) return new ArrayList<>(subCommands.keySet());
        return subCommand.onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
    }

    /**
     * @return log files
     */
    public String[] listLogs() {
        return plugin.getMclogsClient().listLogsInDirectory(plugin.getRunDir());
    }

    /**
     * @return crash reports
     */
    public String[] listCrashReports() {
        return plugin.getMclogsClient().listCrashReportsInDirectory(plugin.getRunDir());
    }
}

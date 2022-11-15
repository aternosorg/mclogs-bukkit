package gs.mclo.bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class MclogsListCommand extends SubCommand {

    private final CommandMclogs mclogs;

    public MclogsListCommand(CommandMclogs mclogs) {
        this.mclogs = mclogs;
    }

    @Override
    String getPermission() {
        return "mclogs.list";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        TextComponent message = new TextComponent();

        TextComponent logsHeader = new TextComponent("Available logs:");
        logsHeader.setBold(true);
        logsHeader.setColor(ChatColor.GREEN);
        message.addExtra(logsHeader);
        for (String log : mclogs.listLogs()) {
            TextComponent entry = new TextComponent("\n" + log);
            entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mclogs share " + log));
            entry.setBold(false);
            message.addExtra(entry);
        }

        TextComponent crashReportsHeader = new TextComponent("\nAvailable crash reports:");
        crashReportsHeader.setBold(true);
        crashReportsHeader.setColor(ChatColor.GREEN);
        message.addExtra(crashReportsHeader);
        for (String crashReport : mclogs.listCrashReports()) {
            TextComponent entry = new TextComponent("\n" + crashReport);
            entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mclogs share " + crashReport));
            entry.setBold(false);
            message.addExtra(entry);
        }

        sender.spigot().sendMessage(message);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}

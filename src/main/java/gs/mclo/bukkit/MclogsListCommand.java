package gs.mclo.bukkit;

import gs.mclo.java.MclogsAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        String[] logs = mclogs.listLogs();

        if (logs.length == 0) {
            sender.sendMessage(ChatColor.RED + "No logs available!");
            return true;
        }

        TextComponent message = new TextComponent(ChatColor.GREEN + "Available logs:");
        message.setBold(true);
        for (int i = 0; i < logs.length; i++) {
            TextComponent entry = new TextComponent(logs[i] + (i == logs.length - 1 ? "" : "\n"));
            entry.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mclogs share " + logs[i]));
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

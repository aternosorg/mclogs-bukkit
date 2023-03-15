package gs.mclo.bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        mclogs.plugin.adventure().sender(sender).sendMessage(Component.empty()
                .append(generateList("logs", mclogs.listLogs()))
                .appendNewline()
                .appendNewline()
                .append(generateList("crash-reports", mclogs.listCrashReports()))
        );
        return true;
    }

    protected @NotNull Component generateList(String name, String[] entries) {
        if (entries.length == 0) {
            return Component.text("No " + name + " available.");
        }
        Component list = Component.empty().append(Component
                .text("Available " + name + ":")
                .decorate(TextDecoration.UNDERLINED));
        for (String log : entries) {
            list = list.appendNewline().append(Component
                    .text(log)
                    .clickEvent(ClickEvent.runCommand("/mclogs share " + log))
            );
        }

        return list;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return new ArrayList<>();
    }
}

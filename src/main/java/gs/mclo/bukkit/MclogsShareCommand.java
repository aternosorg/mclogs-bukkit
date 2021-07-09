package gs.mclo.bukkit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MclogsShareCommand extends SubCommand {

    private final CommandMclogs mclogs;

    public MclogsShareCommand(CommandMclogs mclogs) {
        this.mclogs = mclogs;
    }

    @Override
    String getPermission() {
        return "mclogs.list";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        mclogs.log(Level.INFO, "Sharing " + args[0] + "...");
        mclogs.share(sender, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] logs = mclogs.listLogs();
        if (args.length == 0) return Arrays.asList(logs);

        return Arrays
                .stream(logs)
                .filter(log -> log.startsWith(args[0]))
                .collect(Collectors.toList());
    }
}

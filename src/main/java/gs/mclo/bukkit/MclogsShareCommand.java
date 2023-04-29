package gs.mclo.bukkit;

import com.google.common.collect.Streams;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MclogsShareCommand extends SubCommand {

    private final CommandMclogs mclogs;

    public MclogsShareCommand(CommandMclogs mclogs) {
        this.mclogs = mclogs;
    }

    @Override
    String getPermission() {
        return "mcn.share";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;
        mclogs.share(sender, args[0]);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] logs = mclogs.listLogs();
        String[] crashReports = mclogs.listCrashReports();
        Stream<String> suggestions = Streams.concat(Arrays.stream(logs), Arrays.stream(crashReports));

        if (args.length > 0) {
            suggestions = suggestions.filter(log -> log.startsWith(args[0]));
        }

        return suggestions.collect(Collectors.toList());
    }
}

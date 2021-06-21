package gs.mclo.bukkit;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabExecutor;


public abstract class SubCommand implements CommandExecutor, TabExecutor {
    /**
     * @return required permission or null
     */
    abstract String getPermission();
}

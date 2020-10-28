package gs.mclo.bukkit;

import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MclogsBukkitLoader extends JavaPlugin{
    private Logger logger;
    @Override
    public void onEnable() {
        logger = this.getLogger();

        MclogsAPI.mcversion = getServer().getVersion();
        MclogsAPI.userAgent = "Mclogs-bukkit";
        MclogsAPI.version = getDescription().getVersion();

        this.getCommand("mclogs").setExecutor(new CommandMclogs(getDataFolder().getAbsolutePath(), logger));
    }

    @Override
    public void onDisable() {
        //when disabling
    }

}

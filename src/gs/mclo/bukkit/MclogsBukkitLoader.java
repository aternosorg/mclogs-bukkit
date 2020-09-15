package gs.mclo.bukkit;

import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class MclogsBukkitLoader extends JavaPlugin{
    @Override
    public void onEnable() {
        MclogsAPI.mcversion = getServer().getVersion();
        MclogsAPI.userAgent = "Mclogs-bukkit";
        MclogsAPI.version = getDescription().getVersion();

        this.getCommand("mclogs").setExecutor(new CommandMclogs(getDataFolder().getAbsolutePath()));
    }

    @Override
    public void onDisable() {
        //when disabling
    }

}

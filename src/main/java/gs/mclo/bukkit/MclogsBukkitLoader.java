package gs.mclo.bukkit;

import gs.mclo.java.MclogsAPI;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MclogsBukkitLoader extends JavaPlugin {

    private String rundir;

    @Override
    public void onEnable() {
        Logger logger = this.getLogger();
        Configuration config = this.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        MclogsAPI.mcversion = getServer().getVersion();
        MclogsAPI.userAgent = "Mclogs-bukkit";
        MclogsAPI.version = getDescription().getVersion();
        MclogsAPI.setApiHost(config.getString("api-host"));
        MclogsAPI.setProtocol(config.getString("protocol"));

        try {
            rundir = loadRunDir();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE,"Unable to find server directory!", e);
            return;
        }
        this.getCommand("mclogs").setExecutor(new CommandMclogs(this));
    }

    private String loadRunDir() {
        File pluginsDir = getDataFolder().getParentFile().getAbsoluteFile();
        File runDir = pluginsDir.getParentFile().getAbsoluteFile();
        return runDir.getAbsolutePath();
    }

    public String getRunDir() {
        return this.rundir;
    }

}

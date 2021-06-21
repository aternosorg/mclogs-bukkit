package gs.mclo.bukkit;

import gs.mclo.java.MclogsAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MclogsBukkitLoader extends JavaPlugin {

    private String rundir;

    @Override
    public void onEnable() {
        Logger logger = this.getLogger();

        MclogsAPI.mcversion = getServer().getVersion();
        MclogsAPI.userAgent = "Mclogs-bukkit";
        MclogsAPI.version = getDescription().getVersion();

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

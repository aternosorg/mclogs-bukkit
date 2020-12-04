package gs.mclo.bukkit;

import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MclogsBukkitLoader extends JavaPlugin{
    @Override
    public void onEnable() {
        Logger logger = this.getLogger();

        MclogsAPI.mcversion = getServer().getVersion();
        MclogsAPI.userAgent = "Mclogs-bukkit";
        MclogsAPI.version = getDescription().getVersion();

        String runDir = null;
        try {
            runDir = getRunDir();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE,"Unable to find server directory!", e);
        }
        if (runDir != null)
            this.getCommand("mclogs").setExecutor(new CommandMclogs(runDir, logger));
    }

    @Override
    public void onDisable() {
        //when disabling
    }

    private String getRunDir() {
        File pluginsDir = getDataFolder().getParentFile().getAbsoluteFile();
        File runDir = pluginsDir.getParentFile().getAbsoluteFile();
        return runDir.getAbsolutePath();
    }

}

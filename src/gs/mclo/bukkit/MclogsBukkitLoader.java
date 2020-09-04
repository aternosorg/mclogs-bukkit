package gs.mclo.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class MclogsBukkitLoader extends JavaPlugin{
    @Override
    public void onEnable() {
        this.getCommand("mclogs").setExecutor(new CommandMclogs(getDataFolder().getAbsolutePath()));
        //when enabling
    }

    @Override
    public void onDisable() {
        //when disabling
    }

}

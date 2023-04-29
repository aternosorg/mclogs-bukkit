package gs.mclo.bukkit;

import gs.mclo.api.Instance;
import gs.mclo.api.MclogsClient;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MclogsPlugin extends JavaPlugin {

    protected String rundir;

    protected MclogsClient mclogsClient = null;

    protected BukkitAudiences adventure;

    @Override
    public void onEnable() {
        // load config
        Logger logger = this.getLogger();

        this.mclogsClient = getMclogsClient();
        this.adventure = BukkitAudiences.create(this);

        Configuration config = this.getConfig();
        config.options().copyDefaults(true);
        saveConfig();

        try {
            this.rundir = loadRunDir();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Nelze najít adresář serveru!", e);
            return;
        }
        this.getCommand("log").setExecutor(new CommandMclogs(this));
    }

    @Override
    public void onDisable() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        this.mclogsClient = null;
    }

    /**
     * @return get the adventure adapter
     */
    public @NotNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Pokusil se o přístup k Adventure, když byl plugin zakázán!");
        }
        return this.adventure;
    }

    /**
     * @return get the configured mclogs client
     */
    protected @NotNull MclogsClient getMclogsClient() {
        if (mclogsClient != null) {
            return mclogsClient;
        }

        String pluginVersion = getDescription().getVersion();
        String serverVersion = getServer().getVersion();
        mclogsClient = new MclogsClient("mclogs-bukkit", pluginVersion, serverVersion);
        Configuration config = this.getConfig();
        Instance instance = new Instance();
        String apiBaseUrl = config.getString("api-base-url");
        if (!config.isSet("api-base-url") && config.isSet("api-host")) {
            apiBaseUrl = "https://api.mcnavody.eu";
            config.set("api-base-url", apiBaseUrl);
            config.set("api-host", null);
            config.set("protocol", null);
            saveConfig();
        }

        if (apiBaseUrl != null) {
            instance.setApiBaseUrl(apiBaseUrl);
        }

        String viewLogsUrl = config.getString("view-logs-url");
        if (!config.isSet("view-logs-url") && config.isSet("host")) {
            String protocol = config.getString("protocol");
            if (protocol == null || !protocol.matches("(?i)https?")) {
                protocol = "https";
            }
            viewLogsUrl = protocol + "://" + config.getString("host");
            config.set("view-logs-url", viewLogsUrl);
            config.set("host", null);
            config.set("protocol", null);
            saveConfig();
        }

        if (viewLogsUrl != null) {
            instance.setViewLogUrl(viewLogsUrl);
        }

        mclogsClient.setInstance(instance);
        return mclogsClient;
    }

    protected String loadRunDir() {
        File pluginsDir = getDataFolder().getParentFile().getAbsoluteFile();
        File runDir = pluginsDir.getParentFile().getAbsoluteFile();
        return runDir.getAbsolutePath();
    }

    public String getRunDir() {
        return this.rundir;
    }
}

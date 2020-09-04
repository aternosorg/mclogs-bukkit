package gs.mclo.bukkit;

import gs.mclo.mclogs.APIResponse;
import gs.mclo.mclogs.MclogsAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

public class CommandMclogs implements CommandExecutor {

    private final String runDir;

    public CommandMclogs(String dataPath) {
        runDir = getRunDir(dataPath);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Logger logger = commandSender.getServer().getLogger();
        if (args.length == 0) {
            //share latest.log
            logger.log(Level.INFO,"Sharing latest.log...");
            File log = new File(runDir +"/logs/latest.log");
            share(commandSender,log);
            return true;
        }
        else if (args.length == 1 && args[0].equals("list")) {
            String[] logs = MclogsAPI.listLogs(runDir);
            commandSender.sendMessage("Available logs: \n" + String.join(",\n",logs));
            //list logs
            return true;
        }
        else if (args.length == 2 && args[0].equals("share")) {
            //share args[1]
            if (args[1].contains("../") || !args[1].endsWith(".log") && !args[1].endsWith(".log.gz") ) {
                return false;
            }
            logger.log(Level.INFO,"Sharing "+args[1]+"...");
            File log = new File(runDir +"/logs/"+args[1]);
            share(commandSender,log);
            return true;
        }
        else {
            //unknown command
            return false;
        }
    }

    private void share(CommandSender commandSender, File f) {

        Logger logger = commandSender.getServer().getLogger();
        try {
            InputStream is = new FileInputStream(f);
            if (f.getName().endsWith(".gz")) {
                is = new GZIPInputStream(is);
            }
            APIResponse response = MclogsAPI.shareLog(is);
            if (response.success) {
                commandSender.sendMessage("Your log has been shared: "+response.url);
            }
            else {
                logger.log(Level.SEVERE,"An error occurred when sharing your log");
                logger.log(Level.SEVERE, response.error);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE,"An error occurred when sharing your log");
            e.printStackTrace();
        }
    }

    private String getRunDir(String dataPath) {
        String[] dataDir = dataPath.split("/");
        String[] baseDir = new String[dataDir.length - 2];
        for (int i = 0; i < dataDir.length - 2; i++) {
            baseDir[i] = dataDir[i];
        }
        return String.join("/",baseDir);
    }
}

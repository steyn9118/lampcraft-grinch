package steyn91.grinchplugin;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import steyn91.grinchplugin.Commands.AdminCommands;
import steyn91.grinchplugin.Commands.PlayerCommands;
import steyn91.grinchplugin.Listeners.PlayerListener;
import steyn91.grinchplugin.Stats.Database;
import steyn91.grinchplugin.Stats.PlaceholderManager;
import steyn91.grinchplugin.Stats.StatsManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class GrinchPlugin extends JavaPlugin {

    private static final List<Arena> arenas = new ArrayList<>();
    private static GrinchPlugin plugin;

    private Song song;
    public Song getSong(){
        return song;
    }

    public static GrinchPlugin getPlugin() {
        return plugin;
    }
    public List<Arena> getArenas(){
        return arenas;
    }

    @Override
    public void onEnable() {

        plugin = this;

        Bukkit.getServer().getPluginManager().registerEvents(new Utils(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("grinch").setExecutor(new PlayerCommands());
        getCommand("gradmin").setExecutor(new AdminCommands());
        new PlaceholderManager().register();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        song = NBSDecoder.parse(new File(GrinchPlugin.getPlugin().getDataFolder() + "/song.nbs"));

        loadArenas(false);

        Database.initDatabase();
        StatsManager.startSavingCycle();
    }

    @Override
    public void onDisable(){
        StatsManager.saveAllToDB();
    }

    public static void loadArenas(boolean shouldReload){

        if (shouldReload){
            for (Arena arena : arenas){
                arena.forceStop();
            }
            arenas.clear();
        }

        File arenasFolder = new File(GrinchPlugin.getPlugin().getDataFolder() + "/arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdir();
        }

        File[] arenasFiles = arenasFolder.listFiles();

        assert arenasFiles != null;
        for (File file : arenasFiles) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            arenas.add(new Arena(
                    config.getInt("id"),
                    config.getInt("spawnSafeRadius"),
                    config.getInt("minPlayers"),
                    config.getInt("maxPlayers"),
                    (List<Location>) config.getList("presentsLocations"),
                    config.getInt("preGameWaitingTime"),
                    config.getInt("gameMaxTime"),
                    config.getLocation("lobbyLocation"),
                    config.getLocation("startLocation")
            ));
        }
    }

}

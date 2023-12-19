package steyn91.grinchplugin;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.*;

public class Utils implements Listener {

    // Metadata: "playingGrinch"(true/null)

    private static final GrinchPlugin plugin = GrinchPlugin.getPlugin();
    private static HeadDatabaseAPI headAPI;

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent e) {
        headAPI = new HeadDatabaseAPI();
    }

    private static final List<String> presentSkins = new ArrayList<>();{
        presentSkins.add("23994");
        presentSkins.add("24021");
        presentSkins.add("24048");
    }

    private static String getRandomPresentSkin(){
        return presentSkins.get(new Random().nextInt(presentSkins.size()));
    }

    public static void clearBlocks(List<Location> locations){
        for (Location location : locations){
            location.getBlock().setType(Material.AIR);
        }
    }

    public static int fillWithPresents(List<Location> locations, int percentage){
        Collections.shuffle(locations);
        List<Location> randomLocations = locations.subList(0, Math.round(locations.size() * ((float)percentage / 100)));
        for (Location presentLocation : randomLocations){
            Bukkit.getWorld("world").getBlockAt(presentLocation).setType(Material.PLAYER_HEAD);
            headAPI.setBlockSkin(Bukkit.getWorld("world").getBlockAt(presentLocation), getRandomPresentSkin());
        }
        return randomLocations.size();
    }

    public static Arena getArenaOfPlayer(Player player){
        if (player.getMetadata("playingGrinch").size() == 0) return null;
        for (Arena arena : plugin.getArenas()){
            if (arena.getPlayers().contains(player)) return arena;
        }
        return null;
    }

    public static Arena getArenaByID(int id){
        for (Arena arena : plugin.getArenas()){
            if (arena.getId() == id){
                return arena;
            }
        }
        return null;
    }

    public static double findDistance(Location loc1, Location loc2){
        return (Math.sqrt(
                          Math.pow((loc1.getBlockX() - loc2.getBlockX()), 2)
                          + Math.pow((loc1.getBlockY() - loc2.getBlockY()), 2)
                          + Math.pow((loc1.getBlockZ() - loc2.getBlockZ()), 2)
                          )
                );
    }

}

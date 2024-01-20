package steyn91.grinchplugin.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import steyn91.grinchplugin.Arena;
import steyn91.grinchplugin.GrinchPlugin;
import steyn91.grinchplugin.Utils;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event){
        // Не даёт уйти от спавна пока игра не началась
        Player player = event.getPlayer();
        Arena arena = Utils.getArenaOfPlayer(player);
        if (arena == null) return;
        if (!arena.isGameActive() && Utils.findDistance(arena.getStartLocation(), player.getLocation()) > arena.getSpawnSafeRadius()){
            player.teleport(arena.getStartLocation().setDirection(player.getLocation().getDirection()));
        }
    }

    @EventHandler
    public void playerRightClickBlock(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Arena arena = Utils.getArenaOfPlayer(player);
        if (arena == null) return;
        boolean clickedBlockIsHead = event.getClickedBlock().getBlockData().getMaterial().equals(Material.PLAYER_HEAD);
        if (!event.hasBlock() || !clickedBlockIsHead || !event.getAction().isRightClick()) return;
        arena.collectPresent(event.getClickedBlock().getLocation(), player);
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Arena arena = Utils.getArenaOfPlayer(player);
        if (arena != null) arena.leave(player);
    }
}

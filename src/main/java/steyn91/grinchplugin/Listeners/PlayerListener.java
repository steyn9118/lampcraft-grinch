package steyn91.grinchplugin.Listeners;

import org.bukkit.Material;
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
        if (Utils.getArenaOfPlayer(event.getPlayer()) == null) return;
        Arena arena = Utils.getArenaOfPlayer(event.getPlayer());
        if (!arena.isGameActive() && Utils.findDistance(arena.getStartLocation(), event.getPlayer().getLocation()) > 3){
            event.getPlayer().teleport(arena.getStartLocation());
        }
    }

    @EventHandler
    public void playerRightClickBlock(PlayerInteractEvent event){
        if (!event.hasBlock() || !event.getClickedBlock().getBlockData().getMaterial().equals(Material.PLAYER_HEAD) || !event.getAction().isRightClick()) return;
        if (Utils.getArenaOfPlayer(event.getPlayer()) != null) Utils.getArenaOfPlayer(event.getPlayer()).collectPresent(event.getClickedBlock().getLocation(), event.getPlayer());
    }

    @EventHandler
    public void playerLeaveEvent(PlayerQuitEvent event){
        if (Utils.getArenaOfPlayer(event.getPlayer()) != null) Utils.getArenaOfPlayer(event.getPlayer()).leave(event.getPlayer());
    }

}

package steyn91.grinchplugin;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import steyn91.grinchplugin.TopUtils.Top;

public class Arena {

    // Из конфига
    private final int id;
    private final int minPlayers;
    private final int maxPlayers;
    private final List<Location> presentsLocations;
    private final int preGameWaitingTime;
    private final int gameMaxTime;
    private final Location lobbyLocation;
    private final Location startLocation;

    // Динамические
    private final List<Player> players = new ArrayList<>();
    private final Top top = new Top();
    private boolean isGameActive = false;
    private boolean preGameCountdownActive = false;
    private int presentsAmount;
    private int presentsCollected;
    private boolean foreStop = false;

    // Технические
    GrinchPlugin plugin = GrinchPlugin.getPlugin();
    World world = Bukkit.getWorld("world");
    TextComponent joinMessage = Component.text("Ты присоединился к игре").color(NamedTextColor.GRAY);
    TextComponent leaveMessage = Component.text("Ты покинул игру").color(NamedTextColor.GRAY);
    TextComponent winMessage = Component.text("Ты победил!").color(NamedTextColor.GREEN);
    TextComponent loseMessage = Component.text("Ты проиграл").color(NamedTextColor.RED);
    TextComponent fullArenaMessage = Component.text("На этой арене уже нет мест!").color(NamedTextColor.RED);
    TextComponent gameActiveMessage = Component.text("На этой арене уже идёт игра!").color(NamedTextColor.RED);
    TextComponent arenaReloadMessage = Component.text("Плагин перезагружается!").color(NamedTextColor.RED);

    public int getId(){
        return id;
    }
    public List<Player> getPlayers(){
        return players;
    }
    public Location getStartLocation(){
        return startLocation;
    }
    public boolean isGameActive() {
        return isGameActive;
    }

    // Конструктор
    public Arena(int id,
                 int minPlayers,
                 int maxPlayers,
                 List<Location> presentsLocations,
                 int preGameWaitingTime,
                 int gameMaxTime,
                 Location lobbyLocation,
                 Location startLocation){

        this.id = id;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.presentsLocations = presentsLocations;
        this.preGameWaitingTime = preGameWaitingTime;
        this.gameMaxTime = gameMaxTime;
        this.lobbyLocation = lobbyLocation;
        this.startLocation = startLocation;


    }

    // Это база
    public void join(Player p){

        if (players.size() == maxPlayers){
            p.sendMessage(fullArenaMessage);
            return;
        }
        if (isGameActive){
            p.sendMessage(gameActiveMessage);
            return;
        }

        players.add(p);
        top.addEntry(p, 0);
        p.teleport(startLocation);
        p.sendMessage(joinMessage);
        p.setMetadata("playingGrinch", new FixedMetadataValue(plugin, true));

        if (players.size() >= minPlayers) startLobbyCountdown();

    }

    public void leave(Player p){

        players.remove(p);
        top.removeEntry(p);
        p.teleport(lobbyLocation);
        p.sendMessage(leaveMessage);
        p.setLevel(0);
        p.setExp(0);
        p.removeMetadata("playingGrinch", plugin);

    }

    public void forceStop(){
        for (Player player : players){
            player.sendMessage(arenaReloadMessage);
        }
        foreStop = true;
    }

    // Начало обратного отсчёта в лобби
    private void startLobbyCountdown(){

        if (preGameCountdownActive) return;

        preGameCountdownActive = true;
        BukkitRunnable preGameCountdownRunnable = new BukkitRunnable() {
            int preGameCountdown = preGameWaitingTime;
            @Override
            public void run() {
                preGameCountdown -= 1;

                if (players.size() < minPlayers){
                    preGameCountdownActive = false;
                    this.cancel();
                }

                if (preGameCountdown == 0){
                    preGameCountdownActive = false;
                    startGame();
                    this.cancel();
                }

                for (Player player : players){
                    player.setExp((float)preGameCountdown / preGameWaitingTime);
                    player.setLevel(preGameCountdown);
                }
            }
        };
        preGameCountdownRunnable.runTaskTimer(plugin, 0, 20);
    }

    private void startGame(){

        Utils.clearBlocks(presentsLocations);
        presentsAmount = Utils.fillWithPresents(presentsLocations, 50);
        presentsCollected = 0;

        isGameActive = true;
        BukkitRunnable gameTimer = new BukkitRunnable() {
            int gameCountdown = gameMaxTime;

            @Override
            public void run() {

                if (gameCountdown == 0 || players.size() < 2 || presentsCollected == presentsAmount || foreStop) {
                    stopGame();
                    this.cancel();
                }

                gameCountdown -= 1;

                for (Player player : players){
                    player.setExp((float)gameCountdown / gameMaxTime);
                    player.setLevel(gameCountdown);
                    player.sendActionBar(Component.text("Ты собрал " + top.getEntry(player).getScore() + " подарков").color(NamedTextColor.YELLOW));
                }
            }
        };
        gameTimer.runTaskTimer(plugin, 0, 20);

    }

    private void stopGame(){

        for (Player player : players){

            player.teleport(lobbyLocation);
            player.setLevel(0);
            player.setExp(0);
            player.removeMetadata("playingGrinch", plugin);
            
        }

        top.sort();
        top.informParticipants(players,winMessage, loseMessage);
        top.showTop(3, players);
        top.broadcastWinner(players);


        players.clear();
        top.clear();
        isGameActive = false;

    }

    // Связанные с игрой
    public void collectPresent(Location loc, Player player){
        plugin.getLogger().info(loc.toString());
        if (!presentsLocations.contains(loc)) return;
        world.getBlockAt(loc).setType(Material.AIR);
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 100, 1);
        top.incrementScore(player, 1);
        presentsCollected += 1;

    }

}

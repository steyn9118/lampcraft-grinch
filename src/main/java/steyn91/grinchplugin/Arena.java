package steyn91.grinchplugin;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import me.neznamy.tab.api.TabAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import steyn91.grinchplugin.Stats.StatsManager;
import steyn91.grinchplugin.TopUtils.Top;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class Arena {

    // Из конфига
    private final int id;
    private final int spawnSafeRadius;
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
    private BossBar activeBossBar;

    // Технические
    private final RadioSongPlayer radioSongPlayer;
    private final GrinchPlugin plugin = GrinchPlugin.getPlugin();
    private final World world = Bukkit.getWorld("world");
    private final TabAPI tabAPI = TabAPI.getInstance();
    private final TextComponent joinMessage = Component.text("Ты присоединился к игре").color(NamedTextColor.GRAY);
    private final TextComponent leaveMessage = Component.text("Ты покинул игру").color(NamedTextColor.GRAY);
    private final TextComponent winMessage = Component.text("Ты победил!").color(NamedTextColor.GREEN);
    private final TextComponent loseMessage = Component.text("Ты проиграл").color(NamedTextColor.RED);
    private final TextComponent fullArenaMessage = Component.text("На этой арене уже нет мест!").color(NamedTextColor.RED);
    private final TextComponent gameActiveMessage = Component.text("На этой арене уже идёт игра!").color(NamedTextColor.RED);
    private final TextComponent arenaReloadMessage = Component.text("Плагин перезагружается!").color(NamedTextColor.RED);
    private final BossBar waitingForPlayers = BossBar.bossBar(Component.text("Ждём игроков..."), 1, BossBar.Color.WHITE, BossBar.Overlay.NOTCHED_6);
    private final BossBar gameStarting = BossBar.bossBar(Component.text("Игра скоро начнётся!"), 1, BossBar.Color.GREEN, BossBar.Overlay.NOTCHED_6);

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
    public int getSpawnSafeRadius() {
        return spawnSafeRadius;
    }

    // Конструктор
    public Arena(int id,
                 int spawnSafeRadius, int minPlayers,
                 int maxPlayers,
                 List<Location> presentsLocations,
                 int preGameWaitingTime,
                 int gameMaxTime,
                 Location lobbyLocation,
                 Location startLocation){

        this.id = id;
        this.spawnSafeRadius = spawnSafeRadius;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.presentsLocations = presentsLocations;
        this.preGameWaitingTime = preGameWaitingTime;
        this.gameMaxTime = gameMaxTime;
        this.lobbyLocation = lobbyLocation;
        this.startLocation = startLocation;

        this.radioSongPlayer = new RadioSongPlayer(new Playlist(plugin.getSong()));
        this.radioSongPlayer.setCategory(com.xxmicloxx.NoteBlockAPI.model.SoundCategory.RECORDS);

        activeBossBar = waitingForPlayers;
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
        if (players.contains(p)) return;

        players.add(p);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cmi kit grinch " + p.getName());
        top.addEntry(p, 0);
        p.teleport(startLocation);
        p.sendMessage(joinMessage);
        p.setMetadata("playingGrinch", new FixedMetadataValue(plugin, true));

        radioSongPlayer.addPlayer(p);

        if (!preGameCountdownActive){
            p.showBossBar(waitingForPlayers);
        } else {
            p.showBossBar(gameStarting);
        }

        if (players.size() >= minPlayers) startLobbyCountdown();

    }

    public void leave(Player p){

        p.hideBossBar(activeBossBar);
        players.remove(p);
        top.removeEntry(p);
        p.teleport(lobbyLocation);
        p.sendMessage(leaveMessage);
        p.setLevel(0);
        p.setExp(0);
        p.getInventory().clear();
        p.removeMetadata("playingGrinch", plugin);
        tabAPI.getScoreboardManager().resetScoreboard(tabAPI.getPlayer(p.getUniqueId()));
        radioSongPlayer.removePlayer(p);

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
        for (Player player : players){
            player.hideBossBar(activeBossBar);
            player.showBossBar(gameStarting);
        }
        activeBossBar = gameStarting;

        preGameCountdownActive = true;
        BukkitRunnable preGameCountdownRunnable = new BukkitRunnable() {
            int preGameCountdown = preGameWaitingTime;
            @Override
            public void run() {
                preGameCountdown -= 1;

                if (players.size() < minPlayers){
                    preGameCountdownActive = false;
                    for (Player player : players){
                        player.hideBossBar(activeBossBar);
                        player.showBossBar(waitingForPlayers);
                    }
                    activeBossBar = waitingForPlayers;
                    this.cancel();
                }

                if (preGameCountdown == 0){
                    preGameCountdownActive = false;
                    startGame();
                    this.cancel();
                }

                for (Player player : players){
                    if (preGameCountdown <= 5 || preGameCountdown % 5 == 0){
                        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 100, 1);
                    }
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
        radioSongPlayer.setRepeatMode(RepeatMode.ALL);
        radioSongPlayer.setPlaying(true);

        for (Player player : players){
            player.hideBossBar(activeBossBar);
        }
        activeBossBar = waitingForPlayers;

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

                top.showScoreBoard(players, tabAPI, String.valueOf(id));

                for (Player player : players){
                    if (gameCountdown <= 10 || gameCountdown % 60 == 0){
                        player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 100, 1);
                    }


                    player.setExp((float)gameCountdown / gameMaxTime);
                    player.setLevel(gameCountdown);
                    player.sendActionBar(Component.text("Ты собрал " + top.getEntryByPlayer(player).getScore() + " подарков").color(NamedTextColor.YELLOW));
                }
            }
        };
        gameTimer.runTaskTimer(plugin, 0, 20);

    }

    @SuppressWarnings("ConstantConditions")
    private void stopGame(){

        for (Player player : players){

            if (player == top.getEntryByPlace(1).getPlayer()){
                StatsManager.updateWins(player.getName());
            }

            StatsManager.updateGames(player.getName());
            StatsManager.updatePresentsAll(player.getName(), top.getEntryByPlayer(player).getScore());
            StatsManager.updatePresentsOneGame(player.getName(), top.getEntryByPlayer(player).getScore());

            player.teleport(lobbyLocation);
            player.setLevel(0);
            player.setExp(0);
            player.getInventory().clear();
            player.removeMetadata("playingGrinch", plugin);
            tabAPI.getScoreboardManager().resetScoreboard(tabAPI.getPlayer(player.getUniqueId()));
            radioSongPlayer.removePlayer(player);

        }

        top.informParticipants(players, winMessage, loseMessage);
        top.showTop(3, players);
        top.broadcastWinner(players);

        players.clear();
        top.clear();
        isGameActive = false;
        radioSongPlayer.setPlaying(false);

    }

    // Связанные с игрой
    public void collectPresent(Location loc, Player player){
        if (!presentsLocations.contains(loc)) return;
        world.getBlockAt(loc).setType(Material.AIR);
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 100, 1);
        top.incrementScore(player, 1);
        presentsCollected += 1;

    }

}

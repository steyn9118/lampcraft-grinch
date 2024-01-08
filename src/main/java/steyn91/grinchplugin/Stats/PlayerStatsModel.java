package steyn91.grinchplugin.Stats;

import org.bukkit.entity.Player;

import java.util.Date;

public class PlayerStatsModel {

    private final String playerName;
    private int wins;
    private int games;
    private int presentsAll;
    private int presentsOneGame;

    private Date date;

    public PlayerStatsModel(String playerName, int wins, int games, int presentsAll, int presentsOneGame, Date date) {
        this.playerName = playerName;
        this.wins = wins;
        this.games = games;
        this.presentsAll = presentsAll;
        this.presentsOneGame = presentsOneGame;
        this.date = date;
    }

    public int getGames() {
        return games;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public int getPresentsAll() {
        return presentsAll;
    }

    public void setPresentsAll(int presentsAll) {
        this.presentsAll = presentsAll;
    }

    public int getPresentsOneGame() {
        return presentsOneGame;
    }

    public void setPresentsOneGame(int presentsOneGame) {
        this.presentsOneGame = presentsOneGame;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

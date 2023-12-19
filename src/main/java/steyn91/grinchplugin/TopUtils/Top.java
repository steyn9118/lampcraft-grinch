package steyn91.grinchplugin.TopUtils;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import me.neznamy.tab.api.scoreboard.ScoreboardManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class Top {

    private final List<TopEntry> places = new ArrayList<>();

    public void addEntry(Player player, int score){
        if (containsPlayer(player)) return;
        places.add(new TopEntry(player, score));
    }

    public void removeEntry(Player player){
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)){
                places.remove(entry);
                break;
            }
        }
    }

    public void updateEntry(Player player, int newScore){
        if (containsPlayer(player)) getEntry(player).setScore(newScore);
    }

    public void incrementScore(Player player, int increment){
        if (containsPlayer(player)) updateEntry(player, getEntry(player).getScore() + increment);
    }

    private boolean containsPlayer(Player player){
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)) return true;
        }
        return false;
    }

    public TopEntry getEntry(Player player){
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)) return entry;
        }
        return null;
    }

    public void sort(){
        Collections.sort(places);
    }

    public List<TopEntry> getPlaces() {
        return places;
    }

    public void clear(){
        places.clear();
    }

    public void showTop(int entries, List<Player> players){
        if (places.size() < entries) entries = places.size();

        for (Player player : players){
            player.sendMessage(Component.text("+----- ТОП -----+").color(NamedTextColor.YELLOW));
            for (int i = 0; i < entries; i++) {
                player.sendMessage(Component.text((i + 1) + ". " + places.get(i).getPlayer().getName() + " - " + places.get(i).getScore()).color(NamedTextColor.WHITE));
            }
            player.sendMessage(Component.text("+----- --- -----+").color(NamedTextColor.YELLOW));
        }
    }

    public void informParticipants(List<Player> players, Component winMessage, Component loseMessage){
        for (Player player : players){
            if (places.get(0).getPlayer().equals(player)) player.sendMessage(winMessage);
            else player.sendMessage(loseMessage);
        }
    }

    public void broadcastWinner(List<Player> players){
        for (Player player : players){
            player.showTitle(Title.title(Component.text("Победил " + places.get(0).getPlayer().getName()).color(NamedTextColor.GREEN), Component.text("Собрав " + places.get(0).getScore() + " подарков").color(NamedTextColor.GREEN)));
        }
    }

    public void showScoreBoard(List<Player> players, TabAPI api, String id){
        List<String> scoreBoardStrings = new ArrayList<>(places.size());
        for (TopEntry entry : places){
            scoreBoardStrings.add(entry.getPlayer().getName() + " - " + entry.getScore());
        }
        Scoreboard scoreboard = api.getScoreboardManager().createScoreboard(id, "Счёт", scoreBoardStrings);
        for (Player player : players){
                api.getScoreboardManager().showScoreboard(api.getPlayer(player.getName()), scoreboard);
        }
    }
}

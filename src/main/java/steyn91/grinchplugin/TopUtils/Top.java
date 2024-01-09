package steyn91.grinchplugin.TopUtils;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.scoreboard.Scoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
public class Top {

    private final List<TopEntry> places = new ArrayList<>();
    private boolean isSorted = false;

    public void addEntry(Player player, int score){
        if (containsPlayer(player)) return;
        places.add(new TopEntry(player, score));
        isSorted = false;
    }

    public void removeEntry(Player player){
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)){
                places.remove(entry);
                break;
            }
        }
    }

    private void updateEntry(Player player, int newScore){
        if (containsPlayer(player)){
            getEntryByPlayer(player).setScore(newScore);
            isSorted = false;
        }
    }

    public void incrementScore(Player player, int increment){
        if (containsPlayer(player)) updateEntry(player, getEntryByPlayer(player).getScore() + increment);
    }

    private boolean containsPlayer(Player player){
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)) return true;
        }
        return false;
    }

    public TopEntry getEntryByPlayer(Player player){
        if (!isSorted) sort();
        for (TopEntry entry : places){
            if (entry.getPlayer().equals(player)) return entry;
        }
        return null;
    }

    private void sort(){
        if (!isSorted){
            Collections.sort(places);
            isSorted = true;
        }
    }

    public TopEntry getEntryByPlace(int place) {
        if (!isSorted) sort();
        return places.get(place - 1);
    }

    public void clear(){
        places.clear();
    }

    public void showTop(int entries, List<Player> players){
        if (!isSorted) sort();
        if (places.size() < entries) entries = places.size();

        // Выравниваем длины строк
        int maxLength = 0;
        for (int i = 0; i < entries; i++) {
            if (maxLength < places.get(i).getPlayer().getName().length()) maxLength = places.get(i).getPlayer().getName().length();
        }

        for (Player player : players){

            player.sendMessage(Component.text("+----- ТОП -----+").color(NamedTextColor.YELLOW));
            for (int i = 0; i < entries; i++) {

                player.sendMessage(Component.text((i + 1) + ". " + places.get(i).getPlayer().getName() + " ".repeat(Math.max(0, maxLength - places.get(i).getPlayer().getName().length())) + " - " + places.get(i).getScore()).color(NamedTextColor.GRAY));
            }
            player.sendMessage(Component.text("+----- --- -----+").color(NamedTextColor.YELLOW));
        }
    }

    public void informParticipants(List<Player> players, Component winMessage, Component loseMessage){
        if (!isSorted) sort();
        for (Player player : players){
            if (places.get(0).getPlayer().equals(player)) player.sendMessage(winMessage);
            else player.sendMessage(loseMessage);
        }
    }

    public void broadcastWinner(List<Player> players){
        if (!isSorted) sort();
        for (Player player : players){
            player.showTitle(Title.title(Component.text("Победил " + places.get(0).getPlayer().getName()).color(NamedTextColor.GREEN), Component.text("Собрав " + places.get(0).getScore() + " подарков").color(NamedTextColor.GREEN)));
        }
    }

    public void showScoreBoard(List<Player> players, TabAPI api, String id){
        if (!isSorted) sort();

        // Строка с макс длинной
        int maxLength = 0;
        for (TopEntry entry : places) {
            if (maxLength < entry.getPlayer().getName().length()) maxLength = entry.getPlayer().getName().length();
        }

        List<String> scoreBoardStrings = new ArrayList<>(places.size());
        for (TopEntry entry : places){
            scoreBoardStrings.add(" " + ChatColor.WHITE + entry.getPlayer().getName() + " ".repeat(Math.max(0, maxLength - entry.getPlayer().getName().length())) + ChatColor.GRAY + " " + ChatColor.GREEN + entry.getScore());
        }

        Scoreboard scoreboard = api.getScoreboardManager().createScoreboard(id, ChatColor.YELLOW + "Счёт", scoreBoardStrings);
        for (Player player : players){
                api.getScoreboardManager().showScoreboard(api.getPlayer(player.getName()), scoreboard);
        }
    }
}

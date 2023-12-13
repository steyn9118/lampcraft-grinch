package steyn91.grinchplugin.TopUtils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopEntry implements Comparable<TopEntry> {

    private int score;
    private final Player player;

    public TopEntry(Player player, int score){
        this.score = score;
        this.player = player;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public int compareTo(@NotNull TopEntry o) {
        return o.getScore() - this.score;
    }
}

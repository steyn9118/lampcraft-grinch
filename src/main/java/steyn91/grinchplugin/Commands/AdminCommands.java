package steyn91.grinchplugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import steyn91.grinchplugin.GrinchPlugin;
import steyn91.grinchplugin.Stats.StatsManager;

public class AdminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("gradmin")){

            if (!sender.hasPermission("gradmin")) return false;

            switch (args.length){

                case (0):
                    sender.sendMessage("gradmin reload");
                    sender.sendMessage("gradmin remove <playername>");

                case (1):
                    if (args[0].equals("reload")){
                        GrinchPlugin.loadArenas(true);
                    }

                case (2):
                    if (args[0].equals("remove")){
                        StatsManager.removeStat(args[1]);
                    }
            }
            return false;
        }

        return false;
    }
}

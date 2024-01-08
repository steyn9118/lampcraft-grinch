package steyn91.grinchplugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import steyn91.grinchplugin.GrinchPlugin;
import steyn91.grinchplugin.Utils;

public class PlayerCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equals("grinch")){

            if (!sender.hasPermission("grinch")) return false;

            switch (args.length){

                case (0):
                    sender.sendMessage("grinch join <player> <arenaID>");
                    sender.sendMessage("grinch leave <player>");
                    sender.sendMessage("gradmin");

                case (2):
                    if (args[0].equals("leave")){
                        Utils.getArenaOfPlayer(Bukkit.getPlayer(args[1])).leave(Bukkit.getPlayer(args[1]));
                    }

                case (3):
                    if (args[0].equals("join")){
                        Utils.getArenaByID(Integer.parseInt(args[2])).join(Bukkit.getPlayer(args[1]));
                    }
            }
            return false;
        }

        return false;
    }
}

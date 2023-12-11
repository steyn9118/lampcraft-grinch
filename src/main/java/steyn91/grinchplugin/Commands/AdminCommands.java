package steyn91.grinchplugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import steyn91.grinchplugin.GrinchPlugin;

public class AdminCommands implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equals("gradmin")){

            if (!sender.hasPermission("gradmin")) return false;

            switch (args.length){

                case (0):
                    sender.sendMessage("gradmin reload");

                case (1):
                    if (args[0].equals("reload")){
                        GrinchPlugin.loadArenas(true);
                    }
            }
            return false;
        }

        return false;
    }
}

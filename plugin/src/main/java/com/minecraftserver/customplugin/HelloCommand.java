package com.minecraftserver.customplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelloCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage("§aHello, " + player.getName() + "! Welcome to the custom server!");
        } else {
            sender.sendMessage("This command can only be used by players!");
        }
        return true;
    }
}

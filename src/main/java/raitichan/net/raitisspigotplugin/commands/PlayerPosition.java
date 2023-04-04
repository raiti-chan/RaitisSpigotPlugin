package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerPosition implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length < 1) return false;
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            Player target = Bukkit.getPlayer(strings[0]);
            if (target == null) return false;
            Location targetLocation = target.getLocation();

            player.sendMessage(target.getDisplayName() + ":[" + player.getWorld().getEnvironment()+ "] x:" + (int)targetLocation.getX() + " y:" + (int)targetLocation.getY() + " z:" + (int)targetLocation.getZ());
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (strings.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getDisplayName)
                    .collect(Collectors.toList());
        }
        return ImmutableList.of();
    }
}

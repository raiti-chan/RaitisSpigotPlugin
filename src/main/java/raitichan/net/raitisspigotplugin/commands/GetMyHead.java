package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GetMyHead implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            NBTItem nbtItem = new NBTItem(new ItemStack(Material.PLAYER_HEAD));
            nbtItem.setString("SkullOwner", player.getDisplayName());
            player.getInventory().addItem(nbtItem.getItem());
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ImmutableList.of();
    }
}

package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import raitichan.net.raitisspigotplugin.nbtstruct.NBTPlayer;
import raitichan.net.raitisspigotplugin.nbtstruct.NBTXpBank;

import java.util.List;

public class XpBank implements TabCompleter, CommandExecutor {

    private static final List<String> SUB_COMMANDS = ImmutableList.of("show", "deposit", "withdraw", "splash");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return SUB_COMMANDS;
        }
        return ImmutableList.of();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 1) return false;
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        NBTPlayer nbtPlayer = new NBTPlayer(player);
        NBTXpBank nbtXpBank = nbtPlayer.getXpBank();

        switch (strings[0]) {
            case "show":
                return show(player, nbtXpBank);
            case "deposit":
                if (strings.length < 2) return false;
                return deposit(player, nbtXpBank, purseInt(strings[1]));
            case "withdraw":
                if (strings.length < 2) return false;
                return withdraw(player, nbtXpBank, purseInt(strings[1]));
            case "splash":
                if (strings.length < 2) return false;
                return splash(player, nbtXpBank, purseInt(strings[1]));
        }
        return false;
    }

    private static boolean show(@NotNull Player player, @NotNull NBTXpBank nbtXpBank) {
        player.sendMessage("Bank Level : " + nbtXpBank.getLevel() + " Lv");
        return true;
    }

    private static boolean deposit(@NotNull Player player, @NotNull NBTXpBank nbtXpBank, int level) {
        int playerLevel = player.getLevel();
        if (playerLevel < level) return false;
        player.giveExpLevels(-level);
        nbtXpBank.setLevel(nbtXpBank.getLevel() + level);
        return true;
    }

    private static boolean withdraw(@NotNull Player player, @NotNull NBTXpBank nbtXpBank, int level) {
        int bankLevel = nbtXpBank.getLevel();
        if (bankLevel < level) return false;
        player.giveExpLevels(level);
        nbtXpBank.setLevel(bankLevel - level);
        return true;
    }
    private static boolean splash(@NotNull Player player, @NotNull NBTXpBank nbtXpBank, int level) {
        int bankLevel = nbtXpBank.getLevel();
        if (bankLevel < level) return false;
        int xp = bankLevel >= 15 ? 37 + (bankLevel - 15) * 5 : 7 + bankLevel * 2;
        for (int i = 0; i < level; i++) {
            Entity xpOrb = player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
            NBTEntity nbtEntity = new NBTEntity(xpOrb);
            nbtEntity.setInteger("Value", xp);
        }
        nbtXpBank.setLevel(bankLevel - level);
        return true;
    }

    private static int purseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
